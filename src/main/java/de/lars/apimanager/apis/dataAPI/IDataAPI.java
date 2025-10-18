package de.lars.apimanager.apis.dataAPI;

import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;

public interface IDataAPI {
    Timestamp getCreatedAt();

    CompletableFuture<Timestamp> getCreatedAtAsync();

    Timestamp getUpdatedAt();

    CompletableFuture<Timestamp> getUpdatedAtAsync();

    void setRealTimeEnabled(boolean enabled);

    CompletableFuture<Void> setRealTimeEnabledAsync(boolean enabled);

    boolean isRealTimeActivated();

    CompletableFuture<Boolean> isRealTimeActivatedAsync();

    void setRealWeatherEnabled(boolean enabled);

    CompletableFuture<Void> setRealWeatherEnabledAsync(boolean enabled);

    boolean isRealWeatherActivated();

    CompletableFuture<Boolean> isRealWeatherActivatedAsync();

    void activateMaintenance(String reason, Timestamp endTime);

    CompletableFuture<Void> activateMaintenanceAsync(String reason, Timestamp endTime);

    void deactivateMaintenance();

    CompletableFuture<Void> deactivateMaintenanceAsync();

    boolean isMaintenanceActive();

    CompletableFuture<Boolean> isMaintenanceActiveAsync();

    void setMaintenanceReason(String reason);

    CompletableFuture<Void> setMaintenanceReasonAsync(String reason);

    String getMaintenanceReason();

    CompletableFuture<String> getMaintenanceReasonAsync();

    void setMaintenanceEnd(Timestamp endTime);

    CompletableFuture<Void> setMaintenanceEndAsync(Timestamp endTime);

    Timestamp getMaintenanceEnd();

    CompletableFuture<Timestamp> getMaintenanceEndAsync();

    void setMaxPlayers(int maxPlayers);

    CompletableFuture<Void> setMaxPlayersAsync(int maxPlayers);

    int getMaxPlayers();

    CompletableFuture<Integer> getMaxPlayersAsync();

    void setServerName(String serverName);

    CompletableFuture<Void> setServerNameAsync(String serverName);

    String getServerName();

    CompletableFuture<String> getServerNameAsync();

    void setServerVersion(String serverVersion);

    CompletableFuture<Void> setServerVersionAsync(String serverVersion);

    String getServerVersion();

    CompletableFuture<String> getServerVersionAsync();
}