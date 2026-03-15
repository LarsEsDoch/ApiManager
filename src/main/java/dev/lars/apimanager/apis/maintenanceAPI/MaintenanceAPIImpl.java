package dev.lars.apimanager.apis.maintenanceAPI;

import dev.lars.apimanager.ApiManager;
import dev.lars.apimanager.database.DatabaseRepository;
import dev.lars.apimanager.database.IDatabaseManager;
import dev.lars.apimanager.utils.ApiManagerValidateParameter;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class MaintenanceAPIImpl implements IMaintenanceAPI {
    private static final String TABLE = "server_maintenance";

    private String serverId() {
        return ApiManager.getServerId();
    }

    private DatabaseRepository repo() {
        return new DatabaseRepository();
    }

    private IDatabaseManager db() {
        return ApiManager.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db().update(String.format("""
            CREATE TABLE IF NOT EXISTS %s (
                server_id VARCHAR(64) NOT NULL PRIMARY KEY,
                is_maintenance_enabled BOOLEAN NOT NULL DEFAULT FALSE,
                maintenance_reason VARCHAR(255) NOT NULL DEFAULT '',
                maintenance_start TIMESTAMP DEFAULT NULL,
                maintenance_estimated_end TIMESTAMP DEFAULT NULL,
                maintenance_deadline TIMESTAMP DEFAULT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """, TABLE));

        repo().insertIgnore(TABLE, new String[]{"server_id"}, serverId());
    }

    @Override
    public Instant getCreatedAt() {
        return repo().getInstant(TABLE, "created_at", "server_id = ?", serverId());
    }

    @Override
    public CompletableFuture<Instant> getCreatedAtAsync() {
        return repo().getInstantAsync(TABLE, "created_at", "server_id = ?", serverId());
    }

    @Override
    public Instant getUpdatedAt() {
        return repo().getInstant(TABLE, "updated_at", "server_id = ?", serverId());
    }

    @Override
    public CompletableFuture<Instant> getUpdatedAtAsync() {
        return repo().getInstantAsync(TABLE, "updated_at", "server_id = ?", serverId());
    }

    @Override
    public void setMaintenanceEnabled(boolean enabled) {
        repo().updateColumn(TABLE,
                "is_maintenance_enabled",
                enabled,
                where());
    }

    @Override
    public CompletableFuture<Void> setMaintenanceEnabledAsync(boolean enabled) {
        return repo().updateColumnAsync(TABLE,
                "is_maintenance_enabled",
                enabled,
                where());
    }

    @Override
    public void enableMaintenance(String reason, Instant start, Instant estimatedEnd, Instant deadline) {
        ApiManagerValidateParameter.validateReason(reason);
        repo().updateColumns(TABLE,
                new String[]{"is_maintenance_enabled", "maintenance_reason", "maintenance_start", "maintenance_estimated_end", "maintenance_deadline"},
                new Object[]{true, reason, start, estimatedEnd, deadline},
                where());
    }

    @Override
    public CompletableFuture<Void> enableMaintenanceAsync(String reason, Instant start, Instant estimatedEnd, Instant deadline) {
        ApiManagerValidateParameter.validateReason(reason);
        return repo().updateColumnsAsync(TABLE,
                new String[]{"is_maintenance_enabled", "maintenance_reason", "maintenance_start", "maintenance_estimated_end", "maintenance_deadline"},
                new Object[]{true, reason, start, estimatedEnd, deadline},
                where());
    }

    @Override
    public void disableMaintenance() {
        repo().updateColumns(TABLE,
                new String[]{"is_maintenance_enabled", "maintenance_reason", "maintenance_start", "maintenance_estimated_end", "maintenance_deadline"},
                new Object[]{false, "", null, null, null},
                where());
    }

    @Override
    public CompletableFuture<Void> disableMaintenanceAsync() {
        return repo().updateColumnsAsync(TABLE,
                new String[]{"is_maintenance_enabled", "maintenance_reason", "maintenance_start", "maintenance_estimated_end", "maintenance_deadline"},
                new Object[]{false, "", null, null, null},
                where());
    }

    @Override
    public boolean isMaintenanceEnabled() {
        return repo().getBoolean(TABLE, "is_maintenance_enabled", "server_id = ?", serverId());
    }

    @Override
    public CompletableFuture<Boolean> isMaintenanceEnabledAsync() {
        return repo().getBooleanAsync(TABLE, "is_maintenance_enabled", "server_id = ?", serverId());
    }

    @Override
    public void setMaintenanceReason(String reason) {
        ApiManagerValidateParameter.validateReason(reason);
        repo().updateColumn(TABLE, "maintenance_reason", reason, "server_id = ?", serverId());
    }

    @Override
    public CompletableFuture<Void> setMaintenanceReasonAsync(String reason) {
        ApiManagerValidateParameter.validateReason(reason);
        return repo().updateColumnAsync(TABLE, "maintenance_reason", reason, "server_id = ?", serverId());
    }

    @Override
    public String getMaintenanceReason() {
        return repo().getString(TABLE, "maintenance_reason", "server_id = ?", serverId());
    }

    @Override
    public CompletableFuture<String> getMaintenanceReasonAsync() {
        return repo().getStringAsync(TABLE, "maintenance_reason", "server_id = ?", serverId());
    }

    @Override
    public void setMaintenanceStart(Instant start) {
        ApiManagerValidateParameter.validateInstant(start);
        repo().updateColumn(TABLE, "maintenance_start", start, "server_id = ?", serverId());
    }

    @Override
    public CompletableFuture<Void> setMaintenanceStartAsync(Instant start) {
        ApiManagerValidateParameter.validateInstant(start);
        return repo().updateColumnAsync(TABLE, "maintenance_start", start, "server_id = ?", serverId());
    }

    @Override
    public Instant getMaintenanceStart() {
        return repo().getInstant(TABLE, "maintenance_start", "server_id = ?", serverId());
    }

    @Override
    public CompletableFuture<Instant> getMaintenanceStartAsync() {
        return repo().getInstantAsync(TABLE, "maintenance_start", "server_id = ?", serverId());
    }

    @Override
    public void setMaintenanceEstimatedEnd(Instant estimatedEnd) {
        ApiManagerValidateParameter.validateInstant(estimatedEnd);
        repo().updateColumn(TABLE, "maintenance_estimated_end", estimatedEnd, "server_id = ?", serverId());
    }

    @Override
    public CompletableFuture<Void> setMaintenanceEstimatedEndAsync(Instant estimatedEnd) {
        ApiManagerValidateParameter.validateInstant(estimatedEnd);
        return repo().updateColumnAsync(TABLE, "maintenance_estimated_end", estimatedEnd, "server_id = ?", serverId());
    }

    @Override
    public Instant getMaintenanceEstimatedEnd() {
        return repo().getInstant(TABLE, "maintenance_estimated_end", "server_id = ?", serverId());
    }

    @Override
    public CompletableFuture<Instant> getMaintenanceEstimatedEndAsync() {
        return repo().getInstantAsync(TABLE, "maintenance_estimated_end", "server_id = ?", serverId());
    }

    @Override
    public void setMaintenanceDeadline(Instant deadline) {
        ApiManagerValidateParameter.validateInstant(deadline);
        repo().updateColumn(TABLE, "maintenance_deadline", deadline, "server_id = ?", serverId());
    }

    @Override
    public CompletableFuture<Void> setMaintenanceDeadlineAsync(Instant deadline) {
        ApiManagerValidateParameter.validateInstant(deadline);
        return repo().updateColumnAsync(TABLE, "maintenance_deadline", deadline, "server_id = ?", serverId());
    }

    @Override
    public Instant getMaintenanceDeadline() {
        return repo().getInstant(TABLE, "maintenance_deadline", "server_id = ?", serverId());
    }

    @Override
    public CompletableFuture<Instant> getMaintenanceDeadlineAsync() {
        return repo().getInstantAsync(TABLE, "maintenance_deadline", "server_id = ?", serverId());
    }
}