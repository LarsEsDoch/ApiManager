package de.lars.apimanager.apis.nickAPI;

import de.lars.apimanager.Main;
import de.lars.apimanager.database.DatabaseManager;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;

public class NickAPIImpl implements INickAPI {
    private final DatabaseManager db;

    public NickAPIImpl() {
        this.db = Main.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db.update("""
            CREATE TABLE IF NOT EXISTS player_nicknames (
                uuid CHAR(36) NOT NULL PRIMARY KEY,
                nickname VARCHAR(32) DEFAULT NULL,
                fake_rank VARCHAR(16) DEFAULT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (uuid) REFERENCES players(uuid) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);
    }

    public void initPlayer(OfflinePlayer player) {
        db.update("""
            INSERT IGNORE INTO player_nicknames (uuid, nickname, fake_rank)
            VALUES (?, ?, ?)
        """, player.getUniqueId().toString(), null, null);
    }

    public boolean doesUserExist(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM player_nicknames WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        });
    }

    @Override
    public Timestamp getCreatedAt(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT created_at FROM player_nicknames WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("created_at");
                    return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Timestamp> getCreatedAtAsync(OfflinePlayer player) {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT created_at FROM player_nicknames WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("created_at");
                    return null;
                }
            }
        });
    }

    @Override
    public Timestamp getUpdatedAt(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT updated_at FROM player_nicknames WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("updated_at");
                    return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Timestamp> getUpdatedAtAsync(OfflinePlayer player) {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT updated_at FROM player_nicknames WHERE uuid = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("updated_at");
                    return null;
                }
            }
        });
    }

    @Override
    public void setNickname(OfflinePlayer player, String nickname) {
        db.update("UPDATE player_nicknames SET nickname=? WHERE uuid=?", nickname, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setNicknameAsync(OfflinePlayer player, String nickname) {
        return db.updateAsync("UPDATE player_nicknames SET nickname=? WHERE uuid=?", nickname, player.getUniqueId().toString());
    }

    @Override
    public String getNickname(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT nickname FROM player_nicknames WHERE uuid=?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString("nickname");
                }
            }
            return null;
        });
    }

    @Override
    public CompletableFuture<String> getNicknameAsync(OfflinePlayer player) {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT nickname FROM player_nicknames WHERE uuid=?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString("nickname");
                }
            }
            return null;
        });
    }

    @Override
    public void setFakeRank(OfflinePlayer player, String rankId) {
        db.update("UPDATE player_nicknames SET fake_rank=? WHERE uuid=?", rankId, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setFakeRankAsync(OfflinePlayer player, String rankId) {
        return db.updateAsync("UPDATE player_nicknames SET fake_rank=? WHERE uuid=?", rankId, player.getUniqueId().toString());
    }

    @Override
    public String getFakeRank(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT fake_rank FROM player_nicknames WHERE uuid=?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString("fake_rank");
                }
            }
            return null;
        });
    }

    @Override
    public CompletableFuture<String> getFakeRankAsync(OfflinePlayer player) {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT fake_rank FROM player_nicknames WHERE uuid=?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString("fake_rank");
                }
            }
            return null;
        });
    }
}