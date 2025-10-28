package de.lars.apimanager.apis.homeAPI;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IHomeAPI {
    Timestamp getCreatedAt(int homeId);

    CompletableFuture<Timestamp> getCreatedAtAsync(int homeId);

    Timestamp getUpdatedAt(int homeId);

    CompletableFuture<Timestamp> getUpdatedAtAsync(int homeId);

    void createHome(OfflinePlayer player, String name, Location location, boolean isPublic);

    CompletableFuture<Void> createHomeAsync(OfflinePlayer player, String name, Location location, boolean isPublic);

    void deleteHome(int homeId);

    CompletableFuture<Void> deleteHomeAsync(int homeId);

    void renameHome(int homeId, String newName);

    CompletableFuture<Void> renameHomeAsync(int homeId, String newName);

    void setHomePublic(int homeId, boolean isPublic);

    CompletableFuture<Void> setHomePublicAsync(int homeId, boolean isPublic);

    void updateHomeLocation(int homeId, Location location);

    CompletableFuture<Void> updateHomeLocationAsync(int homeId, Location location);

    List<String> getHomes(OfflinePlayer player);

    CompletableFuture<List<String>> getHomesAsync(OfflinePlayer player);

    List<String> getOwnHomes(OfflinePlayer player);

    CompletableFuture<List<String>> getOwnHomesAsync(OfflinePlayer player);

    List<String> getAllHomes();

    CompletableFuture<List<String>> getAllHomesAsync();

    Location getHomeLocation(int homeId);

    CompletableFuture<Location> getHomeLocationAsync(int homeId);

    boolean doesHomeExist(String name);

    CompletableFuture<Boolean> doesHomeExistAsync(String name);

    boolean doesOwnHomeExist(OfflinePlayer player, String name);

    CompletableFuture<Boolean> doesOwnHomeExistAsync(OfflinePlayer player, String name);

    int getHomeId(OfflinePlayer player, String name);

    CompletableFuture<Integer> getHomeIdAsync(OfflinePlayer player, String name);
}