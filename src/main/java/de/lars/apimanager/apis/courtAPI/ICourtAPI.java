package de.lars.apimanager.apis.courtAPI;

import org.bukkit.OfflinePlayer;

import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;

public interface ICourtAPI {
    Timestamp getCreatedAt(OfflinePlayer player);

    CompletableFuture<Timestamp> getCreatedAtAsync(OfflinePlayer player);

    Timestamp getUpdatedAt(OfflinePlayer player);

    CompletableFuture<Timestamp> getUpdatedAtAsync(OfflinePlayer player);

    void report(OfflinePlayer target, OfflinePlayer prosecutor, String reason);

    CompletableFuture<Void> reportAsync(OfflinePlayer target, OfflinePlayer prosecutor, String reason);

    void setStatus(OfflinePlayer player, int status);

    CompletableFuture<Void> setStatusAsync(OfflinePlayer player, int status);

    Integer getStatus(OfflinePlayer player);

    CompletableFuture<Integer> getStatusAsync(OfflinePlayer player);

    void setReason(OfflinePlayer player, String reason);

    CompletableFuture<Void> setReasonAsync(OfflinePlayer player, String reason);

    String getReason(OfflinePlayer player);

    CompletableFuture<String> getReasonAsync(OfflinePlayer player);

    void setProsecutor(OfflinePlayer player, OfflinePlayer prosecutor);

    CompletableFuture<Void> setProsecutorAsync(OfflinePlayer player, OfflinePlayer prosecutor);

    String getProsecutor(OfflinePlayer player);

    CompletableFuture<String> getProsecutorAsync(OfflinePlayer player);

    void setTime(OfflinePlayer player, int time);

    CompletableFuture<Void> setTimeAsync(OfflinePlayer player, int time);

    Integer getTime(OfflinePlayer player);

    CompletableFuture<Integer> getTimeAsync(OfflinePlayer player);

    void setCell(OfflinePlayer player, int cell);

    CompletableFuture<Void> setCellAsync(OfflinePlayer player, int cell);

    Integer getCell(OfflinePlayer player);

    CompletableFuture<Integer> getCellAsync(OfflinePlayer player);

    void resetPlayer(OfflinePlayer player);

    CompletableFuture<Void> resetPlayerAsync(OfflinePlayer player);
}