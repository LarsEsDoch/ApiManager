package de.lars.apimanager.apis.backpackAPI;

import de.lars.apimanager.ApiManager;
import de.lars.apimanager.database.DatabaseManager;
import de.lars.apimanager.utils.ValidateParameter;
import org.bukkit.OfflinePlayer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class BackpackAPIImpl implements IBackpackAPI {
    private final DatabaseManager db;

    public BackpackAPIImpl() {
        this.db = ApiManager.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db.update("""
            CREATE TABLE IF NOT EXISTS player_backpacks (
                uuid CHAR(36) NOT NULL PRIMARY KEY,
                data LONGBLOB,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (uuid) REFERENCES players(uuid) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);
    }

    public void initPlayer(OfflinePlayer player) {
        if (!doesUserExist(player)) {
            db.update("INSERT IGNORE INTO player_backpacks (uuid, data) VALUES (?, ?)",
                    player.getUniqueId().toString(), null);
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
    public Instant getCreatedAt(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                     "SELECT created_at FROM player_backpacks WHERE uuid = ? LIMIT 1"
                 )) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("created_at").toInstant();
                    return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Instant> getCreatedAtAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                     "SELECT created_at FROM player_backpacks WHERE uuid = ? LIMIT 1"
                 )) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("created_at").toInstant();
                    return null;
                }
            }
        });
    }

    @Override
    public Instant getUpdatedAt(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                     "SELECT updated_at FROM player_backpacks WHERE uuid = ? LIMIT 1"
                 )) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("updated_at").toInstant();
                    return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Instant> getUpdatedAtAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                     "SELECT updated_at FROM player_backpacks WHERE uuid = ? LIMIT 1"
                 )) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("updated_at").toInstant();
                    return null;
                }
            }
        });
    }

    @Override
    public void setBackpack(OfflinePlayer player, String data) {
        ValidateParameter.validatePlayer(player);
        String compressed = compressToBase64(data);
        db.update("UPDATE player_backpacks SET data = ? WHERE uuid = ? LIMIT 1",
                compressed, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setBackpackAsync(OfflinePlayer player, String data) {
        ValidateParameter.validatePlayer(player);
        String compressed = compressToBase64(data);
        return db.updateAsync("UPDATE player_backpacks SET data = ? WHERE uuid = ? LIMIT 1",
                    compressed, player.getUniqueId().toString());
    }

    @Override
    public String getBackpack(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT data FROM player_backpacks WHERE uuid = ? LIMIT 1")) {
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
        ValidateParameter.validatePlayer(player);
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT data FROM player_backpacks WHERE uuid = ? LIMIT 1")) {
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
            ApiManager.getInstance().getLogger().log(Level.WARNING, "Failed to compress backpack data", e);
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
            ApiManager.getInstance().getLogger().log(Level.WARNING, "Failed to decompress backpack data", e);
            return "";
        }
    }
}