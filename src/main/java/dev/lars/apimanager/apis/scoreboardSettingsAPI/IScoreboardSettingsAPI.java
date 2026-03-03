package dev.lars.apimanager.apis.scoreboardSettingsAPI;

import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public interface IScoreboardSettingsAPI {
    Instant getCreatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getCreatedAtAsync(OfflinePlayer player);

    Instant getUpdatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getUpdatedAtAsync(OfflinePlayer player);

    void setScoreboardToggle(OfflinePlayer player, boolean toggle);

    CompletableFuture<Void> setScoreboardToggleAsync(OfflinePlayer player, boolean toggle);

    boolean getScoreboardToggle(OfflinePlayer player);

    CompletableFuture<Boolean> getScoreboardToggleAsync(OfflinePlayer player);
}