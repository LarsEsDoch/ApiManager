package de.lars.apimanager.apis.backpackAPI;

import org.bukkit.OfflinePlayer;

import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;

public interface IBackpackAPI {
    Timestamp getCreatedAt(OfflinePlayer player);

    CompletableFuture<Timestamp> getCreatedAtAsync(OfflinePlayer player);

    Timestamp getUpdatedAt(OfflinePlayer player);

    CompletableFuture<Timestamp> getUpdatedAtAsync(OfflinePlayer player);

    void setBackpack(OfflinePlayer player, String data);

    CompletableFuture<Void> setBackpackAsync(OfflinePlayer player, String data);

    String getBackpack(OfflinePlayer player);

    CompletableFuture<String> getBackpackAsync(OfflinePlayer player);
}