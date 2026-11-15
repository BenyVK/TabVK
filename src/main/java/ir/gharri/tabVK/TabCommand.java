package ir.gharri.tabVK;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class TabCommand implements CommandExecutor, TabCompleter {

    private final TabVK plugin;

    public TabCommand(TabVK plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String label, String[] a) {
        if (a.length == 0 || a[0].equalsIgnoreCase("help")) {
            plugin.sendHelp(s);
            return true;
        }

        switch (a[0].toLowerCase(Locale.ROOT)) {
            case "about", "version" -> plugin.sendAbout(s);
            case "reload" -> {
                if (!s.hasPermission("tabvk.admin")) {
                    plugin.send(s, "no-permission");
                    return true;
                }
                plugin.reloadAll();
                plugin.refreshAll();
                plugin.send(s, "reloaded");
            }
            case "toggle" -> {
                if (!(s instanceof Player p)) {
                    plugin.send(s, "players-only");
                    return true;
                }
                if (!p.hasPermission("tabvk.toggle")) {
                    plugin.send(s, "no-permission");
                    return true;
                }
                boolean off = plugin.toggleHeaderFooter(p);
                plugin.send(s, off ? "toggled-off" : "toggled-on");
            }
            case "prefix", "suffix" -> {
                if (!(s instanceof Player p)) {
                    plugin.send(s, "players-only");
                    return true;
                }
                String type = a[0].toLowerCase(Locale.ROOT);
                if (!p.hasPermission("tabvk.customize." + type)) {
                    plugin.send(s, "no-permission");
                    return true;
                }
                if (a.length < 2) {
                    plugin.send(s, "usage");
                    return true;
                }
                plugin.applyCustom(s, p, type, String.join(" ", Arrays.copyOfRange(a, 1, a.length)), false);
            }
            case "set" -> {
                if (!s.hasPermission("tabvk.admin")) {
                    plugin.send(s, "no-permission");
                    return true;
                }
                if (a.length < 4) {
                    plugin.sendHelp(s);
                    return true;
                }
                Player target = Bukkit.getPlayerExact(a[1]);
                if (target == null) {
                    plugin.send(s, "player-not-found");
                    return true;
                }
                String type = a[2].toLowerCase(Locale.ROOT);
                if (!type.equals("prefix") && !type.equals("suffix")) {
                    plugin.sendHelp(s);
                    return true;
                }
                plugin.applyCustom(s, target, type, String.join(" ", Arrays.copyOfRange(a, 3, a.length)), true);
            }
            default -> plugin.sendHelp(s);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String label, String[] a) {
        List<String> out = new ArrayList<>();
        if (a.length == 1) {
            out.add("help");
            out.add("about");
            if (s.hasPermission("tabvk.toggle")) out.add("toggle");
            if (s.hasPermission("tabvk.customize.prefix")) out.add("prefix");
            if (s.hasPermission("tabvk.customize.suffix")) out.add("suffix");
            if (s.hasPermission("tabvk.admin")) {
                out.add("reload");
                out.add("set");
            }
        } else if (a.length == 2 && a[0].equalsIgnoreCase("set") && s.hasPermission("tabvk.admin")) {
            for (Player p : Bukkit.getOnlinePlayers()) out.add(p.getName());
        } else if (a.length == 3 && a[0].equalsIgnoreCase("set") && s.hasPermission("tabvk.admin")) {
            out.add("prefix");
            out.add("suffix");
        } else if ((a.length == 2 && (a[0].equalsIgnoreCase("prefix") || a[0].equalsIgnoreCase("suffix")))
                || (a.length == 4 && a[0].equalsIgnoreCase("set"))) {
            out.add("reset");
        }
        String last = a[a.length - 1].toLowerCase(Locale.ROOT);
        out.removeIf(x -> !x.toLowerCase(Locale.ROOT).startsWith(last));
        return out;
    }
}
