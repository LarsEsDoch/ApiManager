package de.lars.apimanager.apis.banAPI;

import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IBanAPI {
    Instant getCreatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getCreatedAtAsync(OfflinePlayer player);

    Instant getUpdatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getUpdatedAtAsync(OfflinePlayer player);

    void setBanned(OfflinePlayer player, String reason, Integer hours);

    CompletableFuture<Void> setBannedAsync(OfflinePlayer player, String reason, Integer hours);

    void setUnBanned(OfflinePlayer player);

    CompletableFuture<Void> setUnBannedAsync(OfflinePlayer player);

    boolean isBanned(OfflinePlayer player);

    CompletableFuture<Boolean> isBannedAsync(OfflinePlayer player);

    void setReason(OfflinePlayer player, String reason);

    CompletableFuture<Void> setReasonAsync(OfflinePlayer player, String reason);

    String getReason(OfflinePlayer player);

    CompletableFuture<String> getReasonAsync(OfflinePlayer player);

    void setDays(OfflinePlayer player, Integer days);

    CompletableFuture<Void> setDaysAsync(OfflinePlayer player, Integer days);

    Instant getEnd(OfflinePlayer player);

    CompletableFuture<Instant> getEndAsync(OfflinePlayer player);

    List<String> getBannedPlayers();

    CompletableFuture<List<String>> getBannedPlayersAsync();
}