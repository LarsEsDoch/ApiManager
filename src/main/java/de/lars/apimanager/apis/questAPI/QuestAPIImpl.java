package de.lars.apimanager.apis.questAPI;

import de.lars.apimanager.Main;
import de.lars.apimanager.database.DatabaseManager;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;

public class QuestAPIImpl implements IQuestAPI {
    private final DatabaseManager db;

    public QuestAPIImpl() {
        this.db = Main.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db.update("""
            CREATE TABLE IF NOT EXISTS player_quests (
                uuid CHAR(36) NOT NULL PRIMARY KEY,
                streak INT NOT NULL DEFAULT 0,
                quest INT NOT NULL DEFAULT -1,
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
        if (player == null) return;
        db.update("""
            INSERT IGNORE INTO player_quests (uuid, streak, quest, quest_complete, target, progress, last_quest_at)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """, player.getUniqueId().toString(), 0, -1, false, null, 0, null);
    }

    public boolean doesUserExist(OfflinePlayer player) {
        if (player == null) return false;
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
    public Timestamp getCreatedAt(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT created_at FROM player_quests WHERE uuid = ? LIMIT 1")) {
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
            try (PreparedStatement ps = conn.prepareStatement("SELECT created_at FROM player_quests WHERE uuid = ? LIMIT 1")) {
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
            try (PreparedStatement ps = conn.prepareStatement("SELECT updated_at FROM player_quests WHERE uuid = ? LIMIT 1")) {
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
            try (PreparedStatement ps = conn.prepareStatement("SELECT updated_at FROM player_quests WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("updated_at");
                    return null;
                }
            }
        });
    }

    @Override
    public void setStreak(OfflinePlayer player, int amount) {
        if (player == null) return;
        db.update("UPDATE player_quests SET streak = ? WHERE uuid = ?", Math.max(0, amount), player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setStreakAsync(OfflinePlayer player, int amount) {
        if (player == null) return CompletableFuture.completedFuture(null);
        return db.updateAsync("UPDATE player_quests SET streak = ? WHERE uuid = ?", Math.max(0, amount), player.getUniqueId().toString());
    }

    @Override
    public void increaseStreak(OfflinePlayer player, int amount) {
        if (player == null || amount == 0) return;
        db.update("UPDATE player_quests SET streak = COALESCE(streak,0) + ? WHERE uuid = ?", amount, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> increaseStreakAsync(OfflinePlayer player, int amount) {
        if (player == null || amount == 0) return CompletableFuture.completedFuture(null);
        return db.updateAsync("UPDATE player_quests SET streak = COALESCE(streak,0) + ? WHERE uuid = ?", amount, player.getUniqueId().toString());
    }

    @Override
    public void decreaseStreak(OfflinePlayer player, int amount) {
        if (player == null || amount == 0) return;
        db.update("UPDATE player_quests SET streak = GREATEST(COALESCE(streak,0) - ?, 0) WHERE uuid = ?", amount, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> decreaseStreakAsync(OfflinePlayer player, int amount) {
        if (player == null || amount == 0) return CompletableFuture.completedFuture(null);
        return db.updateAsync("UPDATE player_quests SET streak = GREATEST(COALESCE(streak,0) - ?, 0) WHERE uuid = ?", amount, player.getUniqueId().toString());
    }

    @Override
    public Integer getStreak(OfflinePlayer player) {
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
    public void setQuest(OfflinePlayer player, int questId, Integer target) {
        if (player == null) return;
        db.update("""
            UPDATE player_quests
            SET quest = ?, quest_complete = FALSE, progress = 0, target = ?
            WHERE uuid = ?
        """, questId, target, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setQuestAsync(OfflinePlayer player, int questId, Integer target) {
        if (player == null) return CompletableFuture.completedFuture(null);
        return db.updateAsync("""
            UPDATE player_quests
            SET quest = ?, quest_complete = FALSE, progress = 0, target = ?
            WHERE uuid = ?
        """, questId, target, player.getUniqueId().toString());
    }

    @Override
    public void setQuestComplete(OfflinePlayer player, boolean complete) {
        if (player == null) return;
        db.update("UPDATE player_quests SET quest_complete = ? WHERE uuid = ?", complete, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setQuestCompleteAsync(OfflinePlayer player, boolean complete) {
        if (player == null) return CompletableFuture.completedFuture(null);
        return db.updateAsync("UPDATE player_quests SET quest_complete = ? WHERE uuid = ?", complete, player.getUniqueId().toString());
    }

    @Override
    public boolean getDailyQuestComplete(OfflinePlayer player) {
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
    public CompletableFuture<Boolean> getDailyQuestCompleteAsync(OfflinePlayer player) {
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
    public Integer getDailyQuest(OfflinePlayer player) {
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
    public void addProgress(OfflinePlayer player, int amount) {
        if (player == null || amount == 0) return;
        db.update("UPDATE player_quests SET progress = COALESCE(progress,0) + ? WHERE uuid = ?", amount, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> addProgressAsync(OfflinePlayer player, int amount) {
        if (player == null || amount == 0) return CompletableFuture.completedFuture(null);
        return db.updateAsync("UPDATE player_quests SET progress = COALESCE(progress,0) + ? WHERE uuid = ?", amount, player.getUniqueId().toString());
    }

    @Override
    public void removeProgress(OfflinePlayer player, int amount) {
        if (player == null || amount == 0) return;
        db.update("UPDATE player_quests SET progress = GREATEST(COALESCE(progress,0) - ?, 0) WHERE uuid = ?", amount, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> removeProgressAsync(OfflinePlayer player, int amount) {
        if (player == null || amount == 0) return CompletableFuture.completedFuture(null);
        return db.updateAsync("UPDATE player_quests SET progress = GREATEST(COALESCE(progress,0) - ?, 0) WHERE uuid = ?", amount, player.getUniqueId().toString());
    }

    @Override
    public void setProgress(OfflinePlayer player, int amount) {
        if (player == null) return;
        db.update("UPDATE player_quests SET progress = ? WHERE uuid = ?", Math.max(0, amount), player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setProgressAsync(OfflinePlayer player, int amount) {
        if (player == null) return CompletableFuture.completedFuture(null);
        return db.updateAsync("UPDATE player_quests SET progress = ? WHERE uuid = ?", Math.max(0, amount), player.getUniqueId().toString());
    }

    @Override
    public Timestamp getQuestDate(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT last_quest_at FROM player_quests WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("last_quest_at");
                    return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Timestamp> getQuestDateAsync(OfflinePlayer player) {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT last_quest_at FROM player_quests WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("last_quest_at");
                    return null;
                }
            }
        });
    }

    @Override
    public void resetQuest(OfflinePlayer player) {
        if (player == null) return;
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
        if (player == null) return CompletableFuture.completedFuture(null);
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