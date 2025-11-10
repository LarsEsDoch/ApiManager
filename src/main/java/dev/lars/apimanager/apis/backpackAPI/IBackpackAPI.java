package dev.lars.apimanager.apis.backpackAPI;

import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public interface IBackpackAPI {
    Instant getCreatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getCreatedAtAsync(OfflinePlayer player);

    Instant getUpdatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getUpdatedAtAsync(OfflinePlayer player);

    void setBackpack(OfflinePlayer player, String data);

    CompletableFuture<Void> setBackpackAsync(OfflinePlayer player, String data);

    String getBackpack(OfflinePlayer player);

    CompletableFuture<String> getBackpackAsync(OfflinePlayer player);
}