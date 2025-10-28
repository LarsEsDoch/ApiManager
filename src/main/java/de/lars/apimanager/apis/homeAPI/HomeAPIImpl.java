package de.lars.apimanager.apis.homeAPI;

import de.lars.apimanager.Main;
import de.lars.apimanager.database.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class HomeAPIImpl implements IHomeAPI {
    private final DatabaseManager db;

    public HomeAPIImpl() {
        this.db = Main.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db.update("""
            CREATE TABLE IF NOT EXISTS player_homes (
                id INT AUTO_INCREMENT PRIMARY KEY,
                uuid CHAR(36) NOT NULL,
                name VARCHAR(255) NOT NULL,
                location VARCHAR(255) NOT NULL,
                is_public TINYINT(1) DEFAULT 0,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (uuid) REFERENCES players(uuid) ON DELETE CASCADE,
                UNIQUE KEY unique_home (uuid, name)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);
    }

    @Override
    public Timestamp getCreatedAt(int homeId) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                     "SELECT created_at FROM player_homes WHERE id = ?"
                 )) {
                ps.setInt(1, homeId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("created_at");
                    return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Timestamp> getCreatedAtAsync(int homeId) {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                     "SELECT created_at FROM player_homes WHERE id = ?"
                 )) {
                ps.setInt(1, homeId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("created_at");
                    return null;
                }
            }
        });
    }

    @Override
    public Timestamp getUpdatedAt(int homeId) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                     "SELECT updated_at FROM player_homes WHERE id = ?"
                 )) {
                ps.setInt(1, homeId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("updated_at");
                    return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Timestamp> getUpdatedAtAsync(int homeId) {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                     "SELECT updated_at FROM player_homes WHERE id = ?"
                 )) {
                ps.setInt(1, homeId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("updated_at");
                    return null;
                }
            }
        });
    }

    @Override
    public void createHome(OfflinePlayer player, String name, Location location, boolean isPublic) {
        db.update("""
            INSERT INTO player_homes (uuid, name, location, is_public)
            VALUES (?, ?, ?, ?)
        """, player.getUniqueId().toString(), name, serializeLocation(location), isPublic ? 1 : 0);
    }

    @Override
    public CompletableFuture<Void> createHomeAsync(OfflinePlayer player, String name, Location location, boolean isPublic) {
        return db.updateAsync("""
            INSERT INTO player_homes (uuid, name, location, is_public)
            VALUES (?, ?, ?, ?)
        """, player.getUniqueId().toString(), name, serializeLocation(location), isPublic ? 1 : 0);
    }

    @Override
    public void deleteHome(int homeId) {
        db.update("DELETE FROM player_homes WHERE id = ?", homeId);
    }

    @Override
    public CompletableFuture<Void> deleteHomeAsync(int homeId) {
        return db.updateAsync("DELETE FROM player_homes WHERE id = ?", homeId);
    }

    @Override
    public void renameHome(int homeId, String newName) {
        db.update("UPDATE player_homes SET name = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?", newName, homeId);
    }

    @Override
    public CompletableFuture<Void> renameHomeAsync(int homeId, String newName) {
        return db.updateAsync("UPDATE player_homes SET name = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?", newName, homeId);
    }

    @Override
    public void setHomePublic(int homeId, boolean isPublic) {
        db.update("UPDATE player_homes SET is_public = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?", isPublic ? 1 : 0, homeId);
    }

    @Override
    public CompletableFuture<Void> setHomePublicAsync(int homeId, boolean isPublic) {
        return db.updateAsync("UPDATE player_homes SET is_public = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?", isPublic ? 1 : 0, homeId);
    }

    @Override
    public void updateHomeLocation(int homeId, Location location) {
        db.update("UPDATE player_homes SET location = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?", serializeLocation(location), homeId);
    }

    @Override
    public CompletableFuture<Void> updateHomeLocationAsync(int homeId, Location location) {
        return db.updateAsync("UPDATE player_homes SET location = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?", serializeLocation(location), homeId);
    }

    @Override
    public List<String> getHomes(OfflinePlayer player) {
        return db.query(conn -> {
            List<String> homes = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT name FROM player_homes WHERE uuid = ? OR is_public = 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) homes.add(rs.getString("name"));
                }
            }
            return homes;
        });
    }

    @Override
    public CompletableFuture<List<String>> getHomesAsync(OfflinePlayer player) {
        return db.queryAsync(conn -> {
            List<String> homes = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT name FROM player_homes WHERE uuid = ? OR is_public = 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) homes.add(rs.getString("name"));
                }
            }
            return homes;
        });
    }

    @Override
    public List<String> getOwnHomes(OfflinePlayer player) {
        return db.query(conn -> {
            List<String> homes = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT name FROM player_homes WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) homes.add(rs.getString("name"));
                }
            }
            return homes;
        });
    }

    @Override
    public CompletableFuture<List<String>> getOwnHomesAsync(OfflinePlayer player) {
        return db.queryAsync(conn -> {
            List<String> homes = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT name FROM player_homes WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) homes.add(rs.getString("name"));
                }
            }
            return homes;
        });
    }

    @Override
    public List<String> getAllHomes() {
        return db.query(conn -> {
            List<String> homes = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement("SELECT name FROM player_homes");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    homes.add(rs.getString("name"));
                }
            }
            return homes;
        });
    }

    @Override
    public CompletableFuture<List<String>> getAllHomesAsync() {
        return db.queryAsync(conn -> {
            List<String> homes = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement("SELECT name FROM player_homes");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    homes.add(rs.getString("name"));
                }
            }
            return homes;
        });
    }

    @Override
    public Location getHomeLocation(int homeId) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT location FROM player_homes WHERE id = ?")) {
                ps.setInt(1, homeId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return deserializeLocation(rs.getString("location"));
                }
            }
            return null;
        });
    }

    @Override
    public CompletableFuture<Location> getHomeLocationAsync(int homeId) {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT location FROM player_homes WHERE id = ?")) {
                ps.setInt(1, homeId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return deserializeLocation(rs.getString("location"));
                }
            }
            return null;
        });
    }

    @Override
    public boolean doesHomeExist(String name) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT 1 FROM player_homes WHERE name = ? LIMIT 1")) {
                ps.setString(1, name);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> doesHomeExistAsync(String name) {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT 1 FROM player_homes WHERE name = ? LIMIT 1")) {
                ps.setString(1, name);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        });
    }

    @Override
    public boolean doesOwnHomeExist(OfflinePlayer player, String name) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT 1 FROM player_homes WHERE uuid = ? AND name = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                ps.setString(2, name);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> doesOwnHomeExistAsync(OfflinePlayer player, String name) {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT 1 FROM player_homes WHERE uuid = ? AND name = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                ps.setString(2, name);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        });
    }

    @Override
    public int getHomeId(OfflinePlayer player, String name) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT id FROM player_homes WHERE (uuid = ? OR is_public = 1) AND name = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                ps.setString(2, name);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("id");
                }
            }
            return -1;
        });
    }

    @Override
    public CompletableFuture<Integer> getHomeIdAsync(OfflinePlayer player, String name) {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT id FROM player_homes WHERE (uuid = ? OR is_public = 1) AND name = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                ps.setString(2, name);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("id");
                }
            }
            return -1;
        });
    }

    private String serializeLocation(Location loc) {
        return String.format(
                "%s,%.3f,%.3f,%.3f,%.3f,%.3f",
                Objects.requireNonNull(loc.getWorld()).getName(),
                loc.getX(), loc.getY(), loc.getZ(),
                loc.getYaw(), loc.getPitch()
        );
    }

    private Location deserializeLocation(String data) {
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