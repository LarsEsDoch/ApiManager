package de.lars.apimanager.apis.timerAPI;

import de.lars.apimanager.ApiManager;
import de.lars.apimanager.database.DatabaseRepository;
import de.lars.apimanager.database.IDatabaseManager;
import de.lars.apimanager.utils.ValidateParameter;
import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class TimerAPIImpl implements ITimerAPI {
    private static final String TABLE = "player_timers";

    private DatabaseRepository repo() {
        return new DatabaseRepository();
    }

    private IDatabaseManager db() {
        return ApiManager.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db().update("""
            CREATE TABLE IF NOT EXISTS player_timers (
                uuid CHAR(36) NOT NULL PRIMARY KEY,
                time INT NOT NULL DEFAULT 0,
                enabled BOOLEAN NOT NULL DEFAULT FALSE,
                public_timer BOOLEAN NOT NULL DEFAULT FALSE,
                running BOOLEAN NOT NULL DEFAULT FALSE,
                timer_mode_enabled BOOLEAN NOT NULL DEFAULT FALSE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (uuid) REFERENCES players(uuid) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);
    }

    public void initPlayer(OfflinePlayer player) {
        db().update("""
            INSERT IGNORE INTO player_timers (uuid, time, enabled, public_timer, running, timer_mode_enabled)
            VALUES (?, ?, ?, ?, ?, ?)
        """, player.getUniqueId().toString(), 0, false, false, false, false);
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
    public void setTime(OfflinePlayer player, int time) {
        ValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "time", time, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setTimeAsync(OfflinePlayer player, int time) {
        ValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "time", time, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setEnabled(OfflinePlayer player, boolean enabled) {
        ValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "enabled", enabled, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setEnabledAsync(OfflinePlayer player, boolean enabled) {
        ValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "enabled", enabled, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setRunning(OfflinePlayer player, boolean running) {
        ValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "running", running, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setRunningAsync(OfflinePlayer player, boolean running) {
        ValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "running", running, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setTimer(OfflinePlayer player, boolean timer) {
        ValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "timer_mode_enabled", timer, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setTimerAsync(OfflinePlayer player, boolean timer) {
        ValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "timer_mode_enabled", timer, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setPublic(OfflinePlayer player, boolean isPublic) {
        ValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "public_timer", isPublic, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setPublicAsync(OfflinePlayer player, boolean isPublic) {
        ValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "public_timer", isPublic, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public Integer getTime(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getInteger(TABLE, "time", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Integer> getTimeAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getIntegerAsync(TABLE, "time", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public boolean isPublic(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        Boolean result = repo().getBoolean(TABLE, "public_timer", "uuid = ?", player.getUniqueId().toString());
        return result != null && result;
    }

    @Override
    public CompletableFuture<Boolean> isPublicAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getBooleanAsync(TABLE, "public_timer", "uuid = ?", player.getUniqueId().toString())
            .thenApply(result -> result != null && result);
    }

    @Override
    public boolean isEnabled(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        Boolean result = repo().getBoolean(TABLE, "enabled", "uuid = ?", player.getUniqueId().toString());
        return result != null && result;
    }

    @Override
    public CompletableFuture<Boolean> isEnabledAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getBooleanAsync(TABLE, "enabled", "uuid = ?", player.getUniqueId().toString())
            .thenApply(result -> result != null && result);
    }

    @Override
    public boolean isRunning(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        Boolean result = repo().getBoolean(TABLE, "running", "uuid = ?", player.getUniqueId().toString());
        return result != null && result;
    }

    @Override
    public CompletableFuture<Boolean> isRunningAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getBooleanAsync(TABLE, "running", "uuid = ?", player.getUniqueId().toString())
            .thenApply(result -> result != null && result);
    }

    @Override
    public boolean isTimerEnabled(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        Boolean result = repo().getBoolean(TABLE, "timer_mode_enabled", "uuid = ?", player.getUniqueId().toString());
        return result != null && result;
    }

    @Override
    public CompletableFuture<Boolean> isTimerEnabledAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getBooleanAsync(TABLE, "timer_mode_enabled", "uuid = ?", player.getUniqueId().toString())
            .thenApply(result -> result != null && result);
    }

    @Override
    public boolean publicTimerExists(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        int count = repo().count(TABLE, "uuid = ? AND public_timer = 1", player.getUniqueId().toString());
        return count > 0;
    }

    @Override
    public CompletableFuture<Boolean> publicTimerExistsAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().countAsync(TABLE, "uuid = ? AND public_timer = 1", player.getUniqueId().toString())
            .thenApply(count -> count != null && count > 0);
    }
}