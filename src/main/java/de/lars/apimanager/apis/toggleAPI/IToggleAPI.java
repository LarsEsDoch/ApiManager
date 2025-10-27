package de.lars.apimanager.apis.toggleAPI;

import org.bukkit.OfflinePlayer;

import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;

public interface IToggleAPI {
    Timestamp getCreatedAt(OfflinePlayer player);

    CompletableFuture<Timestamp> getCreatedAtAsync(OfflinePlayer player);

    Timestamp getUpdatedAt(OfflinePlayer player);

    CompletableFuture<Timestamp> getUpdatedAtAsync(OfflinePlayer player);

    void setBedToggle(OfflinePlayer player, boolean toggle);

    CompletableFuture<Void> setBedToggleAsync(OfflinePlayer player, boolean toggle);

    boolean getBedToggle(OfflinePlayer player);

    CompletableFuture<Boolean> getBedToggleAsync(OfflinePlayer player);

    void setScoreboardToggle(OfflinePlayer player, boolean toggle);

    CompletableFuture<Void> setScoreboardToggleAsync(OfflinePlayer player, boolean toggle);

    boolean getScoreboardToggle(OfflinePlayer player);

    CompletableFuture<Boolean> getScoreboardToggleAsync(OfflinePlayer player);
}