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
        db().update(String.format("""
            CREATE TABLE IF NOT EXISTS %s (
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
        """, TABLE));
    }

    public void initPlayer(OfflinePlayer player) {
        db().update(String.format("""
            INSERT IGNORE INTO %s (uuid, scoreboard_toggle, show_coins, show_playtime, show_deaths, show_coordinates, show_quests, show_online_players, show_ping, show_biom, show_weather, show_condition, show_event_countdown, show_kills, show_progress, show_session_time)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """, TABLE), player.getUniqueId().toString(), true, false, true, false, true, false, false, false, true, false, false, false, false, true, false);
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
    public void setElement(OfflinePlayer player, ScoreboardElement element, boolean value) {
        ApiManagerValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, element.getColumn(), value, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setElementAsync(OfflinePlayer player, ScoreboardElement element, boolean value) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, element.getColumn(), value, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public boolean getElement(OfflinePlayer player, ScoreboardElement element) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getBoolean(TABLE, element.getColumn(), "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Boolean> getElementAsync(OfflinePlayer player, ScoreboardElement element) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getBooleanAsync(TABLE, element.getColumn(), "uuid = ?", player.getUniqueId().toString());
    }
}