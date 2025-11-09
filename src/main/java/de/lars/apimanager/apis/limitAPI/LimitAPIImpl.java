package de.lars.apimanager.apis.limitAPI;

import de.lars.apimanager.ApiManager;
import de.lars.apimanager.database.DatabaseRepository;
import de.lars.apimanager.database.IDatabaseManager;
import de.lars.apimanager.utils.ValidateParameter;
import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class LimitAPIImpl implements ILimitAPI {
    private static final String TABLE = "player_limits";

    private DatabaseRepository repo() {
        return new DatabaseRepository();
    }

    private IDatabaseManager db() {
        return ApiManager.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db().update("""
            CREATE TABLE IF NOT EXISTS player_limits (
                uuid CHAR(36) NOT NULL PRIMARY KEY,
                slots INT DEFAULT 9,
                chunk_limit INT DEFAULT 32,
                home_limit INT DEFAULT 32,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (uuid) REFERENCES players(uuid) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);
    }

    public void initPlayer(OfflinePlayer player) {
        db().update("""
            INSERT IGNORE INTO player_limits (uuid, slots, chunk_limit, home_limit)
            VALUES (?, ?, ?, ?)
        """, player.getUniqueId().toString(), 9, 32, 32);
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
    public void setSlots(OfflinePlayer player, int slots) {
        ValidateParameter.validatePlayer(player);
        if (slots < 0) slots = 0;
        repo().updateColumn(TABLE, "slots", slots, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setSlotsAsync(OfflinePlayer player, int slots) {
        ValidateParameter.validatePlayer(player);
        if (slots < 0) slots = 0;
        return repo().updateColumnAsync(TABLE, "slots", slots, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void increaseSlots(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        Integer current = getSlots(player);
        if (current == null) current = 0;
        setSlots(player, current + amount);
    }

    @Override
    public CompletableFuture<Void> increaseSlotsAsync(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        return getSlotsAsync(player).thenCompose(current -> {
            if (current == null) current = 0;
            return setSlotsAsync(player, current + amount);
        });
    }

    @Override
    public void decreaseSlots(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        Integer current = getSlots(player);
        if (current == null) current = 0;
        setSlots(player, Math.max(0, current - amount));
    }

    @Override
    public CompletableFuture<Void> decreaseSlotsAsync(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        return getSlotsAsync(player).thenCompose(current -> {
            if (current == null) current = 0;
            return setSlotsAsync(player, Math.max(0, current - amount));
        });
    }

    @Override
    public Integer getSlots(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getInteger(TABLE, "slots", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Integer> getSlotsAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getIntegerAsync(TABLE, "slots", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setChunkLimit(OfflinePlayer player, Integer chunk_limit) {
        ValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "chunk_limit", chunk_limit, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setChunkLimitAsync(OfflinePlayer player, Integer chunk_limit) {
        ValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "chunk_limit", chunk_limit, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void increaseChunkLimit(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        Integer current = getChunkLimit(player);
        if (current == null) current = 0;
        setChunkLimit(player, current + amount);
    }

    @Override
    public CompletableFuture<Void> increaseChunkLimitAsync(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        return getChunkLimitAsync(player).thenCompose(current -> {
            if (current == null) current = 0;
            return setChunkLimitAsync(player, current + amount);
        });
    }

    @Override
    public void decreaseChunkLimit(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        Integer current = getChunkLimit(player);
        if (current == null) current = 0;
        setChunkLimit(player, Math.max(0, current - amount));
    }

    @Override
    public CompletableFuture<Void> decreaseChunkLimitAsync(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        return getChunkLimitAsync(player).thenCompose(current -> {
            if (current == null) current = 0;
            return setChunkLimitAsync(player, Math.max(0, current - amount));
        });
    }

    @Override
    public Integer getChunkLimit(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getInteger(TABLE, "chunk_limit", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Integer> getChunkLimitAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getIntegerAsync(TABLE, "chunk_limit", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setHomeLimit(OfflinePlayer player, Integer home_limit) {
        ValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "home_limit", home_limit, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setHomeLimitAsync(OfflinePlayer player, Integer home_limit) {
        ValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "home_limit", home_limit, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void increaseHomeLimit(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        Integer current = getHomeLimit(player);
        if (current == null) return;
        setHomeLimit(player, current + amount);
    }

    @Override
    public CompletableFuture<Void> increaseHomeLimitAsync(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        return getHomeLimitAsync(player).thenCompose(current -> {
            if (current == null) return CompletableFuture.completedFuture(null);
            return setHomeLimitAsync(player, current + amount);
        });
    }

    @Override
    public void decreaseHomeLimit(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        Integer current = getHomeLimit(player);
        if (current == null) return;
        setHomeLimit(player, Math.max(0, current - amount));
    }

    @Override
    public CompletableFuture<Void> decreaseHomeLimitAsync(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        return getHomeLimitAsync(player).thenCompose(current -> {
            if (current == null) return CompletableFuture.completedFuture(null);
            return setHomeLimitAsync(player, Math.max(0, current - amount));
        });
    }

    @Override
    public Integer getHomeLimit(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getInteger(TABLE, "home_limit", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Integer> getHomeLimitAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getIntegerAsync(TABLE, "home_limit", "uuid = ?", player.getUniqueId().toString());
    }
}