package dev.lars.apimanager.apis.playerSettingsAPI;

import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public interface IPlayerSettingsAPI {
    Instant getCreatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getCreatedAtAsync(OfflinePlayer player);

    Instant getUpdatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getUpdatedAtAsync(OfflinePlayer player);

    void setBedToggle(OfflinePlayer player, boolean toggle);

    CompletableFuture<Void> setBedToggleAsync(OfflinePlayer player, boolean toggle);

    boolean getBedToggle(OfflinePlayer player);

    CompletableFuture<Boolean> getBedToggleAsync(OfflinePlayer player);
}