package de.lars.apimanager.apis.questAPI;

import de.lars.apimanager.ApiManager;
import de.lars.apimanager.database.DatabaseRepository;
import de.lars.apimanager.database.IDatabaseManager;
import de.lars.apimanager.utils.ValidateParameter;
import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class QuestAPIImpl implements IQuestAPI {
    private static final String TABLE = "player_quests";

    private DatabaseRepository repo() {
        return new DatabaseRepository();
    }

    private IDatabaseManager db() {
        return ApiManager.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db().update("""
            CREATE TABLE IF NOT EXISTS player_quests (
                uuid CHAR(36) NOT NULL PRIMARY KEY,
                streak INT NOT NULL DEFAULT 0,
                active_quest_id INT NOT NULL DEFAULT -1,
                quest_name VARCHAR(255) NOT NULL DEFAULT '',
                is_quest_complete BOOLEAN NOT NULL DEFAULT FALSE,
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
        db().update("""
            INSERT IGNORE INTO player_quests (uuid, streak, quest, quest_name, is_quest_complete, target, progress, last_quest_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """, player.getUniqueId().toString(), 0, -1, "", false, null, 0, null);
    }

    public boolean doesUserExist(OfflinePlayer player) {
        return repo().exists(TABLE, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public Instant getCreatedAt(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getInstant(TABLE, "created_at", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Instant> getCreatedAtAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getInstantAsync(TABLE, "created_at", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public Instant getUpdatedAt(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getInstant(TABLE, "updated_at", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Instant> getUpdatedAtAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getInstantAsync(TABLE, "updated_at", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setStreak(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "streak", Math.max(0, amount), "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setStreakAsync(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "streak", Math.max(0, amount), "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void increaseStreak(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        if (amount == 0) return;
        repo().increaseColumn(TABLE, "streak", amount, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> increaseStreakAsync(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        if (amount == 0) return CompletableFuture.completedFuture(null);
        return repo().increaseColumnAsync(TABLE, "streak", amount, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void decreaseStreak(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        if (amount == 0) return;
        repo().decreaseColumn(TABLE, "streak", amount, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> decreaseStreakAsync(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        if (amount == 0) return CompletableFuture.completedFuture(null);
        return repo().decreaseColumnAsync(TABLE, "streak", amount, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public Integer getStreak(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        Integer streak = repo().getInteger(TABLE, "streak", "uuid = ?", player.getUniqueId().toString());
        return streak != null ? streak : 0;
    }

    @Override
    public CompletableFuture<Integer> getStreakAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getIntegerAsync(TABLE, "streak", "uuid = ?", player.getUniqueId().toString())
            .thenApply(streak -> streak != null ? streak : 0);
    }

    @Override
    public void setQuest(OfflinePlayer player, int questId, Integer target, String name) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateName(name);
        repo().updateColumns(TABLE,
            new String[]{"quest", "is_quest_complete", "progress", "target", "quest_name"},
            new Object[]{questId, false, 0, target, name},
            "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setQuestAsync(OfflinePlayer player, int questId, Integer target, String name) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateName(name);
        return repo().updateColumnsAsync(TABLE,
            new String[]{"quest", "is_quest_complete", "progress", "target", "quest_name"},
            new Object[]{questId, false, 0, target, name},
            "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setQuestComplete(OfflinePlayer player, boolean complete) {
        ValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "is_quest_complete", complete, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setQuestCompleteAsync(OfflinePlayer player, boolean complete) {
        ValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "is_quest_complete", complete, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public boolean isQuestComplete(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        Boolean result = repo().getBoolean(TABLE, "is_quest_complete", "uuid = ?", player.getUniqueId().toString());
        return result != null && result;
    }

    @Override
    public CompletableFuture<Boolean> isQuestCompleteAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getBooleanAsync(TABLE, "is_quest_complete", "uuid = ?", player.getUniqueId().toString())
            .thenApply(result -> result != null && result);
    }

    @Override
    public void setQuestName(OfflinePlayer player, String name) {
        ValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "quest_name", name, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setQuestNameAsync(OfflinePlayer player, String name) {
        ValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "quest_name", name, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public String getQuestName(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getString(TABLE, "quest_name", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<String> getQuestNameAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getStringAsync(TABLE, "quest_name", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public Integer getQuest(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        Integer active_quest_id = repo().getInteger(TABLE, "quest", "uuid = ?", player.getUniqueId().toString());
        return active_quest_id != null ? active_quest_id : -1;
    }

    @Override
    public CompletableFuture<Integer> getQuestAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getIntegerAsync(TABLE, "quest", "uuid = ?", player.getUniqueId().toString())
            .thenApply(active_quest_id -> active_quest_id != null ? active_quest_id : -1);
    }

    @Override
    public Integer getTargetAmount(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getInteger(TABLE, "target", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Integer> getTargetAmountAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getIntegerAsync(TABLE, "target", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public Integer getProgress(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        Integer progress = repo().getInteger(TABLE, "progress", "uuid = ?", player.getUniqueId().toString());
        return progress != null ? progress : 0;
    }

    @Override
    public CompletableFuture<Integer> getProgressAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getIntegerAsync(TABLE, "progress", "uuid = ?", player.getUniqueId().toString())
            .thenApply(progress -> progress != null ? progress : 0);
    }

    @Override
    public void increaseProgress(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        repo().increaseColumn(TABLE, "progress", amount, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> increaseProgressAsync(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        return repo().increaseColumnAsync(TABLE, "progress", amount, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void decreaseProgress(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        repo().decreaseColumn(TABLE, "progress", amount, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> decreaseProgressAsync(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        return repo().decreaseColumnAsync(TABLE, "progress", amount, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setProgress(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "progress", Math.max(0, amount), "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setProgressAsync(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "progress", Math.max(0, amount), "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public Instant getQuestDate(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getInstant(TABLE, "last_quest_at", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Instant> getQuestDateAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getInstantAsync(TABLE, "last_quest_at", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void resetQuest(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        repo().updateColumns(TABLE,
            new String[]{"quest", "is_quest_complete", "target", "progress", "last_quest_at"},
            new Object[]{-1, false, null, 0, null},
            "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> resetQuestAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().updateColumnsAsync(TABLE,
            new String[]{"quest", "is_quest_complete", "target", "progress", "last_quest_at"},
            new Object[]{-1, false, null, 0, null},
            "uuid = ?", player.getUniqueId().toString());
    }
}