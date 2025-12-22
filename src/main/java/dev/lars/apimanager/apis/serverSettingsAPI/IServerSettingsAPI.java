package dev.lars.apimanager.apis.serverSettingsAPI;

import org.bukkit.Location;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IServerSettingsAPI {
    Instant getCreatedAt();

    CompletableFuture<Instant> getCreatedAtAsync();

    Instant getUpdatedAt();

    CompletableFuture<Instant> getUpdatedAtAsync();

    void setServerOnline(boolean online);

    CompletableFuture<Void> setServerOnlineAsync(boolean online);

    boolean isServerOnline();

    CompletableFuture<Boolean> isServerOnlineAsync();

    void setRealTimeEnabled(boolean enabled);

    CompletableFuture<Void> setRealTimeEnabledAsync(boolean enabled);

    boolean isRealTimeEnabled();

    CompletableFuture<Boolean> isRealTimeEnabledAsync();

    void setRealWeatherEnabled(boolean enabled);

    CompletableFuture<Void> setRealWeatherEnabledAsync(boolean enabled);

    boolean isRealWeatherEnabled();

    CompletableFuture<Boolean> isRealWeatherEnabledAsync();

    void enableMaintenance(String reason, Instant start, Instant estimatedEnd, Instant deadline);

    CompletableFuture<Void> enableMaintenanceAsync(String reason, Instant start, Instant estimatedEnd, Instant deadline);

    void disableMaintenance();

    CompletableFuture<Void> disableMaintenanceAsync();

    boolean isMaintenanceEnabled();

    CompletableFuture<Boolean> isMaintenanceEnabledAsync();

    void setMaintenanceReason(String reason);

    CompletableFuture<Void> setMaintenanceReasonAsync(String reason);

    String getMaintenanceReason();

    CompletableFuture<String> getMaintenanceReasonAsync();

    void setMaintenanceStart(Instant start);

    CompletableFuture<Void> setMaintenanceStartAsync(Instant start);

    Instant getMaintenanceStart();

    CompletableFuture<Instant> getMaintenanceStartAsync();

    void setMaintenanceEstimatedEnd(Instant estimatedEnd);

    CompletableFuture<Void> setMaintenanceEstimatedEndAsync(Instant estimatedEnd);

    Instant getMaintenanceEstimatedEnd();

    CompletableFuture<Instant> getMaintenanceEstimatedEndAsync();

    void setMaintenanceDeadline(Instant deadline);

    CompletableFuture<Void> setMaintenanceDeadlineAsync(Instant deadline);

    Instant getMaintenanceDeadline();

    CompletableFuture<Instant> getMaintenanceDeadlineAsync();

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