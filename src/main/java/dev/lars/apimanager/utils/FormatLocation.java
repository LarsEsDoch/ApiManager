package dev.lars.apimanager.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Locale;
import java.util.Objects;

public class FormatLocation {
    public static String serializeLocation(Location loc) {
        return String.format(Locale.ENGLISH, "%s,%.3f,%.3f,%.3f,%.3f,%.3f",
                Objects.requireNonNull(loc.getWorld()).getName(),
                loc.getX(), loc.getY(), loc.getZ(),
                loc.getYaw(), loc.getPitch()
        );
    }

    public static Location deserializeLocation(String data) {
        if (data == null || data.isEmpty()) return null;
        String[] parts = data.split(",");
        if (parts.length != 6) return null;
        return new Location(
                Bukkit.getWorld(parts[0]),
                Double.parseDouble(parts[1]),
                Double.parseDouble(parts[2]),
                Double.parseDouble(parts[3]),
                Float.parseFloat(parts[4]),
                Float.parseFloat(parts[5])
        );
    }
}