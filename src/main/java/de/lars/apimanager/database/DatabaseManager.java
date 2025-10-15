package de.lars.apiManager.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.lars.apiManager.Main;

import java.sql.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.logging.Level;

public class DatabaseManager {

    private final HikariDataSource dataSource;
    private final Executor asyncExecutor;

    public DatabaseManager(String host, int port, String database, String username, String password) {
        HikariConfig config = new HikariConfig();

        config.setDriverClassName("org.mariadb.jdbc.Driver");
        config.setJdbcUrl(String.format(
                "jdbc:mariadb://%s:%d/%s?useUnicode=true&characterEncoding=UTF-8&useSSL=false&autoReconnect=true&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                host, port, database
        ));
        config.setUsername(username);
        config.setPassword(password);

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(10000);
        config.setIdleTimeout(120000);
        config.setMaxLifetime(1800000);
        config.setLeakDetectionThreshold(10000);

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");

        this.dataSource = new HikariDataSource(config);
        this.asyncExecutor = CompletableFuture.delayedExecutor(0, java.util.concurrent.TimeUnit.MILLISECONDS);

        Main.getInstance().getLogger().info("Connected to MariaDB via HikariCP");
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            Main.getInstance().getLogger().info("HikariCP pool closed.");
        }
    }

    public void update(String sql, Object... params) {
        try (Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            setParams(ps, params);
            ps.executeUpdate();
        } catch (SQLException e) {
            Main.getInstance().getLogger().log(Level.SEVERE, "SQL update failed: " + sql, e);
        }
    }

    public CompletableFuture<Void> updateAsync(String sql, Object... params) {
        return CompletableFuture.runAsync(() -> update(sql, params), asyncExecutor);
    }

    public <T> T query(SQLFunction<Connection, T> function) {
        try (Connection conn = getConnection()) {
            return function.apply(conn);
        } catch (SQLException e) {
            Main.getInstance().getLogger().log(Level.SEVERE, "SQL query failed", e);
            return null;
        }
    }

    public <T> CompletableFuture<T> queryAsync(SQLFunction<Connection, T> function) {
        return CompletableFuture.supplyAsync(() -> query(function), asyncExecutor);
    }

    private void setParams(PreparedStatement ps, Object... params) throws SQLException {
        if (params == null) return;
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }

    @FunctionalInterface
    public interface SQLFunction<T, R> {
        R apply(T t) throws SQLException;
    }
}