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

    void setShowCoins(OfflinePlayer player, boolean show);

    CompletableFuture<Void> setShowCoinsAsync(OfflinePlayer player, boolean show);

    boolean getShowCoins(OfflinePlayer player);

    CompletableFuture<Boolean> getShowCoinsAsync(OfflinePlayer player);

    void setShowPlaytime(OfflinePlayer player, boolean show);

    CompletableFuture<Void> setShowPlaytimeAsync(OfflinePlayer player, boolean show);

    boolean getShowPlaytime(OfflinePlayer player);

    CompletableFuture<Boolean> getShowPlaytimeAsync(OfflinePlayer player);

    void setShowDeaths(OfflinePlayer player, boolean show);

    CompletableFuture<Void> setShowDeathsAsync(OfflinePlayer player, boolean show);

    boolean getShowDeaths(OfflinePlayer player);

    CompletableFuture<Boolean> getShowDeathsAsync(OfflinePlayer player);

    void setShowCoordinates(OfflinePlayer player, boolean show);

    CompletableFuture<Void> setShowCoordinatesAsync(OfflinePlayer player, boolean show);

    boolean getShowCoordinates(OfflinePlayer player);

    CompletableFuture<Boolean> getShowCoordinatesAsync(OfflinePlayer player);

    void setShowQuests(OfflinePlayer player, boolean show);

    CompletableFuture<Void> setShowQuestsAsync(OfflinePlayer player, boolean show);

    boolean getShowQuests(OfflinePlayer player);

    CompletableFuture<Boolean> getShowQuestsAsync(OfflinePlayer player);

    void setShowOnlinePlayers(OfflinePlayer player, boolean show);

    CompletableFuture<Void> setShowOnlinePlayersAsync(OfflinePlayer player, boolean show);

    boolean getShowOnlinePlayers(OfflinePlayer player);

    CompletableFuture<Boolean> getShowOnlinePlayersAsync(OfflinePlayer player);

    void setShowPing(OfflinePlayer player, boolean show);

    CompletableFuture<Void> setShowPingAsync(OfflinePlayer player, boolean show);

    boolean getShowPing(OfflinePlayer player);

    CompletableFuture<Boolean> getShowPingAsync(OfflinePlayer player);

    void setShowBiom(OfflinePlayer player, boolean show);

    CompletableFuture<Void> setShowBiomAsync(OfflinePlayer player, boolean show);

    boolean getShowBiom(OfflinePlayer player);

    CompletableFuture<Boolean> getShowBiomAsync(OfflinePlayer player);

    void setShowWeather(OfflinePlayer player, boolean show);

    CompletableFuture<Void> setShowWeatherAsync(OfflinePlayer player, boolean show);

    boolean getShowWeather(OfflinePlayer player);

    CompletableFuture<Boolean> getShowWeatherAsync(OfflinePlayer player);

    void setShowCondition(OfflinePlayer player, boolean show);

    CompletableFuture<Void> setShowConditionAsync(OfflinePlayer player, boolean show);

    boolean getShowCondition(OfflinePlayer player);

    CompletableFuture<Boolean> getShowConditionAsync(OfflinePlayer player);

    void setShowEventCountdown(OfflinePlayer player, boolean show);

    CompletableFuture<Void> setShowEventCountdownAsync(OfflinePlayer player, boolean show);

    boolean getShowEventCountdown(OfflinePlayer player);

    CompletableFuture<Boolean> getShowEventCountdownAsync(OfflinePlayer player);

    void setShowKills(OfflinePlayer player, boolean show);

    CompletableFuture<Void> setShowKillsAsync(OfflinePlayer player, boolean show);

    boolean getShowKills(OfflinePlayer player);

    CompletableFuture<Boolean> getShowKillsAsync(OfflinePlayer player);

    void setShowProgress(OfflinePlayer player, boolean show);

    CompletableFuture<Void> setShowProgressAsync(OfflinePlayer player, boolean show);

    boolean getShowProgress(OfflinePlayer player);

    CompletableFuture<Boolean> getShowProgressAsync(OfflinePlayer player);

    void setShowSessionTime(OfflinePlayer player, boolean show);

    CompletableFuture<Void> setShowSessionTimeAsync(OfflinePlayer player, boolean show);

    boolean getShowSessionTime(OfflinePlayer player);

    CompletableFuture<Boolean> getShowSessionTimeAsync(OfflinePlayer player);
}