package ir.gharri.tabVK;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Supports legacy (&a, &#ff00ff) and MiniMessage together. */
public final class TextUtil {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private static final Pattern HEX = Pattern.compile("&#([0-9a-fA-F]{6})");
    private static final Pattern LEGACY = Pattern.compile("&([0-9a-fk-orA-FK-OR])");

    private TextUtil() {}

    private static String tag(char c) {
        return switch (Character.toLowerCase(c)) {
            case '0' -> "<black>";
            case '1' -> "<dark_blue>";
            case '2' -> "<dark_green>";
            case '3' -> "<dark_aqua>";
            case '4' -> "<dark_red>";
            case '5' -> "<dark_purple>";
            case '6' -> "<gold>";
            case '7' -> "<gray>";
            case '8' -> "<dark_gray>";
            case '9' -> "<blue>";
            case 'a' -> "<green>";
            case 'b' -> "<aqua>";
            case 'c' -> "<red>";
            case 'd' -> "<light_purple>";
            case 'e' -> "<yellow>";
            case 'f' -> "<white>";
            case 'k' -> "<obfuscated>";
            case 'l' -> "<bold>";
            case 'm' -> "<strikethrough>";
            case 'n' -> "<underlined>";
            case 'o' -> "<italic>";
            default -> "<reset>";
        };
    }

    public static String legacyToMini(String in) {
        String s = HEX.matcher(in).replaceAll("<#$1>");
        Matcher m = LEGACY.matcher(s);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            m.appendReplacement(sb, Matcher.quoteReplacement(tag(m.group(1).charAt(0))));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    public static Component parse(String s) {
        try {
            return MM.deserialize(legacyToMini(s));
        } catch (Exception e) {
            return Component.text(s);
        }
    }

    /** Remove all colors / tags -> plain text. */
    public static String strip(String s) {
        return MM.stripTags(legacyToMini(s));
    }

    /** Visible length (without colors). */
    public static int plainLength(String s) {
        return PlainTextComponentSerializer.plainText().serialize(parse(s)).length();
    }
}
