package de.lars.apimanager.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class SafeDatabaseManager implements IDatabaseManager{
    public void update(String sql, Object... params) {
        logSkip("update", sql);
    }

    public CompletableFuture<Void> updateAsync(String sql, Object... params) {
        logSkip("updateAsync", sql);
        return CompletableFuture.completedFuture(null);
    }

    public <T> T query(DatabaseManager.SQLFunction<java.sql.Connection, T> function) {
        logSkip("query", "function call");
        return null;
    }

    public <T> CompletableFuture<T> queryAsync(DatabaseManager.SQLFunction<java.sql.Connection, T> function) {
        logSkip("queryAsync", "function call");
        return CompletableFuture.completedFuture(null);
    }

    public Connection getConnection() throws SQLException {
        de.lars.apimanager.ApiManager.getInstance().getLogger().log(
                Level.INFO, "[SafeDatabaseManager] getConnection() called - no real database connection present."
        );
        throw new SQLException("No database connection available (SafeDatabaseManager).");
    }

    public void close() {
        de.lars.apimanager.ApiManager.getInstance().getLogger().log(
                Level.INFO, "[SafeDatabaseManager] close() called - no real database connection present."
        );
    }

    private void logSkip(String action, String detail) {
        de.lars.apimanager.ApiManager.getInstance().getLogger().log(
                Level.WARNING,
                "[SafeDatabaseManager] Action [" + action + "] cannot be executed because the database isn't connected."
        );
    }
}