package dev.lars.apimanager.apis.maintenanceAPI;

import dev.lars.apimanager.ApiManager;
import dev.lars.apimanager.database.DatabaseRepository;
import dev.lars.apimanager.database.IDatabaseManager;
import dev.lars.apimanager.utils.ApiManagerValidateParameter;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class MaintenanceAPIImpl implements IMaintenanceAPI {
    private static final String TABLE = "server_maintenance";
    private static final String WHERE_ID = "id = 1";

    private DatabaseRepository repo() {
        return new DatabaseRepository();
    }

    private IDatabaseManager db() {
        return ApiManager.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db().update("""
            CREATE TABLE IF NOT EXISTS server_maintenance (
                id INT PRIMARY KEY CHECK (id = 1),
                is_maintenance_enabled BOOLEAN NOT NULL DEFAULT FALSE,
                maintenance_reason VARCHAR(255) NOT NULL DEFAULT '',
                maintenance_start TIMESTAMP DEFAULT NULL,
                maintenance_estimated_end TIMESTAMP DEFAULT NULL,
                maintenance_deadline TIMESTAMP DEFAULT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);

        if (repo().count(TABLE, WHERE_ID) < 1) {
            db().update("""
                INSERT INTO server_maintenance
                (id, is_maintenance_enabled, maintenance_reason, maintenance_start, maintenance_estimated_end, maintenance_deadline)
                VALUES (1, ?, ?, ?, ?, ?)
            """, false, "", null, null, null);
        }
    }

    @Override
    public Instant getCreatedAt() {
        return repo().getInstant(TABLE, "created_at", WHERE_ID);
    }

    @Override
    public CompletableFuture<Instant> getCreatedAtAsync() {
        return repo().getInstantAsync(TABLE, "created_at", WHERE_ID);
    }

    @Override
    public Instant getUpdatedAt() {
        return repo().getInstant(TABLE, "updated_at", WHERE_ID);
    }

    @Override
    public CompletableFuture<Instant> getUpdatedAtAsync() {
        return repo().getInstantAsync(TABLE, "updated_at", WHERE_ID);
    }

    @Override
    public void setMaintenanceEnabled(boolean enabled) {
        repo().updateColumn(TABLE,
                "is_maintenance_enabled",
                enabled,
                WHERE_ID);
    }

    @Override
    public CompletableFuture<Void> setMaintenanceEnabledAsync(boolean enabled) {
        return repo().updateColumnAsync(TABLE,
                "is_maintenance_enabled",
                enabled,
                WHERE_ID);
    }

    @Override
    public void enableMaintenance(String reason, Instant start, Instant estimatedEnd, Instant deadline) {
        ApiManagerValidateParameter.validateReason(reason);
        repo().updateColumns(TABLE,
                new String[]{"is_maintenance_enabled", "maintenance_reason", "maintenance_start", "maintenance_estimated_end", "maintenance_deadline"},
                new Object[]{true, reason, start, estimatedEnd, deadline},
                WHERE_ID);
    }

    @Override
    public CompletableFuture<Void> enableMaintenanceAsync(String reason, Instant start, Instant estimatedEnd, Instant deadline) {
        ApiManagerValidateParameter.validateReason(reason);
        return repo().updateColumnsAsync(TABLE,
                new String[]{"is_maintenance_enabled", "maintenance_reason", "maintenance_start", "maintenance_estimated_end", "maintenance_deadline"},
                new Object[]{true, reason, start, estimatedEnd, deadline},
                WHERE_ID);
    }

    @Override
    public void disableMaintenance() {
        repo().updateColumns(TABLE,
                new String[]{"is_maintenance_enabled", "maintenance_reason", "maintenance_start", "maintenance_estimated_end", "maintenance_deadline"},
                new Object[]{false, "", null, null, null},
                WHERE_ID);
    }

    @Override
    public CompletableFuture<Void> disableMaintenanceAsync() {
        return repo().updateColumnsAsync(TABLE,
                new String[]{"is_maintenance_enabled", "maintenance_reason", "maintenance_start", "maintenance_estimated_end", "maintenance_deadline"},
                new Object[]{false, "", null, null, null},
                WHERE_ID);
    }

    @Override
    public boolean isMaintenanceEnabled() {
        return repo().getBoolean(TABLE, "is_maintenance_enabled", WHERE_ID);
    }

    @Override
    public CompletableFuture<Boolean> isMaintenanceEnabledAsync() {
        return repo().getBooleanAsync(TABLE, "is_maintenance_enabled", WHERE_ID);
    }

    @Override
    public void setMaintenanceReason(String reason) {
        ApiManagerValidateParameter.validateReason(reason);
        repo().updateColumn(TABLE, "maintenance_reason", reason, WHERE_ID);
    }

    @Override
    public CompletableFuture<Void> setMaintenanceReasonAsync(String reason) {
        ApiManagerValidateParameter.validateReason(reason);
        return repo().updateColumnAsync(TABLE, "maintenance_reason", reason, WHERE_ID);
    }

    @Override
    public String getMaintenanceReason() {
        return repo().getString(TABLE, "maintenance_reason", WHERE_ID);
    }

    @Override
    public CompletableFuture<String> getMaintenanceReasonAsync() {
        return repo().getStringAsync(TABLE, "maintenance_reason", WHERE_ID);
    }

    @Override
    public void setMaintenanceStart(Instant start) {
        ApiManagerValidateParameter.validateInstant(start);
        repo().updateColumn(TABLE, "maintenance_start", start, WHERE_ID);
    }

    @Override
    public CompletableFuture<Void> setMaintenanceStartAsync(Instant start) {
        ApiManagerValidateParameter.validateInstant(start);
        return repo().updateColumnAsync(TABLE, "maintenance_start", start, WHERE_ID);
    }

    @Override
    public Instant getMaintenanceStart() {
        return repo().getInstant(TABLE, "maintenance_start", WHERE_ID);
    }

    @Override
    public CompletableFuture<Instant> getMaintenanceStartAsync() {
        return repo().getInstantAsync(TABLE, "maintenance_start", WHERE_ID);
    }

    @Override
    public void setMaintenanceEstimatedEnd(Instant estimatedEnd) {
        ApiManagerValidateParameter.validateInstant(estimatedEnd);
        repo().updateColumn(TABLE, "maintenance_estimated_end", estimatedEnd, WHERE_ID);
    }

    @Override
    public CompletableFuture<Void> setMaintenanceEstimatedEndAsync(Instant estimatedEnd) {
        ApiManagerValidateParameter.validateInstant(estimatedEnd);
        return repo().updateColumnAsync(TABLE, "maintenance_estimated_end", estimatedEnd, WHERE_ID);
    }

    @Override
    public Instant getMaintenanceEstimatedEnd() {
        return repo().getInstant(TABLE, "maintenance_estimated_end", WHERE_ID);
    }

    @Override
    public CompletableFuture<Instant> getMaintenanceEstimatedEndAsync() {
        return repo().getInstantAsync(TABLE, "maintenance_estimated_end", WHERE_ID);
    }

    @Override
    public void setMaintenanceDeadline(Instant deadline) {
        ApiManagerValidateParameter.validateInstant(deadline);
        repo().updateColumn(TABLE, "maintenance_deadline", deadline, WHERE_ID);
    }

    @Override
    public CompletableFuture<Void> setMaintenanceDeadlineAsync(Instant deadline) {
        ApiManagerValidateParameter.validateInstant(deadline);
        return repo().updateColumnAsync(TABLE, "maintenance_deadline", deadline, WHERE_ID);
    }

    @Override
    public Instant getMaintenanceDeadline() {
        return repo().getInstant(TABLE, "maintenance_deadline", WHERE_ID);
    }

    @Override
    public CompletableFuture<Instant> getMaintenanceDeadlineAsync() {
        return repo().getInstantAsync(TABLE, "maintenance_deadline", WHERE_ID);
    }
}