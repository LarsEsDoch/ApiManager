package de.lars.apimanager.apis.questAPI;

import org.bukkit.OfflinePlayer;

import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;

public interface IQuestAPI {
    Timestamp getCreatedAt(OfflinePlayer player);

    CompletableFuture<Timestamp> getCreatedAtAsync(OfflinePlayer player);

    Timestamp getUpdatedAt(OfflinePlayer player);

    CompletableFuture<Timestamp> getUpdatedAtAsync(OfflinePlayer player);

    void setStreak(OfflinePlayer player, int amount);

    CompletableFuture<Void> setStreakAsync(OfflinePlayer player, int amount);

    void increaseStreak(OfflinePlayer player, int amount);

    CompletableFuture<Void> increaseStreakAsync(OfflinePlayer player, int amount);

    void decreaseStreak(OfflinePlayer player, int amount);

    CompletableFuture<Void> decreaseStreakAsync(OfflinePlayer player, int amount);

    Integer getStreak(OfflinePlayer player);

    CompletableFuture<Integer> getStreakAsync(OfflinePlayer player);

    void setQuest(OfflinePlayer player, int questId, Integer target);

    CompletableFuture<Void> setQuestAsync(OfflinePlayer player, int questId, Integer target);

    void setQuestComplete(OfflinePlayer player, boolean complete);

    CompletableFuture<Void> setQuestCompleteAsync(OfflinePlayer player, boolean complete);

    boolean getDailyQuestComplete(OfflinePlayer player);

    CompletableFuture<Boolean> getDailyQuestCompleteAsync(OfflinePlayer player);

    Integer getDailyQuest(OfflinePlayer player);

    CompletableFuture<Integer> getDailyQuestAsync(OfflinePlayer player);

    Integer getTargetAmount(OfflinePlayer player);

    CompletableFuture<Integer> getTargetAmountAsync(OfflinePlayer player);

    Integer getProgress(OfflinePlayer player);

    CompletableFuture<Integer> getProgressAsync(OfflinePlayer player);

    void increaseProgress(OfflinePlayer player, int amount);

    CompletableFuture<Void> increaseProgressAsync(OfflinePlayer player, int amount);

    void decreaseProgress(OfflinePlayer player, int amount);

    CompletableFuture<Void> decreaseProgressAsync(OfflinePlayer player, int amount);

    void setProgress(OfflinePlayer player, int amount);

    CompletableFuture<Void> setProgressAsync(OfflinePlayer player, int amount);

    Timestamp getQuestDate(OfflinePlayer player);

    CompletableFuture<Timestamp> getQuestDateAsync(OfflinePlayer player);

    void resetQuest(OfflinePlayer player);

    CompletableFuture<Void> resetQuestAsync(OfflinePlayer player);
}