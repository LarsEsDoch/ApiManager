package dev.lars.apimanager.apis.playerAPI;

import dev.lars.apimanager.ApiManager;
import dev.lars.apimanager.database.DatabaseRepository;
import dev.lars.apimanager.database.IDatabaseManager;
import dev.lars.apimanager.utils.ApiManagerValidateParameter;
import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class PlayerAPIImpl implements IPlayerAPI {
    private static final String TABLE = "players";

    private DatabaseRepository repo() {
        return new DatabaseRepository();
    }

    private IDatabaseManager db() {
        return ApiManager.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db().update("""
            CREATE TABLE IF NOT EXISTS players (
                uuid CHAR(36) NOT NULL PRIMARY KEY,
                name VARCHAR(16) NOT NULL DEFAULT '',
                playtime BIGINT NOT NULL DEFAULT 0,
                is_online BOOLEAN NOT NULL DEFAULT FALSE,
                first_join TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                last_seen TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);
    }

    public void initPlayer(OfflinePlayer player) {
        db().update("""
            INSERT IGNORE INTO players (uuid, name, playtime, is_online)
            VALUES (?, ?, ?, ?)
        """, player.getUniqueId().toString(), player.getName(), 0, false);
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
    public String getName(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getString(TABLE, "name", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<String> getNameAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getStringAsync(TABLE, "name", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setPlaytime(OfflinePlayer player, int playtime) {
        ApiManagerValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "playtime", playtime, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setPlaytimeAsync(OfflinePlayer player, int playtime) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "playtime", playtime, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public Integer getPlaytime(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getInteger(TABLE, "playtime", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Integer> getPlaytimeAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getIntegerAsync(TABLE, "playtime", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setOnline(OfflinePlayer player, boolean online) {
        ApiManagerValidateParameter.validatePlayer(player);
        if (!online) {
            repo().updateColumns(TABLE,
                new String[]{"is_online", "name", "last_seen"},
                new Object[]{false, player.getName(), Instant.now()},
                "uuid = ?", player.getUniqueId().toString());
        } else {
            repo().updateColumns(TABLE,
                new String[]{"is_online", "name"},
                new Object[]{true, player.getName()},
                "uuid = ?", player.getUniqueId().toString());
        }
    }

    @Override
    public CompletableFuture<Void> setOnlineAsync(OfflinePlayer player, boolean online) {
        ApiManagerValidateParameter.validatePlayer(player);
        if (!online) {
            return repo().updateColumnsAsync(TABLE,
                new String[]{"is_online", "name", "last_seen"},
                new Object[]{false, player.getName(), Instant.now()},
                "uuid = ?", player.getUniqueId().toString());
        } else {
            return repo().updateColumnsAsync(TABLE,
                new String[]{"is_online", "name"},
                new Object[]{true, player.getName()},
                "uuid = ?", player.getUniqueId().toString());
        }
    }

    @Override
    public boolean isOnline(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        Boolean result = repo().getBoolean(TABLE, "is_online", "uuid = ?", player.getUniqueId().toString());
        return result != null && result;
    }

    @Override
    public CompletableFuture<Boolean> isOnlineAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getBooleanAsync(TABLE, "is_online", "uuid = ?", player.getUniqueId().toString())
            .thenApply(result -> result != null && result);
    }

    @Override
    public Instant getLastSeen(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getInstant(TABLE, "last_seen", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Instant> getLastSeenAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getInstantAsync(TABLE, "last_seen", "uuid = ?", player.getUniqueId().toString());
    }
}