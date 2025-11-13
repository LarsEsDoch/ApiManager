package dev.lars.apimanager.database;

import com.zaxxer.hikari.HikariDataSource;
import dev.lars.apimanager.utils.ApiManagerStatements;
import net.kyori.adventure.text.format.NamedTextColor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

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

    public void logSqlQuery(String sql, Object... params) {
        ApiManagerStatements.logToConsole("[SafeDatabaseManager] logSqlQuery(String sql, Object... params) called - no real database connection present.", NamedTextColor.GOLD);
    }

    public void setSqlLogging(boolean enabled) {
        ApiManagerStatements.logToConsole("[SafeDatabaseManager] setSqlLogging(boolean enabled) called - no real database connection present.", NamedTextColor.GOLD);
    }

    public boolean isSqlLoggingEnabled() {
        ApiManagerStatements.logToConsole("[SafeDatabaseManager] isSqlLoggingEnabled() called - no real database connection present.", NamedTextColor.GOLD);
        return false;
    }

    public Connection getConnection() throws SQLException {
        ApiManagerStatements.logToConsole("[SafeDatabaseManager] getConnection() called - no real database connection present.", NamedTextColor.RED);
        throw new SQLException("No database connection available (SafeDatabaseManager).");
    }

    public HikariDataSource getDataSource() {
        ApiManagerStatements.logToConsole("[SafeDatabaseManager] getDataSource() called - no real database connection present.", NamedTextColor.RED);
        return null;
    }

    public double[] getSmoothedQps() {
        ApiManagerStatements.logToConsole("[SafeDatabaseManager] getSmoothedQps() called - no real database connection present.", NamedTextColor.RED);
        return new double[] {0, 0};
    }

    public void close() {
        ApiManagerStatements.logToConsole("[SafeDatabaseManager] close() called - no real database connection present.", NamedTextColor.GOLD);
    }

    private void logSkip(String action, String detail) {
        ApiManagerStatements.logToConsole("[SafeDatabaseManager] Action [" + action + "] cannot be executed because the database isn't connected.", NamedTextColor.GOLD);
    }
}