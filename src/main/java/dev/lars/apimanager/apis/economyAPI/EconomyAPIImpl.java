package dev.lars.apimanager.apis.economyAPI;

import dev.lars.apimanager.ApiManager;
import dev.lars.apimanager.database.DatabaseRepository;
import dev.lars.apimanager.database.IDatabaseManager;
import dev.lars.apimanager.utils.ApiManagerValidateParameter;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class EconomyAPIImpl implements IEconomyAPI {
    private static final String TABLE = "player_economy";
    private static final String GIFT_TABLE = "player_gifts";

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
                balance BIGINT NOT NULL DEFAULT 0,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (uuid) REFERENCES players(uuid) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """, TABLE));

        db().update(String.format("""
            CREATE TABLE IF NOT EXISTS %s (
                id INT AUTO_INCREMENT PRIMARY KEY,
                uuid CHAR(36) NOT NULL,
                name VARCHAR(255) NOT NULL,
                value INT NOT NULL DEFAULT 0,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (uuid) REFERENCES players(uuid) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """, GIFT_TABLE));
    }

    @Override
    public void initPlayer(UUID uuid) {
        repo().insertIgnore(TABLE, new String[]{"uuid"}, uuid.toString());
    }

    @Override
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
    public Instant getGiftCreatedAt(int giftId) {
        return repo().getInstant(GIFT_TABLE, "created_at", "id = ?", giftId);
    }

    @Override
    public CompletableFuture<Instant> getGiftCreatedAtAsync(int giftId) {
        return repo().getInstantAsync(GIFT_TABLE, "created_at", "id = ?", giftId);
    }

    @Override
    public Instant getGiftUpdatedAt(int giftId) {
        return repo().getInstant(GIFT_TABLE, "updated_at", "id = ?", giftId);
    }

    @Override
    public CompletableFuture<Instant> getGiftUpdatedAtAsync(int giftId) {
        return repo().getInstantAsync(GIFT_TABLE, "updated_at", "id = ?", giftId);
    }

    @Override
    public void setBalance(OfflinePlayer player, long amount) {
        ApiManagerValidateParameter.validatePlayer(player);
        repo().updateColumn(TABLE, "balance", amount, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setBalanceAsync(OfflinePlayer player, long amount) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().updateColumnAsync(TABLE, "balance", amount, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void increaseBalance(OfflinePlayer player, long amount) {
        ApiManagerValidateParameter.validatePlayer(player);
        repo().increaseColumn(TABLE, "balance", amount, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> increaseBalanceAsync(OfflinePlayer player, long amount) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().increaseColumnAsync(TABLE, "balance", amount, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void decreaseBalance(OfflinePlayer player, long amount) {
        ApiManagerValidateParameter.validatePlayer(player);
        repo().decreaseColumn(TABLE, "balance", amount, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> decreaseBalanceAsync(OfflinePlayer player, long amount) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().decreaseColumnAsync(TABLE, "balance", amount, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public Long getBalance(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        Long balance = repo().getLong(TABLE, "balance", "uuid = ?", player.getUniqueId().toString());
        return balance != null ? balance : 0;
    }

    @Override
    public CompletableFuture<Long> getBalanceAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getLongAsync(TABLE, "balance", "uuid = ?", player.getUniqueId().toString())
                .thenApply(balance -> balance != null ? balance : 0);
    }

    private List<Gift> mapGifts(ResultSet rs) throws java.sql.SQLException {
        List<Gift> gifts = new ArrayList<>();
        while (rs.next()) {
            gifts.add(new Gift(rs.getInt("id"), rs.getString("name"), rs.getInt("value")));
        }
        return gifts;
    }

    @Override
    public void addGift(OfflinePlayer player, String name, int value) {
        ApiManagerValidateParameter.validatePlayer(player);
        repo().insert(GIFT_TABLE, new String[]{"uuid", "name", "value"},
                player.getUniqueId().toString(), name, value);
    }

    @Override
    public CompletableFuture<Void> addGiftAsync(OfflinePlayer player, String name, int value) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().insertAsync(GIFT_TABLE, new String[]{"uuid", "name", "value"},
                player.getUniqueId().toString(), name, value);
    }

    @Override
    public void removeGift(OfflinePlayer player, int giftId) {
        ApiManagerValidateParameter.validatePlayer(player);
        repo().delete(GIFT_TABLE, "id = ? AND uuid = ?", giftId, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> removeGiftAsync(OfflinePlayer player, int giftId) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().deleteAsync(GIFT_TABLE, "id = ? AND uuid = ?", giftId, player.getUniqueId().toString());
    }

    @Override
    public List<Gift> getGifts(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        String sql = String.format("SELECT id, name, value FROM %s WHERE uuid = ?", GIFT_TABLE);
        return db().query(conn -> {
            db().logSqlQuery(sql, player.getUniqueId().toString());
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    return mapGifts(rs);
                }
            }
        });
    }

    @Override
    public CompletableFuture<List<Gift>> getGiftsAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        String sql = String.format("SELECT id, name, value FROM %s WHERE uuid = ?", GIFT_TABLE);
        return db().queryAsync(conn -> {
            db().logSqlQuery(sql, player.getUniqueId().toString());
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    return mapGifts(rs);
                }
            }
        });
    }

    @Override
    public Gift getGiftByName(OfflinePlayer player, String name) {
        ApiManagerValidateParameter.validatePlayer(player);
        String sql = String.format("SELECT id, name, value FROM %s WHERE uuid = ? AND name = ? LIMIT 1", GIFT_TABLE);
        return db().query(conn -> {
            db().logSqlQuery(sql, player.getUniqueId().toString(), name);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, player.getUniqueId().toString());
                ps.setString(2, name);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return new Gift(rs.getInt("id"), rs.getString("name"), rs.getInt("value"));
                    return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Gift> getGiftByNameAsync(OfflinePlayer player, String name) {
        ApiManagerValidateParameter.validatePlayer(player);
        String sql = String.format("SELECT id, name, value FROM %s WHERE uuid = ? AND name = ? LIMIT 1", GIFT_TABLE);
        return db().queryAsync(conn -> {
            db().logSqlQuery(sql, player.getUniqueId().toString(), name);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, player.getUniqueId().toString());
                ps.setString(2, name);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return new Gift(rs.getInt("id"), rs.getString("name"), rs.getInt("value"));
                    return null;
                }
            }
        });
    }

    @Override
    public boolean hasGift(OfflinePlayer player, int giftId) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().exists(GIFT_TABLE, "id = ? AND uuid = ?", giftId, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Boolean> hasGiftAsync(OfflinePlayer player, int giftId) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().existsAsync(GIFT_TABLE, "id = ? AND uuid = ?", giftId, player.getUniqueId().toString());
    }

    @Override
    public int getGiftCount(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().count(GIFT_TABLE, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Integer> getGiftCountAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().countAsync(GIFT_TABLE, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public int getTotalGiftValue(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        String sql = String.format("SELECT COALESCE(SUM(value), 0) FROM %s WHERE uuid = ?", GIFT_TABLE);
        return db().query(conn -> {
            db().logSqlQuery(sql, player.getUniqueId().toString());
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? rs.getInt(1) : 0;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Integer> getTotalGiftValueAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        String sql = String.format("SELECT COALESCE(SUM(value), 0) FROM %s WHERE uuid = ?", GIFT_TABLE);
        return db().queryAsync(conn -> {
            db().logSqlQuery(sql, player.getUniqueId().toString());
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? rs.getInt(1) : 0;
                }
            }
        });
    }

    @Override
    public void resetGifts(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        repo().delete(GIFT_TABLE, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> resetGiftsAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().deleteAsync(GIFT_TABLE, "uuid = ?", player.getUniqueId().toString());
    }
}