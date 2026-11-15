package ir.gharri.tabVK;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

/** Separate class so PlaceholderAPI is only loaded when it is installed. */
final class PapiHook {

    private PapiHook() {}

    static String set(Player player, String text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }
}
