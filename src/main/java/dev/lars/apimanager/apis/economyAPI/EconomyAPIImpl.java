package dev.lars.apimanager.apis.economyAPI;

import dev.lars.apimanager.ApiManager;
import dev.lars.apimanager.database.DatabaseRepository;
import dev.lars.apimanager.database.IDatabaseManager;
import dev.lars.apimanager.utils.ValidateParameter;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class EconomyAPIImpl implements IEconomyAPI {
    private static final String TABLE = "player_economy";

    private DatabaseRepository repo() {
        return new DatabaseRepository();
    }

    private IDatabaseManager db() {
        return ApiManager.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db().update("""
            CREATE TABLE IF NOT EXISTS player_economy (
                uuid CHAR(36) NOT NULL PRIMARY KEY,
                balance INT DEFAULT 0,
                gifts VARCHAR(255) DEFAULT '',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (uuid) REFERENCES players(uuid) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);
    }

    public void initPlayer(OfflinePlayer player) {
        db().update("""
            INSERT IGNORE INTO player_economy (uuid, balance, gifts)
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
    public void setBalance(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "balance", amount, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setBalanceAsync(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "balance", amount, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void increaseBalance(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        repo().increaseColumn(TABLE, "balance", amount, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> increaseBalanceAsync(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        return repo().increaseColumnAsync(TABLE, "balance", amount, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void decreaseBalance(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        repo().decreaseColumn(TABLE, "balance", amount, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> decreaseBalanceAsync(OfflinePlayer player, int amount) {
        ValidateParameter.validatePlayer(player);
        return repo().decreaseColumnAsync(TABLE, "balance", amount, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public Integer getBalance(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        Integer balance = repo().getInteger(TABLE, "balance", "uuid = ?", player.getUniqueId().toString());
        return balance != null ? balance : 0;
    }

    @Override
    public CompletableFuture<Integer> getBalanceAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getIntegerAsync(TABLE, "balance", "uuid = ?", player.getUniqueId().toString())
            .thenApply(balance -> balance != null ? balance : 0);
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
            try (PreparedStatement ps = conn.prepareStatement("SELECT gifts FROM player_economy WHERE uuid=?")) {
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
            try (PreparedStatement ps = conn.prepareStatement("SELECT gifts FROM player_economy WHERE uuid=?")) {
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