package ir.gharri.tabVK;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Logger;

/**
 * Reads prefix / suffix / primary group / weight from RankVK.
 * Uses reflection, so TabVK does not need the RankVK jar to compile and still loads without it.
 */
final class RankHook {

    /** What RankVK says about one online player. */
    record Info(String group, int weight, String prefix, String suffix) {}

    private final Logger log;
    private final Object manager;
    private final Method cached, resolvedFor, groupByName, defaultGroup;
    private final Method resPrefix, resSuffix;
    private final Method userActive;
    private final Method nodeIsGroup, nodeGroupName, nodeValue;
    private final Method groupWeight;
    private boolean warned;

    private RankHook(Logger log, Plugin rank) throws Exception {
        this.log = log;
        ClassLoader cl = rank.getClass().getClassLoader();

        manager = rank.getClass().getMethod("manager").invoke(rank);
        Class<?> mgr = manager.getClass();
        Class<?> user = cl.loadClass("ir.gharri.rankVK.model.User");
        Class<?> group = cl.loadClass("ir.gharri.rankVK.model.Group");
        Class<?> node = cl.loadClass("ir.gharri.rankVK.model.Node");
        Class<?> resolved = cl.loadClass("ir.gharri.rankVK.PermissionManager$Resolved");

        cached = mgr.getMethod("cached", java.util.UUID.class);
        resolvedFor = mgr.getMethod("resolvedFor", Player.class, user);
        groupByName = mgr.getMethod("group", String.class);
        defaultGroup = mgr.getMethod("defaultGroup");
        resPrefix = resolved.getMethod("prefix");
        resSuffix = resolved.getMethod("suffix");
        userActive = user.getMethod("active", String.class);
        nodeIsGroup = node.getMethod("isGroup");
        nodeGroupName = node.getMethod("groupName");
        nodeValue = node.getMethod("value");
        groupWeight = group.getMethod("weight");
    }

    /** Returns null when RankVK is missing or its API does not match. */
    static RankHook create(Logger log) {
        Plugin rank = Bukkit.getPluginManager().getPlugin("RankVK");
        if (rank == null || !rank.isEnabled()) return null;
        try {
            return new RankHook(log, rank);
        } catch (Exception e) {
            log.warning("RankVK found but could not be hooked: " + e);
            return null;
        }
    }

    /** null = RankVK has not loaded this player yet. */
    Info info(Player p) {
        try {
            Object u = cached.invoke(manager, p.getUniqueId());
            if (u == null) return null;

            Object r = resolvedFor.invoke(manager, p, u);
            String prefix = (String) resPrefix.invoke(r);
            String suffix = (String) resSuffix.invoke(r);

            // primary group = the group (set directly on the player) with the highest weight
            String best = null;
            int bestWeight = Integer.MIN_VALUE;
            List<?> nodes = (List<?>) userActive.invoke(u, p.getWorld().getName());
            for (Object n : nodes) {
                if (!(Boolean) nodeIsGroup.invoke(n) || !(Boolean) nodeValue.invoke(n)) continue;
                String name = (String) nodeGroupName.invoke(n);
                Object g = groupByName.invoke(manager, name);
                if (g == null) continue;
                int w = (Integer) groupWeight.invoke(g);
                if (w > bestWeight) {
                    bestWeight = w;
                    best = name;
                }
            }
            if (best == null) { // no group on the player -> RankVK's default group
                best = (String) defaultGroup.invoke(manager);
                Object g = groupByName.invoke(manager, best);
                bestWeight = g == null ? 0 : (Integer) groupWeight.invoke(g);
            }
            return new Info(best, bestWeight, prefix, suffix);
        } catch (Exception e) {
            if (!warned) {
                warned = true;
                log.warning("Could not read data from RankVK: " + e);
            }
            return null;
        }
    }
}
