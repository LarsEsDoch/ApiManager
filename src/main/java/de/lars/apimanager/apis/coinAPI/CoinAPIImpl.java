package de.lars.apimanager.apis.coinAPI;

import de.lars.apimanager.Main;
import de.lars.apimanager.database.DatabaseManager;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class CoinAPIImpl implements ICoinAPI {
    private final DatabaseManager db;

    public CoinAPIImpl() {
        this.db = Main.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db.update("""
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
        db.update("""
            INSERT IGNORE INTO player_coins (uuid, coins, gifts)
            VALUES (?, ?, ?)
        """, player.getUniqueId().toString(), 0, "");
    }

    public boolean doesUserExist(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM player_coins WHERE uuid=? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        });
    }

    @Override
    public Timestamp getCreatedAt(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                     "SELECT created_at FROM player_coins WHERE uuid = ?"
                 )) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("created_at");
                    return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Timestamp> getCreatedAtAsync(OfflinePlayer player) {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                     "SELECT created_at FROM player_coins WHERE uuid = ?"
                 )) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("created_at");
                    return null;
                }
            }
        });
    }

    @Override
    public Timestamp getUpdatedAt(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                     "SELECT updated_at FROM player_coins WHERE uuid = ?"
                 )) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("updated_at");
                    return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Timestamp> getUpdatedAtAsync(OfflinePlayer player) {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                     "SELECT updated_at FROM player_coins WHERE uuid = ?"
                 )) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("updated_at");
                    return null;
                }
            }
        });
    }

    @Override
    public void setCoins(OfflinePlayer player, int amount) {
        db.update("UPDATE player_coins SET coins=? WHERE uuid=?", amount, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setCoinsAsync(OfflinePlayer player, int amount) {
        return db.updateAsync("UPDATE player_coins SET coins=? WHERE uuid=?", amount, player.getUniqueId().toString());
    }

    @Override
    public void addCoins(OfflinePlayer player, int amount) {
        db.update("UPDATE player_coins SET coins = coins + ? WHERE uuid=?", amount, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> addCoinsAsync(OfflinePlayer player, int amount) {
        return db.updateAsync("UPDATE player_coins SET coins = coins + ? WHERE uuid=?", amount, player.getUniqueId().toString());
    }

    @Override
    public void removeCoins(OfflinePlayer player, int amount) {
        db.update("UPDATE player_coins SET coins = GREATEST(coins - ?, 0) WHERE uuid=?", amount, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> removeCoinsAsync(OfflinePlayer player, int amount) {
        return db.updateAsync("UPDATE player_coins SET coins = GREATEST(coins - ?, 0) WHERE uuid=?", amount, player.getUniqueId().toString());
    }

    @Override
    public Integer getCoins(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT coins FROM player_coins WHERE uuid=?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("coins");
                    return 0;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Integer> getCoinsAsync(OfflinePlayer player) {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT coins FROM player_coins WHERE uuid=?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("coins");
                    return 0;
                }
            }
        });
    }

    @Override
    public List<String> getGifts(OfflinePlayer player) {
        return db.query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT gifts FROM player_coins WHERE uuid=?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String gifts = rs.getString("gifts");
                        if (gifts != null && !gifts.isEmpty()) {
                            return new ArrayList<>(Arrays.asList(gifts.split(",")));
                        }
                    }
                    return new ArrayList<String>();
                }
            }
        });
    }

    @Override
    public void addGift(OfflinePlayer player, String gift) {
        List<String> gifts = new ArrayList<>(getGifts(player));
        gifts.add(gift);
        String giftString = String.join(",", gifts);
        db.update("UPDATE player_coins SET gifts=? WHERE uuid=?", giftString, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> addGiftAsync(OfflinePlayer player, String gift) {
        return getGiftsAsync(player).thenCompose(gifts -> {
            gifts.add(gift);
            String giftString = String.join(",", gifts);
            return db.updateAsync("UPDATE player_coins SET gifts=? WHERE uuid=?", giftString, player.getUniqueId().toString());
        });
    }

    @Override
    public void removeGift(OfflinePlayer player, String gift) {
        List<String> gifts = new ArrayList<>(getGifts(player));
        gifts.remove(gift);
        String giftString = String.join(",", gifts);
        db.update("UPDATE player_coins SET gifts=? WHERE uuid=?", giftString, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> removeGiftAsync(OfflinePlayer player, String gift) {
        return getGiftsAsync(player).thenCompose(gifts -> {
            gifts.remove(gift);
            String giftString = String.join(",", gifts);
            return db.updateAsync("UPDATE player_coins SET gifts=? WHERE uuid=?", giftString, player.getUniqueId().toString());
        });
    }

    @Override
    public CompletableFuture<List<String>> getGiftsAsync(OfflinePlayer player) {
        return db.queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT gifts FROM player_coins WHERE uuid=?")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String gifts = rs.getString("gifts");
                        if (gifts != null && !gifts.isEmpty()) {
                            return new ArrayList<>(Arrays.asList(gifts.split(",")));
                        }
                    }
                    return new ArrayList<String>();
                }
            }
        });
    }
}