package dev.lars.apimanager.apis.scoreboardSettingsAPI;

import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public interface IScoreboardSettingsAPI {
    Instant getCreatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getCreatedAtAsync(OfflinePlayer player);

    Instant getUpdatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getUpdatedAtAsync(OfflinePlayer player);

    boolean isScoreboardEnabled(OfflinePlayer player);

    CompletableFuture<Boolean> isScoreboardEnabledAsync(OfflinePlayer player);

    void setScoreboardEnabled(OfflinePlayer player, boolean value);

    CompletableFuture<Void> setScoreboardEnabledAsync(OfflinePlayer player, boolean value);

    void setElement(OfflinePlayer player, ScoreboardElement element, boolean value);

    CompletableFuture<Void> setElementAsync(OfflinePlayer player, ScoreboardElement element, boolean value);

    boolean getElement(OfflinePlayer player, ScoreboardElement element);

    CompletableFuture<Boolean> getElementAsync(OfflinePlayer player, ScoreboardElement element);
}