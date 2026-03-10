
package dev.lars.apimanager.database;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.command.CommandSender;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public interface IDatabaseManager {
    void update(String sql, Object... params);

    CompletableFuture<Void> updateAsync(String sql, Object... params);

    <T> T query(DatabaseManager.SQLFunction<java.sql.Connection, T> function);

    <T> CompletableFuture<T> queryAsync(DatabaseManager.SQLFunction<java.sql.Connection, T> function);

    void logSqlQuery(String sql, Object... params);

    void enableSqlLogging(CommandSender sender, long durationMs);

    void disableSqlLogging(CommandSender sender);

    boolean isSqlLoggingEnabled(CommandSender sender);

    long getSqlLoggingTimeRemaining(CommandSender sender);

    boolean hasAnyLoggingSubscribers();

    HikariDataSource getDataSource();

    Connection getConnection() throws SQLException;

    double[] getSmoothedQps();

    void close();
}