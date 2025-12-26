package dev.lars.apimanager.apis.serverStateAPI;

import org.bukkit.Location;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IServerStateAPI {
    Instant getCreatedAt();

    CompletableFuture<Instant> getCreatedAtAsync();

    Instant getUpdatedAt();

    CompletableFuture<Instant> getUpdatedAtAsync();

    void setServerOnline(boolean online);

    CompletableFuture<Void> setServerOnlineAsync(boolean online);

    boolean isServerOnline();

    CompletableFuture<Boolean> isServerOnlineAsync();

    void setMaxPlayers(int maxPlayers);

    CompletableFuture<Void> setMaxPlayersAsync(int maxPlayers);

    Integer getMaxPlayers();

    CompletableFuture<Integer> getMaxPlayersAsync();

    void setServerName(String serverName);

    CompletableFuture<Void> setServerNameAsync(String serverName);

    String getServerName();

    CompletableFuture<String> getServerNameAsync();

    void setServerGradient(List<String> serverNameColors);

    CompletableFuture<Void> setServerGradientAsync(List<String> serverNameColors);

    List<String> getServerNameGradient();

    CompletableFuture<List<String>> getServerNameGradientAsync();

    void setServerVersion(String serverVersion);

    CompletableFuture<Void> setServerVersionAsync(String serverVersion);

    String getServerVersion();

    CompletableFuture<String> getServerVersionAsync();

    void setSpawnLocation(Location location);

    CompletableFuture<Void> setSpawnLocationAsync(Location location);

    Location getSpawnLocation();

    CompletableFuture<Location> getSpawnLocationAsync();
}
