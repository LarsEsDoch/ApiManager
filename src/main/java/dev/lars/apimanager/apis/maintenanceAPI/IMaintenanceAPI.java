package dev.lars.apimanager.apis.maintenanceAPI;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public interface IMaintenanceAPI {
    Instant getCreatedAt();

    CompletableFuture<Instant> getCreatedAtAsync();

    Instant getUpdatedAt();

    CompletableFuture<Instant> getUpdatedAtAsync();

    void setMaintenanceEnabled(boolean enabled);

    CompletableFuture<Void> setMaintenanceEnabledAsync(boolean enabled);

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
}