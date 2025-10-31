package de.lars.apimanager.apis.questAPI;

import de.lars.apimanager.ApiManager;
import de.lars.apimanager.database.DatabaseManager;
import de.lars.apimanager.utils.ValidateParameter;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class QuestAPIImpl implements IQuestAPI {
    private final DatabaseManager db;

    public QuestAPIImpl() {
        this.db = ApiManager.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db.update("""
            CREATE TABLE IF NOT EXISTS player_quests (
                uuid CHAR(36) NOT NULL PRIMARY KEY,
                streak INT NOT NULL DEFAULT 0,
                quest INT NOT NULL DEFAULT -1,
                quest_name VARCHAR(255) NOT NULL DEFAULT '',
                quest_complete BOOLEAN NOT NULL DEFAULT FALSE,
                target INT NULL,
                progress INT NOT NULL DEFAULT 0,
                last_quest_at TIMESTAMP NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (uuid) REFERENCES players(uuid) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);
    }

    public void initPlayer(OfflinePlayer player) {
        db.update("""
            INSERT IGNORE INTO player_quests (uuid, streak, quest, quest_name, quest_complete, target, progress, last_quest_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """, player.getUniqueId().toString(), 0, -1, "", false, null, 0, null);
    }

    public boolean doesUserExist(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM player_quests WHERE uuid = ? LIMIT 1")) {
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
            try (PreparedStatement ps = conn.prepareStatement("SELECT created_at FROM player_quests WHERE uuid = ? LIMIT 1")) {
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
            try (PreparedStatement ps = conn.prepareStatement("SELECT created_at FROM player_quests WHERE uuid = ? LIMIT 1")) {
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
            try (PreparedStatement ps = conn.prepareStatement("SELECT updated_at FROM player_quests WHERE uuid = ? LIMIT 1")) {
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
            try (PreparedStatement ps = conn.prepareStatement("SELECT updated_at FROM player_quests WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("updated_at").toInstant();
                    return null;
                }
            }
        });
    }

    @Override
    public void setStreak(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        db.update("UPDATE player_quests SET streak = ? WHERE uuid = ? LIMIT 1", Math.max(0, amount), player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setStreakAsync(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        return db.updateAsync("UPDATE player_quests SET streak = ? WHERE uuid = ? LIMIT 1", Math.max(0, amount), player.getUniqueId().toString());
    }

    @Override
    public void increaseStreak(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        if (amount == 0) return;
        db.update("UPDATE player_quests SET streak = COALESCE(streak,0) + ? WHERE uuid = ? LIMIT 1", amount, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> increaseStreakAsync(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        if (amount == 0) return CompletableFuture.completedFuture(null);
        return db.updateAsync("UPDATE player_quests SET streak = COALESCE(streak,0) + ? WHERE uuid = ? LIMIT 1", amount, player.getUniqueId().toString());
    }

    @Override
    public void decreaseStreak(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        if (amount == 0) return;
        db.update("UPDATE player_quests SET streak = GREATEST(COALESCE(streak,0) - ?, 0) WHERE uuid = ? LIMIT 1", amount, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> decreaseStreakAsync(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        if (amount == 0) return CompletableFuture.completedFuture(null);
        return db.updateAsync("UPDATE player_quests SET streak = GREATEST(COALESCE(streak,0) - ?, 0) WHERE uuid = ? LIMIT 1", amount, player.getUniqueId().toString());
    }

    @Override
    public Integer getStreak(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT streak FROM player_quests WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("streak");
                    return 0;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Integer> getStreakAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT streak FROM player_quests WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("streak");
                    return 0;
                }
            }
        });
    }

    @Override
    public void setQuest(OfflinePlayer player, int questId, Integer target, String name) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateName(name);
        db.update("""
            UPDATE player_quests
            SET quest = ?, quest_complete = FALSE, progress = 0, target = ?, quest_name = ?
            WHERE uuid = ?
        """, questId, target, name, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setQuestAsync(OfflinePlayer player, int questId, Integer target, String name) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateName(name);
        return db.updateAsync("""
            UPDATE player_quests
            SET quest = ?, quest_complete = FALSE, progress = 0, target = ?, quest_name = ?
            WHERE uuid = ?
        """, questId, target, name, player.getUniqueId().toString());
    }

    @Override
    public void setQuestComplete(OfflinePlayer player, boolean complete) {
        ValidateParameter.validatePlayer(player);
        db.update("UPDATE player_quests SET quest_complete = ? WHERE uuid = ? LIMIT 1", complete, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setQuestCompleteAsync(OfflinePlayer player, boolean complete) {
        ValidateParameter.validatePlayer(player);
        return db.updateAsync("UPDATE player_quests SET quest_complete = ? WHERE uuid = ? LIMIT 1", complete, player.getUniqueId().toString());
    }

    @Override
    public boolean isDailyQuestComplete(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT quest_complete FROM player_quests WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getBoolean("quest_complete");
                    return false;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> isDailyQuestCompleteAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT quest_complete FROM player_quests WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getBoolean("quest_complete");
                    return false;
                }
            }
        });
    }

    @Override
    public void setQuestName(OfflinePlayer player, String name) {
        ValidateParameter.validatePlayer(player);
        db.update("UPDATE player_quests SET quest_name = ? WHERE uuid = ? LIMIT 1", name, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setQuestNameAsync(OfflinePlayer player, String name) {
        ValidateParameter.validatePlayer(player);
        return db.updateAsync("UPDATE player_quests SET quest_name = ? WHERE uuid = ? LIMIT 1", name, player.getUniqueId().toString());
    }

    @Override
    public String getQuestName(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT quest_name FROM player_quests WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString("quest_name");
                    return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<String> getQuestNameAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT quest_name FROM player_quests WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString("quest_name");
                    return null;
                }
            }
        });
    }

    @Override
    public Integer getDailyQuest(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT quest FROM player_quests WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("quest");
                    return -1;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Integer> getDailyQuestAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT quest FROM player_quests WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("quest");
                    return -1;
                }
            }
        });
    }

    @Override
    public Integer getTargetAmount(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT target FROM player_quests WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int v = rs.getInt("target");
                        return rs.wasNull() ? null : v;
                    }
                    return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Integer> getTargetAmountAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT target FROM player_quests WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int v = rs.getInt("target");
                        return rs.wasNull() ? null : v;
                    }
                    return null;
                }
            }
        });
    }

    @Override
    public Integer getProgress(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT progress FROM player_quests WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("progress");
                    return 0;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Integer> getProgressAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT progress FROM player_quests WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("progress");
                    return 0;
                }
            }
        });
    }

    @Override
    public void increaseProgress(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        db.update("UPDATE player_quests SET progress = COALESCE(progress,0) + ? WHERE uuid = ? LIMIT 1", amount, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> increaseProgressAsync(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        return db.updateAsync("UPDATE player_quests SET progress = COALESCE(progress,0) + ? WHERE uuid = ? LIMIT 1", amount, player.getUniqueId().toString());
    }

    @Override
    public void decreaseProgress(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        db.update("UPDATE player_quests SET progress = GREATEST(COALESCE(progress,0) - ?, 0) WHERE uuid = ? LIMIT 1", amount, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> decreaseProgressAsync(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        return db.updateAsync("UPDATE player_quests SET progress = GREATEST(COALESCE(progress,0) - ?, 0) WHERE uuid = ? LIMIT 1", amount, player.getUniqueId().toString());
    }

    @Override
    public void setProgress(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        db.update("UPDATE player_quests SET progress = ? WHERE uuid = ? LIMIT 1", Math.max(0, amount), player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setProgressAsync(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        return db.updateAsync("UPDATE player_quests SET progress = ? WHERE uuid = ? LIMIT 1", Math.max(0, amount), player.getUniqueId().toString());
    }

    @Override
    public Instant getQuestDate(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT last_quest_at FROM player_quests WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("last_quest_at").toInstant();
                    return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Instant> getQuestDateAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT last_quest_at FROM player_quests WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("last_quest_at").toInstant();
                    return null;
                }
            }
        });
    }

    @Override
    public void resetQuest(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        db.update("""
            UPDATE player_quests
            SET quest = -1,
                quest_complete = FALSE,
                target = NULL,
                progress = 0,
                last_quest_at = NULL
            WHERE uuid = ?
        """, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> resetQuestAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db.updateAsync("""
            UPDATE player_quests
            SET quest = -1,
                quest_complete = FALSE,
                target = NULL,
                progress = 0,
                last_quest_at = NULL
            WHERE uuid = ?
        """, player.getUniqueId().toString());
    }
}