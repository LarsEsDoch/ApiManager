package dev.lars.apimanager.apis.homeAPI;

import dev.lars.apimanager.ApiManager;
import dev.lars.apimanager.database.DatabaseRepository;
import dev.lars.apimanager.database.IDatabaseManager;
import dev.lars.apimanager.utils.FormatLocation;
import dev.lars.apimanager.utils.ValidateParameter;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HomeAPIImpl implements IHomeAPI {
    private static final String TABLE = "player_homes";

    private DatabaseRepository repo() {
        return new DatabaseRepository();
    }

    private IDatabaseManager db() {
        return ApiManager.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db().update("""
            CREATE TABLE IF NOT EXISTS player_homes (
                id INT AUTO_INCREMENT PRIMARY KEY,
                uuid CHAR(36) NOT NULL,
                name VARCHAR(255) NOT NULL,
                location VARCHAR(255) NOT NULL,
                is_public BOOLEAN DEFAULT FALSE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (uuid) REFERENCES players(uuid) ON DELETE CASCADE,
                UNIQUE KEY unique_home (uuid, name)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);
    }

    @Override
    public Instant getCreatedAt(int homeId) {
        return repo().getInstant(TABLE, "created_at", "id = ?", homeId);
    }

    @Override
    public CompletableFuture<Instant> getCreatedAtAsync(int homeId) {
        return repo().getInstantAsync(TABLE, "created_at", "id = ?", homeId);
    }

    @Override
    public Instant getUpdatedAt(int homeId) {
        return repo().getInstant(TABLE, "updated_at", "id = ?", homeId);
    }

    @Override
    public CompletableFuture<Instant> getUpdatedAtAsync(int homeId) {
        return repo().getInstantAsync(TABLE, "updated_at", "id = ?", homeId);
    }

    @Override
    public void createHome(OfflinePlayer player, String name, Location location, boolean isPublic) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateName(name);
        ValidateParameter.validateLocation(location);
        db().update("""
            INSERT INTO player_homes (uuid, name, location, is_public)
            VALUES (?, ?, ?, ?)
        """, player.getUniqueId().toString(), name, FormatLocation.serializeLocation(location), isPublic);
    }

    @Override
    public CompletableFuture<Void> createHomeAsync(OfflinePlayer player, String name, Location location, boolean isPublic) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateName(name);
        ValidateParameter.validateLocation(location);
        return db().updateAsync("""
            INSERT INTO player_homes (uuid, name, location, is_public)
            VALUES (?, ?, ?, ?)
        """, player.getUniqueId().toString(), name, FormatLocation.serializeLocation(location), isPublic);
    }

    @Override
    public void deleteHome(int homeId) {
        repo().delete(TABLE, "id = ?", homeId);
    }

    @Override
    public CompletableFuture<Void> deleteHomeAsync(int homeId) {
        return repo().deleteAsync(TABLE, "id = ?", homeId);
    }

    @Override
    public void renameHome(int homeId, String newName) {
        ValidateParameter.validateName(newName);
        repo().updateColumn(TABLE, "name", newName, "id = ?", homeId);
    }

    @Override
    public CompletableFuture<Void> renameHomeAsync(int homeId, String newName) {
        ValidateParameter.validateName(newName);
        return repo().updateColumnAsync(TABLE, "name", newName, "id = ?", homeId);
    }

    @Override
    public void setHomePublic(int homeId, boolean isPublic) {
        repo().updateColumn(TABLE, "is_public", isPublic, "id = ?", homeId);
    }

    @Override
    public CompletableFuture<Void> setHomePublicAsync(int homeId, boolean isPublic) {
        return repo().updateColumnAsync(TABLE, "is_public", isPublic, "id = ?", homeId);
    }

    @Override
    public void updateHomeLocation(int homeId, Location location) {
        ValidateParameter.validateLocation(location);
        repo().updateColumn(TABLE, "location", FormatLocation.serializeLocation(location), "id = ?", homeId);
    }

    @Override
    public CompletableFuture<Void> updateHomeLocationAsync(int homeId, Location location) {
        ValidateParameter.validateLocation(location);
        return repo().updateColumnAsync(TABLE, "location", FormatLocation.serializeLocation(location), "id = ?", homeId);
    }

    @Override
    public List<String> getHomes(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getStringList(TABLE, "name", "uuid = ? OR is_public = TRUE", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<List<String>> getHomesAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getStringListAsync(TABLE, "name", "uuid = ? OR is_public = TRUE", player.getUniqueId().toString());
    }

    @Override
    public List<String> getOwnHomes(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getStringList(TABLE, "name", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<List<String>> getOwnHomesAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getStringListAsync(TABLE, "name", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public List<String> getAllHomes() {
        return repo().getStringList(TABLE, "name", null);
    }

    @Override
    public CompletableFuture<List<String>> getAllHomesAsync() {
        return repo().getStringListAsync(TABLE, "name", null);
    }

    @Override
    public Location getHomeLocation(int homeId) {
        String locData = repo().getString(TABLE, "location", "id = ?", homeId);
        return FormatLocation.deserializeLocation(locData);
    }

    @Override
    public CompletableFuture<Location> getHomeLocationAsync(int homeId) {
        return repo().getStringAsync(TABLE, "location", "id = ?", homeId)
            .thenApply(FormatLocation::deserializeLocation);
    }

    @Override
    public boolean doesHomeExist(String name) {
        ValidateParameter.validateName(name);
        return repo().exists(TABLE, "name = ?", name);
    }

    @Override
    public CompletableFuture<Boolean> doesHomeExistAsync(String name) {
        ValidateParameter.validateName(name);
        return repo().existsAsync(TABLE, "name = ?", name);
    }

    @Override
    public boolean doesOwnHomeExist(OfflinePlayer player, String name) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateName(name);
        return repo().exists(TABLE, "uuid = ? AND name = ?", player.getUniqueId().toString(), name);
    }

    @Override
    public CompletableFuture<Boolean> doesOwnHomeExistAsync(OfflinePlayer player, String name) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateName(name);
        return repo().existsAsync(TABLE, "uuid = ? AND name = ?", player.getUniqueId().toString(), name);
    }

    @Override
    public int getHomeId(OfflinePlayer player, String name) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateName(name);
        Integer id = repo().getInteger(TABLE, "id", "(uuid = ? OR is_public = TRUE) AND name = ?",
            player.getUniqueId().toString(), name);
        return id != null ? id : -1;
    }

    @Override
    public CompletableFuture<Integer> getHomeIdAsync(OfflinePlayer player, String name) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateName(name);
        return repo().getIntegerAsync(TABLE, "id", "(uuid = ? OR is_public = TRUE) AND name = ?",
            player.getUniqueId().toString(), name)
            .thenApply(id -> id != null ? id : -1);
    }
}