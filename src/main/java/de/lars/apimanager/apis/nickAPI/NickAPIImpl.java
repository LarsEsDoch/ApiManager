package de.lars.apimanager.apis.nickAPI;

import de.lars.apimanager.ApiManager;
import de.lars.apimanager.database.DatabaseManager;
import de.lars.apimanager.utils.ValidateParameter;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class NickAPIImpl implements INickAPI {
    private final DatabaseManager db;

    public NickAPIImpl() {
        this.db = ApiManager.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db.update("""
            CREATE TABLE IF NOT EXISTS player_nicknames (
                uuid CHAR(36) NOT NULL PRIMARY KEY,
                nickname VARCHAR(32) DEFAULT NULL,
                fake_rank INT(16) DEFAULT NULL,
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
    public Instant getCreatedAt(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT created_at FROM player_nicknames WHERE uuid = ? LIMIT 1")) {
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
            try (PreparedStatement ps = conn.prepareStatement("SELECT created_at FROM player_nicknames WHERE uuid = ? LIMIT 1")) {
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
            try (PreparedStatement ps = conn.prepareStatement("SELECT updated_at FROM player_nicknames WHERE uuid = ? LIMIT 1")) {
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
            try (PreparedStatement ps = conn.prepareStatement("SELECT updated_at FROM player_nicknames WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("updated_at").toInstant();
                    return null;
                }
            }
        });
    }

    @Override
    public void setNickname(OfflinePlayer player, String nickname) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateNickName(nickname);
        db.update("UPDATE player_nicknames SET nickname=? WHERE uuid=?", nickname, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setNicknameAsync(OfflinePlayer player, String nickname) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateNickName(nickname);
        return db.updateAsync("UPDATE player_nicknames SET nickname=? WHERE uuid=?", nickname, player.getUniqueId().toString());
    }

    @Override
    public void resetNickname(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        db.update("UPDATE player_nicknames SET nickname=?,fake_rank=? WHERE uuid=?", null, null, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> resetNicknameAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.updateAsync("UPDATE player_nicknames SET nickname=?,fake_rank=? WHERE uuid=?", null, null, player.getUniqueId().toString());
    }

    @Override
    public String getNickname(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
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
        ValidateParameter.validatePlayer(player);
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
    public void setFakeRank(OfflinePlayer player, Integer rankId) {
        db.update("UPDATE player_nicknames SET fake_rank=? WHERE uuid=?", rankId, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setFakeRankAsync(OfflinePlayer player, Integer rankId) {
        return db.updateAsync("UPDATE player_nicknames SET fake_rank=? WHERE uuid=?", rankId, player.getUniqueId().toString());
    }

    @Override
    public Integer getFakeRank(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT fake_rank FROM player_nicknames WHERE uuid=?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int v = rs.getInt("fake_rank");
                        return rs.wasNull() ? null : v;
                    }
                }
            }
            return null;
        });
    }

    @Override
    public CompletableFuture<Integer> getFakeRankAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT fake_rank FROM player_nicknames WHERE uuid=?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int v = rs.getInt("fake_rank");
                        return rs.wasNull() ? null : v;
                    }
                }
            }
            return null;
        });
    }
}