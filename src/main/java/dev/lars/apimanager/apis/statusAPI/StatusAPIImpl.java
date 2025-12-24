package dev.lars.apimanager.apis.statusAPI;

import dev.lars.apimanager.ApiManager;
import dev.lars.apimanager.database.DatabaseRepository;
import dev.lars.apimanager.database.IDatabaseManager;
import dev.lars.apimanager.utils.ApiManagerTextFormation;
import dev.lars.apimanager.utils.ApiManagerValidateParameter;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class StatusAPIImpl implements IStatusAPI {
    private static final String TABLE = "player_status";

    private DatabaseRepository repo() {
        return new DatabaseRepository();
    }

    private IDatabaseManager db() {
        return ApiManager.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db().update("""
            CREATE TABLE IF NOT EXISTS player_status (
                uuid CHAR(36) NOT NULL PRIMARY KEY,
                status VARCHAR(255) NOT NULL DEFAULT '',
                color INT NOT NULL DEFAULT 0,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (uuid) REFERENCES players(uuid) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);
    }

    public void initPlayer(OfflinePlayer player) {
        db().update("""
            INSERT IGNORE INTO player_status (uuid, status, color)
            VALUES (?, ?, ?)
        """, player.getUniqueId().toString(), "", 0);
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
    public void setStatus(OfflinePlayer player, String status) {
        ApiManagerValidateParameter.validatePlayer(player);
        ApiManagerValidateParameter.validateStatus(status);
        repo().updateColumn(TABLE, "status", status, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setStatusAsync(OfflinePlayer player, String status) {
        ApiManagerValidateParameter.validatePlayer(player);
        ApiManagerValidateParameter.validateStatus(status);
        return repo().updateColumnAsync(TABLE, "status", status, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public String getStatus(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getString(TABLE, "status", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<String> getStatusAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getStringAsync(TABLE, "status", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setColor(OfflinePlayer player, NamedTextColor color) {
        ApiManagerValidateParameter.validatePlayer(player);
        ApiManagerValidateParameter.validateNamedTextColor(color);
        repo().updateColumn(TABLE, "color", ApiManagerTextFormation.getColorId(color), "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setColorAsync(OfflinePlayer player, NamedTextColor color) {
        ApiManagerValidateParameter.validatePlayer(player);
        ApiManagerValidateParameter.validateNamedTextColor(color);
        return repo().updateColumnAsync(TABLE, "color", ApiManagerTextFormation.getColorId(color), "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public NamedTextColor getColor(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        Integer colorId = repo().getInteger(TABLE, "color", "uuid = ?", player.getUniqueId().toString());
        return colorId != null ? ApiManagerTextFormation.getNamedTextColor(colorId) : null;
    }

    @Override
    public CompletableFuture<NamedTextColor> getColorAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getIntegerAsync(TABLE, "color", "uuid = ?", player.getUniqueId().toString())
            .thenApply(colorId -> colorId != null ? ApiManagerTextFormation.getNamedTextColor(colorId) : null);
    }
}