package de.lars.apimanager.utils;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.HashSet;
import java.util.Set;

import static net.kyori.adventure.text.format.NamedTextColor.*;

public class TextFormation {
    public static NamedTextColor getNamedTextColor(Integer prefixID) {
        return switch (prefixID) {
            case 0 -> BLACK;
            case 1 -> DARK_BLUE;
            case 2 -> DARK_GREEN;
            case 3 -> DARK_AQUA;
            case 4 -> DARK_RED;
            case 5 -> DARK_PURPLE;
            case 6 -> GOLD;
            case 7 -> GRAY;
            case 8 -> DARK_GRAY;
            case 9 -> BLUE;
            case 10 -> GREEN;
            case 11 -> AQUA;
            case 12 -> RED;
            case 13 -> LIGHT_PURPLE;
            case 14 -> YELLOW;
            default -> WHITE;
        };
    }

    public static Integer getColorId(NamedTextColor color) {
        if (color == NamedTextColor.BLACK) return 0;
        if (color == NamedTextColor.DARK_BLUE) return 1;
        if (color == NamedTextColor.DARK_GREEN) return 2;
        if (color == NamedTextColor.DARK_AQUA) return 3;
        if (color == NamedTextColor.DARK_RED) return 4;
        if (color == NamedTextColor.DARK_PURPLE) return 5;
        if (color == NamedTextColor.GOLD) return 6;
        if (color == NamedTextColor.GRAY) return 7;
        if (color == NamedTextColor.DARK_GRAY) return 8;
        if (color == NamedTextColor.BLUE) return 9;
        if (color == NamedTextColor.GREEN) return 10;
        if (color == NamedTextColor.AQUA) return 11;
        if (color == NamedTextColor.RED) return 12;
        if (color == NamedTextColor.LIGHT_PURPLE) return 13;
        if (color == NamedTextColor.YELLOW) return 14;
        return 15;
    }

    public static int getDecorationId(TextDecoration decoration) {
        if (decoration == TextDecoration.BOLD) return 1;
        if (decoration == TextDecoration.ITALIC) return 2;
        if (decoration == TextDecoration.UNDERLINED) return 4;
        if (decoration == TextDecoration.STRIKETHROUGH) return 8;
        if (decoration == TextDecoration.OBFUSCATED) return 16;
        return 0;
    }

    public static Set<TextDecoration> getTextDecorations(int bitmask) {
        Set<TextDecoration> decorations = new HashSet<>();
        if ((bitmask & 1) != 0) decorations.add(TextDecoration.BOLD);
        if ((bitmask & 2) != 0) decorations.add(TextDecoration.ITALIC);
        if ((bitmask & 4) != 0) decorations.add(TextDecoration.UNDERLINED);
        if ((bitmask & 8) != 0) decorations.add(TextDecoration.STRIKETHROUGH);
        if ((bitmask & 16) != 0) decorations.add(TextDecoration.OBFUSCATED);
        return decorations;
    }

    public static int combineDecorations(Set<TextDecoration> decorations) {
        int bitmask = 0;
        for (TextDecoration dec : decorations) {
            bitmask |= getDecorationId(dec);
        }
        return bitmask;
    }
}