package dev.lars.apimanager.apis.scoreboardSettingsAPI;

import dev.lars.apimanager.ApiManager;
import dev.lars.apimanager.database.DatabaseRepository;
import dev.lars.apimanager.database.IDatabaseManager;
import dev.lars.apimanager.utils.ApiManagerValidateParameter;
import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class ScoreboardSettingsAPIImpl implements IScoreboardSettingsAPI {
    private static final String TABLE = "scoreboard_settings";

    private DatabaseRepository repo() {
        return new DatabaseRepository();
    }

    private IDatabaseManager db() {
        return ApiManager.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db().update("""
            CREATE TABLE IF NOT EXISTS scoreboard_settings (
                uuid CHAR(36) NOT NULL PRIMARY KEY,
                scoreboard_toggle BOOLEAN NOT NULL DEFAULT TRUE,
                show_coins BOOLEAN NOT NULL DEFAULT FALSE,
                show_playtime BOOLEAN NOT NULL DEFAULT TRUE,
                show_deaths BOOLEAN NOT NULL DEFAULT FALSE,
                show_coordinates BOOLEAN NOT NULL DEFAULT TRUE,
                show_quests BOOLEAN NOT NULL DEFAULT FALSE,
                show_online_players BOOLEAN NOT NULL DEFAULT FALSE,
                show_ping BOOLEAN NOT NULL DEFAULT FALSE,
                show_biom BOOLEAN NOT NULL DEFAULT TRUE,
                show_weather BOOLEAN NOT NULL DEFAULT FALSE,
                show_condition BOOLEAN NOT NULL DEFAULT FALSE,
                show_event_countdown BOOLEAN NOT NULL DEFAULT FALSE,
                show_kills BOOLEAN NOT NULL DEFAULT FALSE,
                show_progress BOOLEAN NOT NULL DEFAULT TRUE,
                show_session_time BOOLEAN NOT NULL DEFAULT FALSE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (uuid) REFERENCES players(uuid) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);
    }

    public void initPlayer(OfflinePlayer player) {
        db().update("""
            INSERT IGNORE INTO scoreboard_settings (uuid, scoreboard_toggle, show_coins, show_playtime, show_deaths, show_coordinates, show_quests, show_online_players, show_ping, show_biom, show_weather, show_condition, show_event_countdown, show_kills, show_progress, show_session_time)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """, player.getUniqueId().toString(), true, false, true, false, true, false, false, false, true, false, false, false, false, true, false);
    }

    public boolean doesUserExist(OfflinePlayer player) {
        return repo().exists(TABLE, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public Instant getCreatedAt(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getInstant(TABLE, "created_at", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Instant> getCreatedAtAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getInstantAsync(TABLE, "created_at", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public Instant getUpdatedAt(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getInstant(TABLE, "updated_at", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Instant> getUpdatedAtAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getInstantAsync(TABLE, "updated_at", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setScoreboardToggle(OfflinePlayer player, boolean toggle) {
        ApiManagerValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "scoreboard_toggle", toggle, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setScoreboardToggleAsync(OfflinePlayer player, boolean toggle) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "scoreboard_toggle", toggle, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public boolean getScoreboardToggle(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getBoolean(TABLE, "scoreboard_toggle", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Boolean> getScoreboardToggleAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getBooleanAsync(TABLE, "scoreboard_toggle", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setShowCoins(OfflinePlayer player, boolean show) {
        ApiManagerValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "show_coins", show, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setShowCoinsAsync(OfflinePlayer player, boolean show) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "show_coins", show, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public boolean getShowCoins(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getBoolean(TABLE, "show_coins", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Boolean> getShowCoinsAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getBooleanAsync(TABLE, "show_coins", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setShowPlaytime(OfflinePlayer player, boolean show) {
        ApiManagerValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "show_playtime", show, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setShowPlaytimeAsync(OfflinePlayer player, boolean show) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "show_playtime", show, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public boolean getShowPlaytime(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getBoolean(TABLE, "show_playtime", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Boolean> getShowPlaytimeAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getBooleanAsync(TABLE, "show_playtime", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setShowDeaths(OfflinePlayer player, boolean show) {
        ApiManagerValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "show_deaths", show, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setShowDeathsAsync(OfflinePlayer player, boolean show) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "show_deaths", show, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public boolean getShowDeaths(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getBoolean(TABLE, "show_deaths", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Boolean> getShowDeathsAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getBooleanAsync(TABLE, "show_deaths", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setShowCoordinates(OfflinePlayer player, boolean show) {
        ApiManagerValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "show_coordinates", show, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setShowCoordinatesAsync(OfflinePlayer player, boolean show) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "show_coordinates", show, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public boolean getShowCoordinates(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getBoolean(TABLE, "show_coordinates", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Boolean> getShowCoordinatesAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getBooleanAsync(TABLE, "show_coordinates", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setShowQuests(OfflinePlayer player, boolean show) {
        ApiManagerValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "show_quests", show, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setShowQuestsAsync(OfflinePlayer player, boolean show) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "show_quests", show, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public boolean getShowQuests(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getBoolean(TABLE, "show_quests", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Boolean> getShowQuestsAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getBooleanAsync(TABLE, "show_quests", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setShowOnlinePlayers(OfflinePlayer player, boolean show) {
        ApiManagerValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "show_online_players", show, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setShowOnlinePlayersAsync(OfflinePlayer player, boolean show) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "show_online_players", show, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public boolean getShowOnlinePlayers(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getBoolean(TABLE, "show_online_players", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Boolean> getShowOnlinePlayersAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getBooleanAsync(TABLE, "show_online_players", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setShowPing(OfflinePlayer player, boolean show) {
        ApiManagerValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "show_ping", show, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setShowPingAsync(OfflinePlayer player, boolean show) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "show_ping", show, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public boolean getShowPing(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getBoolean(TABLE, "show_ping", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Boolean> getShowPingAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getBooleanAsync(TABLE, "show_ping", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setShowBiom(OfflinePlayer player, boolean show) {
        ApiManagerValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "show_biom", show, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setShowBiomAsync(OfflinePlayer player, boolean show) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "show_biom", show, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public boolean getShowBiom(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getBoolean(TABLE, "show_biom", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Boolean> getShowBiomAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getBooleanAsync(TABLE, "show_biom", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setShowWeather(OfflinePlayer player, boolean show) {
        ApiManagerValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "show_weather", show, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setShowWeatherAsync(OfflinePlayer player, boolean show) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "show_weather", show, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public boolean getShowWeather(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getBoolean(TABLE, "show_weather", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Boolean> getShowWeatherAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getBooleanAsync(TABLE, "show_weather", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setShowCondition(OfflinePlayer player, boolean show) {
        ApiManagerValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "show_condition", show, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setShowConditionAsync(OfflinePlayer player, boolean show) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "show_condition", show, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public boolean getShowCondition(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getBoolean(TABLE, "show_condition", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Boolean> getShowConditionAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getBooleanAsync(TABLE, "show_condition", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setShowEventCountdown(OfflinePlayer player, boolean show) {
        ApiManagerValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "show_event_countdown", show, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setShowEventCountdownAsync(OfflinePlayer player, boolean show) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "show_event_countdown", show, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public boolean getShowEventCountdown(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getBoolean(TABLE, "show_event_countdown", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Boolean> getShowEventCountdownAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getBooleanAsync(TABLE, "show_event_countdown", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setShowKills(OfflinePlayer player, boolean show) {
        ApiManagerValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "show_kills", show, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setShowKillsAsync(OfflinePlayer player, boolean show) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "show_kills", show, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public boolean getShowKills(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getBoolean(TABLE, "show_kills", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Boolean> getShowKillsAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getBooleanAsync(TABLE, "show_kills", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setShowProgress(OfflinePlayer player, boolean show) {
        ApiManagerValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "show_progress", show, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setShowProgressAsync(OfflinePlayer player, boolean show) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "show_progress", show, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public boolean getShowProgress(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getBoolean(TABLE, "show_progress", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Boolean> getShowProgressAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getBooleanAsync(TABLE, "show_progress", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setShowSessionTime(OfflinePlayer player, boolean show) {
        ApiManagerValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "show_session_time", show, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setShowSessionTimeAsync(OfflinePlayer player, boolean show) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "show_session_time", show, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public boolean getShowSessionTime(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getBoolean(TABLE, "show_session_time", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Boolean> getShowSessionTimeAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getBooleanAsync(TABLE, "show_session_time", "uuid = ?", player.getUniqueId().toString());
    }
}