package de.lars.apimanager.apis.coinAPI;

import de.lars.apimanager.ApiManager;
import de.lars.apimanager.database.DatabaseRepository;
import de.lars.apimanager.database.IDatabaseManager;
import de.lars.apimanager.utils.ValidateParameter;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class CoinAPIImpl implements ICoinAPI {
    private static final String TABLE = "player_coins";

    private DatabaseRepository repo() {
        return new DatabaseRepository();
    }

    private IDatabaseManager db() {
        return ApiManager.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db().update("""
            CREATE TABLE IF NOT EXISTS player_coins (
                uuid CHAR(36) NOT NULL PRIMARY KEY,
                coins INT DEFAULT 0,
                gifts VARCHAR(255) DEFAULT '',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (uuid) REFERENCES players(uuid) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);
    }

    public void initPlayer(OfflinePlayer player) {
        db().update("""
            INSERT IGNORE INTO player_coins (uuid, coins, gifts)
            VALUES (?, ?, ?)
        """, player.getUniqueId().toString(), 0, "");
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
    public void setCoins(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "coins", amount, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setCoinsAsync(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "coins", amount, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void addCoins(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        db().update("UPDATE player_coins SET coins = coins + ? WHERE uuid=?", amount, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> addCoinsAsync(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        return db().updateAsync("UPDATE player_coins SET coins = coins + ? WHERE uuid=?", amount, player.getUniqueId().toString());
    }

    @Override
    public void removeCoins(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        db().update("UPDATE player_coins SET coins = GREATEST(coins - ?, 0) WHERE uuid=?", amount, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> removeCoinsAsync(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        return db().updateAsync("UPDATE player_coins SET coins = GREATEST(coins - ?, 0) WHERE uuid=?", amount, player.getUniqueId().toString());
    }

    @Override
    public Integer getCoins(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        Integer coins = repo().getInteger(TABLE, "coins", "uuid = ?", player.getUniqueId().toString());
        return coins != null ? coins : 0;
    }

    @Override
    public CompletableFuture<Integer> getCoinsAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getIntegerAsync(TABLE, "coins", "uuid = ?", player.getUniqueId().toString())
            .thenApply(coins -> coins != null ? coins : 0);
    }

    @Override
    public void addGift(OfflinePlayer player, int gift) {
        ValidateParameter.validatePlayer(player);
        List<Integer> gifts = new ArrayList<>(getGifts(player));
        gifts.add(gift);
        String giftString = gifts.stream().map(String::valueOf).collect(Collectors.joining(","));
        repo().updateColumn(TABLE, "gifts", giftString, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> addGiftAsync(OfflinePlayer player, int gift) {
        ValidateParameter.validatePlayer(player);
        return getGiftsAsync(player).thenCompose(gifts -> {
            gifts.add(gift);
            String giftString = gifts.stream().map(String::valueOf).collect(Collectors.joining(","));
            return repo().updateColumnAsync(TABLE, "gifts", giftString, "uuid = ?", player.getUniqueId().toString());
        });
    }

    @Override
    public void removeGift(OfflinePlayer player, int gift) {
        ValidateParameter.validatePlayer(player);
        List<Integer> gifts = new ArrayList<>(getGifts(player));
        gifts.remove(Integer.valueOf(gift));
        String giftString = gifts.stream().map(String::valueOf).collect(Collectors.joining(","));
        repo().updateColumn(TABLE, "gifts", giftString, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> removeGiftAsync(OfflinePlayer player, int gift) {
        ValidateParameter.validatePlayer(player);
        return getGiftsAsync(player).thenCompose(gifts -> {
            gifts.remove(Integer.valueOf(gift));
            String giftString = gifts.stream().map(String::valueOf).collect(Collectors.joining(","));
            return repo().updateColumnAsync(TABLE, "gifts", giftString, "uuid = ?", player.getUniqueId().toString());
        });
    }

    @Override
    public List<Integer> getGifts(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db().query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT gifts FROM player_coins WHERE uuid=?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String gifts = rs.getString("gifts");
                        if (gifts != null && !gifts.isEmpty()) {
                            return Arrays.stream(gifts.split(","))
                                    .map(String::trim)
                                    .filter(s -> !s.isEmpty())
                                    .map(Integer::parseInt)
                                    .collect(Collectors.toList());
                        }
                    }
                    return new ArrayList<>();
                }
            }
        });
    }

    @Override
    public CompletableFuture<List<Integer>> getGiftsAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db().queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT gifts FROM player_coins WHERE uuid=?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String gifts = rs.getString("gifts");
                        if (gifts != null && !gifts.isEmpty()) {
                            return Arrays.stream(gifts.split(","))
                                    .map(String::trim)
                                    .filter(s -> !s.isEmpty())
                                    .map(Integer::parseInt)
                                    .collect(Collectors.toList());
                        }
                    }
                    return new ArrayList<>();
                }
            }
        });
    }
}