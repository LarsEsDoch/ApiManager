package de.lars.apimanager.apis.timerAPI;

import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public interface ITimerAPI {
    Instant getCreatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getCreatedAtAsync(OfflinePlayer player);

    Instant getUpdatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getUpdatedAtAsync(OfflinePlayer player);

    void setTime(OfflinePlayer player, int time);

    CompletableFuture<Void> setTimeAsync(OfflinePlayer player, int time);

    void setEnabled(OfflinePlayer player, boolean off);

    CompletableFuture<Void> setEnabledAsync(OfflinePlayer player, boolean off);

    void setRunning(OfflinePlayer player, boolean running);

    CompletableFuture<Void> setRunningAsync(OfflinePlayer player, boolean running);

    void setTimer(OfflinePlayer player, boolean timer);

    CompletableFuture<Void> setTimerAsync(OfflinePlayer player, boolean timer);

    void setPublic(OfflinePlayer player, boolean isPublic);

    CompletableFuture<Void> setPublicAsync(OfflinePlayer player, boolean isPublic);

    Integer getTime(OfflinePlayer player);

    CompletableFuture<Integer> getTimeAsync(OfflinePlayer player);

    boolean isPublic(OfflinePlayer player);

    CompletableFuture<Boolean> isPublicAsync(OfflinePlayer player);

    boolean isEnabled(OfflinePlayer player);

    CompletableFuture<Boolean> isEnabledAsync(OfflinePlayer player);

    boolean isRunning(OfflinePlayer player);

    CompletableFuture<Boolean> isRunningAsync(OfflinePlayer player);

    boolean isTimerEnabled(OfflinePlayer player);

    CompletableFuture<Boolean> isTimerEnabledAsync(OfflinePlayer player);

    boolean publicTimerExists(OfflinePlayer player);

    CompletableFuture<Boolean> publicTimerExistsAsync(OfflinePlayer player);
}