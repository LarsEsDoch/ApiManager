package dev.lars.apimanager.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.lars.apimanager.utils.ApiManagerStatements;
import net.kyori.adventure.text.format.NamedTextColor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class DatabaseManager implements IDatabaseManager {
    private volatile HikariDataSource dataSource;
    private final ExecutorService asyncExecutor;
    private final CompletableFuture<Void> ready = new CompletableFuture<>();
    private final AtomicBoolean sqlLoggingEnabled = new AtomicBoolean(false);

    public DatabaseManager(String host, int port, String database, String username, String password) {
        this.asyncExecutor = Executors.newFixedThreadPool(
            8,
            r -> {
                Thread thread = new Thread(r);
                thread.setName("ApiManager-Async-DB-Thread");
                thread.setDaemon(true);
                return thread;
            }
        );

        CompletableFuture.runAsync(() -> {
            while (true) {
                try {
                    HikariConfig config = new HikariConfig();
                    config.setDriverClassName("org.mariadb.jdbc.Driver");
                    config.setJdbcUrl(String.format(
                            "jdbc:mariadb://%s:%d/%s?useUnicode=true&characterEncoding=UTF-8&useSSL=false&autoReconnect=true&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                            host, port, database
                    ));
                    config.setUsername(username);
                    config.setPassword(password);

                    config.setMaximumPoolSize(10);
                    config.setMinimumIdle(3);
                    config.setConnectionTimeout(10000);
                    config.setValidationTimeout(5000);
                    config.setIdleTimeout(300000);
                    config.setMaxLifetime(900000);
                    config.setLeakDetectionThreshold(60000);

                    config.setConnectionTestQuery("SELECT 1");

                    config.addDataSourceProperty("autoReconnect", "true");
                    config.addDataSourceProperty("socketTimeout", "30000");
                    config.addDataSourceProperty("connectTimeout", "5000");
                    config.addDataSourceProperty("tcpKeepAlive", "true");
                    config.addDataSourceProperty("cachePrepStmts", "true");
                    config.addDataSourceProperty("prepStmtCacheSize", "250");
                    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

                    HikariDataSource ds = new HikariDataSource(config);

                    try (Connection conn = ds.getConnection()) {
                        ApiManagerStatements.logToConsole("Connected to MariaDB via HikariCP", NamedTextColor.GREEN);
                    }

                    this.dataSource = ds;
                    ready.complete(null);
                    break;
                } catch (Exception e) {
                    ApiManagerStatements.logToConsole("Database connection failed, retrying in 5s... " + e.getMessage(), NamedTextColor.GOLD);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ignored) {}
                }
            }
        });
    }

    public CompletableFuture<Void> readyFuture() {
        return ready;
    }

    public boolean isReady() {
        return ready.isDone() && dataSource != null;
    }

    public void setSqlLogging(boolean enabled) {
        sqlLoggingEnabled.set(enabled);
        String status = enabled ? "enabled" : "disabled";
        ApiManagerStatements.logToConsole("SQL query logging " + status,
            enabled ? NamedTextColor.GREEN : NamedTextColor.GRAY);
    }

    public boolean isSqlLoggingEnabled() {
        return sqlLoggingEnabled.get();
    }

    public void logSqlQuery(String sql, Object... params) {
        if (sqlLoggingEnabled.get()) {
            StringBuilder log = new StringBuilder();
            log.append("[SQL Query] ").append(sql.trim());

            if (params != null && params.length > 0) {
                log.append(" | Parameters: [");
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param == null) {
                        log.append("null");
                    } else {
                        String paramStr = param.toString();
                        if (paramStr.length() > 100) {
                            paramStr = paramStr.substring(0, 97) + "...";
                        }
                        log.append(paramStr);
                    }
                    if (i < params.length - 1) {
                        log.append(", ");
                    }
                }
                log.append("]");
            }

            ApiManagerStatements.logToConsole(log.toString(), NamedTextColor.AQUA);
        }
    }

    private void logSqlUpdate(String sql, Object... params) {
        if (sqlLoggingEnabled.get()) {
            StringBuilder log = new StringBuilder();
            log.append("[SQL Update] ").append(sql.trim());

            if (params != null && params.length > 0) {
                log.append(" | Parameters: [");
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param == null) {
                        log.append("null");
                    } else {
                        String paramStr = param.toString();
                        if (paramStr.length() > 100) {
                            paramStr = paramStr.substring(0, 97) + "...";
                        }
                        log.append(paramStr);
                    }
                    if (i < params.length - 1) {
                        log.append(", ");
                    }
                }
                log.append("]");
            }

            ApiManagerStatements.logToConsole(log.toString(), NamedTextColor.LIGHT_PURPLE);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource not initialized yet. Connection pool is not ready.");
        }
        return dataSource.getConnection();
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }

    @Override
    public void close() {
        if (asyncExecutor != null && !asyncExecutor.isShutdown()) {
            asyncExecutor.shutdown();
            try {
                if (!asyncExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                    asyncExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                asyncExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            ApiManagerStatements.logToConsole("HikariCP pool closed.", NamedTextColor.GRAY);
        }
    }

    public void update(String sql, Object... params) {
        logSqlUpdate(sql, params);

        try (Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            setParams(ps, params);
            ps.executeUpdate();
        } catch (SQLException e) {
            ApiManagerStatements.logToConsole("SQL update failed: " + sql + " " + e.getMessage(), NamedTextColor.RED);
        }
    }

    public CompletableFuture<Void> updateAsync(String sql, Object... params) {
        return CompletableFuture.runAsync(() -> update(sql, params), asyncExecutor);
    }

    public <T> T query(SQLFunction<Connection, T> function) {
        try (Connection conn = getConnection()) {
            return function.apply(conn);
        } catch (SQLException e) {
            ApiManagerStatements.logToConsole("SQL query failed: " + e.getMessage(), NamedTextColor.RED);
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