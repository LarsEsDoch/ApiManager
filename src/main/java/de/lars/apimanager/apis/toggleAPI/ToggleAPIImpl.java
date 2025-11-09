package de.lars.apimanager.apis.toggleAPI;

import de.lars.apimanager.ApiManager;
import de.lars.apimanager.database.DatabaseRepository;
import de.lars.apimanager.database.IDatabaseManager;
import de.lars.apimanager.utils.ValidateParameter;
import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class ToggleAPIImpl implements IToggleAPI {
    private static final String TABLE = "player_toggles";

    private DatabaseRepository repo() {
        return new DatabaseRepository();
    }

    private IDatabaseManager db() {
        return ApiManager.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db().update("""
            CREATE TABLE IF NOT EXISTS player_toggles (
                uuid CHAR(36) NOT NULL PRIMARY KEY,
                bed_toggle BOOLEAN DEFAULT TRUE,
                scoreboard_toggle BOOLEAN DEFAULT TRUE,
                FOREIGN KEY (uuid) REFERENCES players(uuid) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);
    }

    public void initPlayer(OfflinePlayer player) {
        db().update("""
            INSERT IGNORE INTO player_toggles (uuid, bed_toggle, scoreboard_toggle)
            VALUES (?, ?, ?)
        """, player.getUniqueId().toString(), true, true);
    }

    public boolean doesUserExist(OfflinePlayer player) {
        return repo().exists(TABLE, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public Instant getCreatedAt(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getInstant(TABLE, "created_at", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Instant> getCreatedAtAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getInstantAsync(TABLE, "created_at", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public Instant getUpdatedAt(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getInstant(TABLE, "updated_at", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Instant> getUpdatedAtAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getInstantAsync(TABLE, "updated_at", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setBedToggle(OfflinePlayer player, boolean toggle) {
        ValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "bed_toggle", toggle, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setBedToggleAsync(OfflinePlayer player, boolean toggle) {
        ValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "bed_toggle", toggle, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public boolean getBedToggle(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        Boolean result = repo().getBoolean(TABLE, "bed_toggle", "uuid = ?", player.getUniqueId().toString());
        return result == null || result;
    }

    @Override
    public CompletableFuture<Boolean> getBedToggleAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getBooleanAsync(TABLE, "bed_toggle", "uuid = ?", player.getUniqueId().toString())
            .thenApply(result -> result == null || result);
    }

    @Override
    public void setScoreboardToggle(OfflinePlayer player, boolean toggle) {
        ValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "scoreboard_toggle", toggle, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setScoreboardToggleAsync(OfflinePlayer player, boolean toggle) {
        ValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "scoreboard_toggle", toggle, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public boolean getScoreboardToggle(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        Boolean result = repo().getBoolean(TABLE, "scoreboard_toggle", "uuid = ?", player.getUniqueId().toString());
        return result == null || result;
    }

    @Override
    public CompletableFuture<Boolean> getScoreboardToggleAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getBooleanAsync(TABLE, "scoreboard_toggle", "uuid = ?", player.getUniqueId().toString())
            .thenApply(result -> result == null || result);
    }
}