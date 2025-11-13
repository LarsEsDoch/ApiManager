package dev.lars.apimanager.apis.limitAPI;

import dev.lars.apimanager.ApiManager;
import dev.lars.apimanager.database.DatabaseRepository;
import dev.lars.apimanager.database.IDatabaseManager;
import dev.lars.apimanager.utils.ApiManagerValidateParameter;
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
                backpack_slots INT DEFAULT 9,
                max_chunks INT DEFAULT 32,
                max_homes INT DEFAULT 32,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (uuid) REFERENCES players(uuid) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);
    }

    public void initPlayer(OfflinePlayer player) {
        db().update("""
            INSERT IGNORE INTO player_limits (uuid, backpack_slots, max_chunks, max_homes)
            VALUES (?, ?, ?, ?)
        """, player.getUniqueId().toString(), 9, 32, 32);
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
    public void setBackpackSlots(OfflinePlayer player, int backpack_slots) {
        ApiManagerValidateParameter.validatePlayer(player);
        if (backpack_slots < 0) backpack_slots = 0;
        repo().updateColumn(TABLE, "backpack_slots", backpack_slots, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setBackpackSlotsAsync(OfflinePlayer player, int backpack_slots) {
        ApiManagerValidateParameter.validatePlayer(player);
        if (backpack_slots < 0) backpack_slots = 0;
        return repo().updateColumnAsync(TABLE, "backpack_slots", backpack_slots, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void increaseBackpackSlots(OfflinePlayer player, int amount) {
        ApiManagerValidateParameter.validatePlayer(player);
        Integer current = getBackpackSlots(player);
        if (current == null) current = 0;
        setBackpackSlots(player, current + amount);
    }

    @Override
    public CompletableFuture<Void> increaseBackpackSlotsAsync(OfflinePlayer player, int amount) {
        ApiManagerValidateParameter.validatePlayer(player);
        return getBackpackSlotsAsync(player).thenCompose(current -> {
            if (current == null) current = 0;
            return setBackpackSlotsAsync(player, current + amount);
        });
    }

    @Override
    public void decreaseBackpackSlots(OfflinePlayer player, int amount) {
        ApiManagerValidateParameter.validatePlayer(player);
        Integer current = getBackpackSlots(player);
        if (current == null) current = 0;
        setBackpackSlots(player, Math.max(0, current - amount));
    }

    @Override
    public CompletableFuture<Void> decreaseBackpackSlotsAsync(OfflinePlayer player, int amount) {
        ApiManagerValidateParameter.validatePlayer(player);
        return getBackpackSlotsAsync(player).thenCompose(current -> {
            if (current == null) current = 0;
            return setBackpackSlotsAsync(player, Math.max(0, current - amount));
        });
    }

    @Override
    public Integer getBackpackSlots(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getInteger(TABLE, "backpack_slots", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Integer> getBackpackSlotsAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getIntegerAsync(TABLE, "backpack_slots", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setMaxChunks(OfflinePlayer player, Integer max_chunks) {
        ApiManagerValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "max_chunks", max_chunks, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setMaxChunksAsync(OfflinePlayer player, Integer max_chunks) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "max_chunks", max_chunks, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void increaseMaxChunks(OfflinePlayer player, int amount) {
        ApiManagerValidateParameter.validatePlayer(player);
        Integer current = getMaxChunks(player);
        if (current == null) current = 0;
        setMaxChunks(player, current + amount);
    }

    @Override
    public CompletableFuture<Void> increaseMaxChunksAsync(OfflinePlayer player, int amount) {
        ApiManagerValidateParameter.validatePlayer(player);
        return getMaxChunksAsync(player).thenCompose(current -> {
            if (current == null) current = 0;
            return setMaxChunksAsync(player, current + amount);
        });
    }

    @Override
    public void decreaseMaxChunks(OfflinePlayer player, int amount) {
        ApiManagerValidateParameter.validatePlayer(player);
        Integer current = getMaxChunks(player);
        if (current == null) current = 0;
        setMaxChunks(player, Math.max(0, current - amount));
    }

    @Override
    public CompletableFuture<Void> decreaseMaxChunksAsync(OfflinePlayer player, int amount) {
        ApiManagerValidateParameter.validatePlayer(player);
        return getMaxChunksAsync(player).thenCompose(current -> {
            if (current == null) current = 0;
            return setMaxChunksAsync(player, Math.max(0, current - amount));
        });
    }

    @Override
    public Integer getMaxChunks(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getInteger(TABLE, "max_chunks", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Integer> getMaxChunksAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getIntegerAsync(TABLE, "max_chunks", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setMaxHomes(OfflinePlayer player, Integer max_homes) {
        ApiManagerValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "max_homes", max_homes, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setMaxHomesAsync(OfflinePlayer player, Integer max_homes) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "max_homes", max_homes, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void increaseMaxHomes(OfflinePlayer player, int amount) {
        ApiManagerValidateParameter.validatePlayer(player);
        Integer current = getMaxHomes(player);
        if (current == null) return;
        setMaxHomes(player, current + amount);
    }

    @Override
    public CompletableFuture<Void> increaseMaxHomesAsync(OfflinePlayer player, int amount) {
        ApiManagerValidateParameter.validatePlayer(player);
        return getMaxHomesAsync(player).thenCompose(current -> {
            if (current == null) return CompletableFuture.completedFuture(null);
            return setMaxHomesAsync(player, current + amount);
        });
    }

    @Override
    public void decreaseMaxHomes(OfflinePlayer player, int amount) {
        ApiManagerValidateParameter.validatePlayer(player);
        Integer current = getMaxHomes(player);
        if (current == null) return;
        setMaxHomes(player, Math.max(0, current - amount));
    }

    @Override
    public CompletableFuture<Void> decreaseMaxHomesAsync(OfflinePlayer player, int amount) {
        ApiManagerValidateParameter.validatePlayer(player);
        return getMaxHomesAsync(player).thenCompose(current -> {
            if (current == null) return CompletableFuture.completedFuture(null);
            return setMaxHomesAsync(player, Math.max(0, current - amount));
        });
    }

    @Override
    public Integer getMaxHomes(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getInteger(TABLE, "max_homes", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Integer> getMaxHomesAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getIntegerAsync(TABLE, "max_homes", "uuid = ?", player.getUniqueId().toString());
    }
}