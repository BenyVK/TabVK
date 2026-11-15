package ir.gharri.tabVK;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public final class TabVK extends JavaPlugin implements Listener {

    record Group(String id, String perm, int weight, String prefix, String suffix, String nameColor) {}

    record Anim(int interval, List<String> frames) {}


    private YamlConfiguration cfg;
    private YamlConfiguration data;
    private File dataFile;

    private final Map<String, Anim> anims = new LinkedHashMap<>();
    private final TreeMap<Integer, ConfigurationSection> pingLevels = new TreeMap<>();
    private final Map<String, String> globals = new HashMap<>();

    private boolean papi;
    private RankHook rank;
    private long tick;
    private int hfInterval = 5, nameInterval = 10, cycle = 80;
    private ZoneId zone = ZoneId.of("UTC");
    private DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm:ss");
    private DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private BukkitTask task;

    @Override
    public void onEnable() {
        File conf = new File(getDataFolder(), "configvk.yaml");
        if (!conf.exists()) saveResource("configvk.yaml", false);
        dataFile = new File(getDataFolder(), "data.yml");
        data = YamlConfiguration.loadConfiguration(dataFile);

        reloadAll();
        getServer().getPluginManager().registerEvents(this, this);

        PluginCommand pc = getCommand("tabvk");
        if (pc != null) {
            TabCommand cmd = new TabCommand(this);
            pc.setExecutor(cmd);
            pc.setTabCompleter(cmd);
        }
        getLogger().info("TabVK v" + getPluginMeta().getVersion() + " by BenyVK enabled"
                + " | github.com/BenyVK | www.gharri.ir");
        getLogger().info("PlaceholderAPI: " + papi + ", RankVK: " + (rank != null));
        if (rank == null) getLogger().warning("RankVK was not found - ranks (prefix/suffix) will not be shown.");
    }

    @Override
    public void onDisable() {
        if (task != null) task.cancel();
        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
        for (Team t : sb.getTeams()) {
            if (t.getName().startsWith("tvk")) t.unregister();
        }
    }

    // ------------------------------------------------------------------ config

    public YamlConfiguration cfg() {
        return cfg;
    }

    public void reloadAll() {
        cfg = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "configvk.yaml"));

        papi = cfg.getBoolean("settings.use-placeholderapi", true)
                && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
        rank = cfg.getBoolean("rankvk.enabled", true) ? RankHook.create(getLogger()) : null;
        hfInterval = Math.max(1, cfg.getInt("settings.header-footer-interval", 5));
        nameInterval = Math.max(1, cfg.getInt("settings.name-interval", 10));
        cycle = Math.max(10, cfg.getInt("settings.gradient-cycle-ticks", 80));

        try {
            zone = ZoneId.of(cfg.getString("settings.timezone", "UTC"));
        } catch (Exception e) {
            zone = ZoneId.of("UTC");
        }
        try {
            timeFmt = DateTimeFormatter.ofPattern(cfg.getString("settings.time-format", "HH:mm:ss"));
            dateFmt = DateTimeFormatter.ofPattern(cfg.getString("settings.date-format", "yyyy/MM/dd"));
        } catch (Exception e) {
            getLogger().warning("Invalid time/date format in configvk.yaml");
        }

        anims.clear();
        ConfigurationSection as = cfg.getConfigurationSection("animations");
        if (as != null) {
            for (String k : as.getKeys(false)) {
                List<String> frames = as.getStringList(k + ".frames");
                if (!frames.isEmpty()) {
                    anims.put(k, new Anim(Math.max(1, as.getInt(k + ".interval", 10)), frames));
                }
            }
        }

        pingLevels.clear();
        ConfigurationSection ps = cfg.getConfigurationSection("ping-levels");
        if (ps != null) {
            for (String k : ps.getKeys(false)) {
                try {
                    ConfigurationSection s = ps.getConfigurationSection(k);
                    if (s != null) pingLevels.put(Integer.parseInt(k), s);
                } catch (NumberFormatException ignored) {
                }
            }
        }

        if (task != null) task.cancel();
        task = Bukkit.getScheduler().runTaskTimer(this, this::onTick, 1L, 1L);
    }

    // ------------------------------------------------------------------ loop

    private void onTick() {
        tick++;
        boolean doHf = tick % hfInterval == 0;
        boolean doName = tick % nameInterval == 0;
        if (!doHf && !doName) return;

        refreshGlobals();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (doHf) updateHeaderFooter(p);
            if (doName) updateName(p);
        }
    }

    public void refreshAll() {
        refreshGlobals();
        for (Player p : Bukkit.getOnlinePlayers()) updateAll(p);
    }

    public void updateAll(Player p) {
        updateHeaderFooter(p);
        updateName(p);
    }

    private void put(String key, String value) {
        globals.put("%" + key + "%", value);
    }

    private void refreshGlobals() {
        globals.clear();
        put("online", String.valueOf(Bukkit.getOnlinePlayers().size()));
        put("max", String.valueOf(Bukkit.getMaxPlayers()));

        double tps = Math.min(20.0, Bukkit.getTPS()[0]);
        put("tps", String.format(Locale.US, "%.1f", tps));
        put("tps_color", tps >= 18 ? "<green>" : tps >= 15 ? "<yellow>" : "<red>");

        Runtime rt = Runtime.getRuntime();
        put("ram_max", String.valueOf(rt.maxMemory() / 1048576L));
        put("ram_used", String.valueOf((rt.totalMemory() - rt.freeMemory()) / 1048576L));

        ZonedDateTime now = ZonedDateTime.now(zone);
        put("time", timeFmt.format(now));
        put("date", dateFmt.format(now));

        double phase = 2.0 * (tick % cycle) / cycle - 1.0; // -1 .. 1
        put("phase", String.format(Locale.US, "%.2f", phase));
        put("phase_neg", String.format(Locale.US, "%.2f", -phase));

        ConfigurationSection cp = cfg.getConfigurationSection("custom-placeholders");
        if (cp != null) {
            for (String k : cp.getKeys(false)) put(k, cp.getString(k, ""));
        }
        for (Map.Entry<String, Anim> e : anims.entrySet()) {
            Anim a = e.getValue();
            put("anim_" + e.getKey(), a.frames().get((int) ((tick / a.interval()) % a.frames().size())));
        }

        List<String> quotes = cfg.getStringList("quotes.list");
        int qi = Math.max(1, cfg.getInt("quotes.interval", 100));
        put("quote", quotes.isEmpty() ? "" : quotes.get((int) ((tick / qi) % quotes.size())));
    }

    // ------------------------------------------------------------------ resolving

    /** Rank info comes only from RankVK. Without RankVK (or before it loaded the player) there is no rank. */
    private Group groupOf(Player p) {
        String color = cfg.getString("rankvk.name-color", "&f");
        if (rank != null) {
            RankHook.Info i = rank.info(p);
            if (i != null) {
                String pre = i.prefix() != null ? i.prefix() : cfg.getString("rankvk.default-prefix", "");
                String suf = i.suffix() != null ? i.suffix() : cfg.getString("rankvk.default-suffix", "");
                if (cfg.getBoolean("rankvk.auto-space", true)) {
                    // Minecraft strips trailing spaces from commands, so add them back
                    if (!TextUtil.strip(pre).isEmpty() && !pre.endsWith(" ")) pre = pre + " ";
                    if (!TextUtil.strip(suf).isEmpty() && !suf.startsWith(" ")) suf = " " + suf;
                }
                color = cfg.getString("rankvk.group-name-colors." + i.group(), color);
                return new Group(i.group(), "", i.weight(), pre, suf, color);
            }
        }
        return new Group("none", "", 0, cfg.getString("rankvk.default-prefix", ""),
                cfg.getString("rankvk.default-suffix", ""), color);
    }

    private String prefixOf(Player p, Group g) {
        String c = data.getString("players." + p.getUniqueId() + ".prefix");
        return c != null ? c + " " : g.prefix();
    }

    private String suffixOf(Player p, Group g) {
        String c = data.getString("players." + p.getUniqueId() + ".suffix");
        return c != null ? " " + c : g.suffix();
    }

    private ConfigurationSection pingLevel(int ping) {
        Map.Entry<Integer, ConfigurationSection> e = pingLevels.ceilingEntry(ping);
        if (e == null) e = pingLevels.lastEntry();
        return e == null ? null : e.getValue();
    }

    private Map<String, String> playerVars(Player p, Group g, String prefix, String suffix) {
        Map<String, String> m = new HashMap<>();
        int ping = p.getPing();
        ConfigurationSection lvl = pingLevel(ping);
        m.put("%player%", p.getName());
        m.put("%name%", p.getName());
        m.put("%ping%", String.valueOf(ping));
        m.put("%ping_color%", lvl == null ? "&f" : lvl.getString("color", "&f"));
        m.put("%ping_bars%", lvl == null ? "" : lvl.getString("bars", ""));
        m.put("%world%", p.getWorld().getName());
        m.put("%x%", String.valueOf(p.getLocation().getBlockX()));
        m.put("%y%", String.valueOf(p.getLocation().getBlockY()));
        m.put("%z%", String.valueOf(p.getLocation().getBlockZ()));
        m.put("%health%", String.valueOf((int) Math.ceil(p.getHealth())));
        m.put("%food%", String.valueOf(p.getFoodLevel()));
        m.put("%level%", String.valueOf(p.getLevel()));
        m.put("%gamemode%", p.getGameMode().name().toLowerCase(Locale.ROOT));
        m.put("%group%", g.id());
        m.put("%name_color%", g.nameColor());
        m.put("%prefix%", prefix);
        m.put("%suffix%", suffix);
        return m;
    }

    private String resolve(Player p, String s, Map<String, String> local) {
        for (int i = 0; i < 3 && s.indexOf('%') >= 0; i++) {
            for (Map.Entry<String, String> e : local.entrySet()) s = s.replace(e.getKey(), e.getValue());
            for (Map.Entry<String, String> e : globals.entrySet()) s = s.replace(e.getKey(), e.getValue());
        }
        if (papi && s.indexOf('%') >= 0) s = PapiHook.set(p, s);
        return s;
    }

    // ------------------------------------------------------------------ updating

    private void updateName(Player p) {
        if (!cfg.getBoolean("player-list.enabled", true)) return;
        Group g = groupOf(p);
        Map<String, String> vars = playerVars(p, g, prefixOf(p, g), suffixOf(p, g));

        String format = cfg.getString("player-list.format", "%player%");
        p.playerListName(TextUtil.parse(resolve(p, format, vars)));

        if (cfg.getBoolean("settings.sort-by-group", true)) {
            p.setPlayerListOrder(g.weight());
        }
        if (cfg.getBoolean("nametag.enabled", false)) {
            updateNametag(p, g, vars);
        }
    }

    private void updateNametag(Player p, Group g, Map<String, String> vars) {
        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
        String tn = "tvk" + String.format("%03d", 999 - Math.max(0, Math.min(999, g.weight()))) + p.getName();
        if (tn.length() > 40) tn = tn.substring(0, 40);

        Team old = sb.getEntryTeam(p.getName());
        if (old != null && !old.getName().equals(tn) && old.getName().startsWith("tvk")) {
            old.removeEntry(p.getName());
            if (old.getEntries().isEmpty()) old.unregister();
        }
        Team t = sb.getTeam(tn);
        if (t == null) t = sb.registerNewTeam(tn);
        t.prefix(TextUtil.parse(resolve(p, cfg.getString("nametag.prefix", ""), vars)));
        t.suffix(TextUtil.parse(resolve(p, cfg.getString("nametag.suffix", ""), vars)));
        if (!t.hasEntry(p.getName())) t.addEntry(p.getName());
    }

    private void removeFromTeam(Player p) {
        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
        Team t = sb.getEntryTeam(p.getName());
        if (t != null && t.getName().startsWith("tvk")) {
            t.removeEntry(p.getName());
            if (t.getEntries().isEmpty()) t.unregister();
        }
    }

    private void updateHeaderFooter(Player p) {
        if (!cfg.getBoolean("header-footer.enabled", true)) return;
        if (data.getBoolean("players." + p.getUniqueId() + ".hf-off", false)) return;

        Group g = groupOf(p);
        Map<String, String> vars = playerVars(p, g, prefixOf(p, g), suffixOf(p, g));

        String w = "worlds." + p.getWorld().getName();
        List<String> header = cfg.getStringList(w + ".header");
        List<String> footer = cfg.getStringList(w + ".footer");
        if (header.isEmpty()) header = cfg.getStringList("header-footer.header");
        if (footer.isEmpty()) footer = cfg.getStringList("header-footer.footer");

        p.sendPlayerListHeaderAndFooter(build(p, header, vars), build(p, footer, vars));
    }

    private Component build(Player p, List<String> lines, Map<String, String> vars) {
        List<Component> parts = new ArrayList<>(lines.size());
        for (String l : lines) parts.add(TextUtil.parse(resolve(p, l, vars)));
        return Component.join(JoinConfiguration.newlines(), parts);
    }

    // ------------------------------------------------------------------ API for command

    public boolean toggleHeaderFooter(Player p) {
        String path = "players." + p.getUniqueId() + ".hf-off";
        boolean off = !data.getBoolean(path, false);
        data.set(path, off ? true : null);
        saveData();
        if (off) {
            p.sendPlayerListHeaderAndFooter(Component.empty(), Component.empty());
        } else {
            refreshGlobals();
            updateHeaderFooter(p);
        }
        return off;
    }

    public void applyCustom(CommandSender by, Player target, String type, String raw, boolean admin) {
        String path = "players." + target.getUniqueId() + "." + type;

        if (raw.equalsIgnoreCase("reset")) {
            data.set(path, null);
            saveData();
            refreshGlobals();
            updateAll(target);
            send(by, "reset");
            return;
        }
        if (!admin) {
            if (!by.hasPermission("tabvk.color") && !cfg.getBoolean("customization.allow-colors-without-permission", false)) {
                raw = TextUtil.strip(raw);
            }
            int max = cfg.getInt("customization.max-" + type + "-length", 20);
            if (TextUtil.plainLength(raw) > max) {
                send(by, "too-long", "%max%", String.valueOf(max));
                return;
            }
        }
        data.set(path, raw);
        saveData();
        refreshGlobals();
        updateAll(target);

        if (by == target) {
            send(by, "set", "%type%", type, "%value%", raw);
        } else {
            send(by, "set-other", "%player%", target.getName(), "%type%", type, "%value%", raw);
        }
    }

    public void send(CommandSender to, String key, String... kv) {
        String msg = cfg.getString("messages." + key, key);
        for (int i = 0; i + 1 < kv.length; i += 2) msg = msg.replace(kv[i], kv[i + 1]);
        to.sendMessage(TextUtil.parse(cfg.getString("messages.prefix", "") + msg));
    }

    public void sendAbout(CommandSender to) {
        String v = getPluginMeta().getVersion();
        for (String l : cfg.getStringList("messages.about")) to.sendMessage(TextUtil.parse(l.replace("%version%", v)));
    }

    public void sendHelp(CommandSender to) {
        for (String l : cfg.getStringList("messages.help")) to.sendMessage(TextUtil.parse(l));
    }

    private void saveData() {
        try {
            data.save(dataFile);
        } catch (IOException e) {
            getLogger().warning("Could not save data.yml: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------ events

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Bukkit.getScheduler().runTaskLater(this, () -> {
            if (p.isOnline()) {
                refreshGlobals();
                updateAll(p);
            }
        }, 2L);
    }

    @EventHandler
    public void onWorld(PlayerChangedWorldEvent e) {
        Player p = e.getPlayer();
        Bukkit.getScheduler().runTaskLater(this, () -> {
            if (p.isOnline()) {
                refreshGlobals();
                updateHeaderFooter(p);
            }
        }, 1L);
    }

    /**
     * Hides "plugin:command" entries (like tabvk:tabvk) from the command suggestions.
     * Only the suggestions are hidden, typing the long form still works.
     * Runs last so it also cleans the list when RankVK calls updateCommands().
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommandSend(PlayerCommandSendEvent e) {
        String mode = cfg.getString("hide-namespaced-commands", "own").toLowerCase(Locale.ROOT);
        switch (mode) {
            case "all" -> e.getCommands().removeIf(c -> c.indexOf(':') > 0);
            case "own" -> e.getCommands().removeIf(c -> c.startsWith("tabvk:"));
            default -> { }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        removeFromTeam(e.getPlayer());
    }
}
