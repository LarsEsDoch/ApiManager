package de.lars.apimanager.apis.timerAPI;

import org.bukkit.OfflinePlayer;

import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;

public interface ITimerAPI {
    Timestamp getCreatedAt(OfflinePlayer player);

    CompletableFuture<Timestamp> getCreatedAtAsync(OfflinePlayer player);

    Timestamp getUpdatedAt(OfflinePlayer player);

    CompletableFuture<Timestamp> getUpdatedAtAsync(OfflinePlayer player);

    void setTime(OfflinePlayer player, int time);

    CompletableFuture<Void> setTimeAsync(OfflinePlayer player, int time);

    void setOff(OfflinePlayer player, boolean off);

    CompletableFuture<Void> setOffAsync(OfflinePlayer player, boolean off);

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

    boolean isOff(OfflinePlayer player);

    CompletableFuture<Boolean> isOffAsync(OfflinePlayer player);

    boolean isRunning(OfflinePlayer player);

    CompletableFuture<Boolean> isRunningAsync(OfflinePlayer player);

    boolean isTimerEnabled(OfflinePlayer player);

    CompletableFuture<Boolean> isTimerEnabledAsync(OfflinePlayer player);

    boolean publicTimerExists(OfflinePlayer player);

    CompletableFuture<Boolean> publicTimerExistsAsync(OfflinePlayer player);
}