package de.lars.apimanager.utils;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.Set;

public class ValidateParameter {
    public static void validatePlayer(OfflinePlayer player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
    }

    public static void validateChunk(Chunk chunk) {
        if (chunk == null) {
            throw new IllegalArgumentException("Chunk cannot be null");
        }
    }

    public static void validateLocation(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
    }

    public static void validateNamedTextColor(NamedTextColor namedTextColor) {
        if (namedTextColor == null) {
            throw new IllegalArgumentException("NamedTextColor cannot be null");
        }
    }

    public static void validateTextDecoration(TextDecoration textDecoration) {
        if (textDecoration == null) {
            throw new IllegalArgumentException("TextDecoration cannot be null");
        }
    }

    public static void validateTextDecorations(Set<TextDecoration> textDecorations) {
        if (textDecorations == null) {
            throw new IllegalArgumentException("TextDecorations cannot be null");
        }
    }

    public static void validateReason(String reason) {
        if (reason == null) {
            throw new IllegalArgumentException("Reason cannot be null");
        }
    }

    public static void validateStatus(String status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
    }

    public static void validateName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
    }

    public static void validateNickName(String nickName) {
        if (nickName == null) {
            throw new IllegalArgumentException("Nickname cannot be null");
        }
    }

    public static void validateServerName(String serverName) {
        if (serverName == null) {
            throw new IllegalArgumentException("Servername cannot be null");
        }
    }

    public static void validateServerVersion(String serverVersion) {
        if (serverVersion == null) {
            throw new IllegalArgumentException("Serverversion cannot be null");
        }
    }

    public static void validateInstant(Instant instant) {
        if (instant == null) {
            throw new IllegalArgumentException("Instant cannot be null");
        }
    }
}