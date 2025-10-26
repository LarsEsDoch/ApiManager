package de.lars.apimanager.apis.backpackAPI;

import de.lars.apimanager.Main;
import de.lars.apimanager.database.DatabaseManager;
import org.bukkit.OfflinePlayer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class BackpackAPIImpl implements IBackpackAPI {
    private final DatabaseManager db;

    public BackpackAPIImpl() {
        this.db = Main.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db.update("""
            CREATE TABLE IF NOT EXISTS player_backpacks (
                uuid CHAR(36) NOT NULL PRIMARY KEY,
                slots INT NOT NULL DEFAULT 9,
                data LONGBLOB,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (uuid) REFERENCES players(uuid) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);
    }

    public void initPlayer(OfflinePlayer player) {
        if (!doesUserExist(player)) {
            db.update("INSERT IGNORE INTO player_backpacks (uuid, slots, data) VALUES (?, ?, ?)",
                    player.getUniqueId().toString(), 9, null);
        }
    }

    public boolean doesUserExist(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM player_backpacks WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        });
    }

    @Override
    public void setSlots(OfflinePlayer player, int slots) {
        if (slots < 0) slots = 0;
        db.update("UPDATE player_backpacks SET slots = ? WHERE uuid = ?",
                slots, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setSlotsAsync(OfflinePlayer player, int slots) {
        if (slots < 0) slots = 0;
        return db.updateAsync("UPDATE player_backpacks SET slots = ? WHERE uuid = ?",
                        slots, player.getUniqueId().toString());
    }

    @Override
    public Integer getSlots(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT slots FROM player_backpacks WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int slots = rs.getInt("slots");
                        return rs.wasNull() ? null : slots;
                    } else {
                        return null;
                    }
                }
            }
        });
    }

    @Override
    public CompletableFuture<Integer> getSlotsAsync(OfflinePlayer player) {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT slots FROM player_backpacks WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int slots = rs.getInt("slots");
                        return rs.wasNull() ? null : slots;
                    } else {
                        return null;
                    }
                }
            }
        });
    }

    @Override
    public void setBackpack(OfflinePlayer player, String data) {
        String compressed = compressToBase64(data);
        db.update("UPDATE player_backpacks SET data = ? WHERE uuid = ?",
                compressed, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setBackpackAsync(OfflinePlayer player, String data) {
        String compressed = compressToBase64(data);
        return db.updateAsync("UPDATE player_backpacks SET data = ? WHERE uuid = ?",
                    compressed, player.getUniqueId().toString());
    }

    @Override
    public String getBackpack(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT data FROM player_backpacks WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String base64 = rs.getString("data");
                        return base64 == null ? "" : decompressFromBase64(base64);
                    }
                }
            }
            return "";
        });
    }

    @Override
    public CompletableFuture<String> getBackpackAsync(OfflinePlayer player) {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT data FROM player_backpacks WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String base64 = rs.getString("data");
                        return base64 == null ? "" : decompressFromBase64(base64);
                    }
                }
            }
            return "";
        });
    }

    private String compressToBase64(String input) {
        if (input == null || input.isEmpty()) return "";
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             DeflaterOutputStream dos = new DeflaterOutputStream(bos)) {
            dos.write(input.getBytes(StandardCharsets.UTF_8));
            dos.finish();
            return Base64.getEncoder().encodeToString(bos.toByteArray());
        } catch (IOException e) {
            Main.getInstance().getLogger().log(Level.WARNING, "Failed to compress backpack data", e);
            return "";
        }
    }

    private String decompressFromBase64(String base64) {
        if (base64 == null || base64.isEmpty()) return "";
        byte[] compressed = Base64.getDecoder().decode(base64);
        try (ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
             InflaterInputStream iis = new InflaterInputStream(bis);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = iis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            return bos.toString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            Main.getInstance().getLogger().log(Level.WARNING, "Failed to decompress backpack data", e);
            return "";
        }
    }
}