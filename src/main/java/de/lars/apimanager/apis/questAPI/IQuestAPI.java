package de.lars.apimanager.apis.questAPI;

import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public interface IQuestAPI {
    Instant getCreatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getCreatedAtAsync(OfflinePlayer player);

    Instant getUpdatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getUpdatedAtAsync(OfflinePlayer player);

    void setStreak(OfflinePlayer player, int amount);

    CompletableFuture<Void> setStreakAsync(OfflinePlayer player, int amount);

    void increaseStreak(OfflinePlayer player, int amount);

    CompletableFuture<Void> increaseStreakAsync(OfflinePlayer player, int amount);

    void decreaseStreak(OfflinePlayer player, int amount);

    CompletableFuture<Void> decreaseStreakAsync(OfflinePlayer player, int amount);

    Integer getStreak(OfflinePlayer player);

    CompletableFuture<Integer> getStreakAsync(OfflinePlayer player);

    void setQuest(OfflinePlayer player, int questId, Integer target, String name);

    CompletableFuture<Void> setQuestAsync(OfflinePlayer player, int questId, Integer target, String name);

    void setQuestComplete(OfflinePlayer player, boolean complete);

    CompletableFuture<Void> setQuestCompleteAsync(OfflinePlayer player, boolean complete);

    void setQuestName(OfflinePlayer player, String name);

    CompletableFuture<Void> setQuestNameAsync(OfflinePlayer player, String name);

    boolean isDailyQuestComplete(OfflinePlayer player);

    CompletableFuture<Boolean> isDailyQuestCompleteAsync(OfflinePlayer player);

    String getQuestName(OfflinePlayer player);

    CompletableFuture<String> getQuestNameAsync(OfflinePlayer player);

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

    Instant getQuestDate(OfflinePlayer player);

    CompletableFuture<Instant> getQuestDateAsync(OfflinePlayer player);

    void resetQuest(OfflinePlayer player);

    CompletableFuture<Void> resetQuestAsync(OfflinePlayer player);
}