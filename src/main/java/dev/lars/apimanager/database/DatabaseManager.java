package dev.lars.apimanager.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.lars.apimanager.ApiManager;
import dev.lars.apimanager.utils.ApiManagerStatements;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public final class DatabaseManager implements IDatabaseManager {
    private volatile HikariDataSource dataSource;
    private final ExecutorService asyncExecutor;
    private final CompletableFuture<Void> ready = new CompletableFuture<>();
    private final AtomicBoolean sqlLoggingEnabled = new AtomicBoolean(false);
    private volatile long sqlLoggingEnabledUntil = 0;
    private final AtomicLong queryCount = new AtomicLong(0);
    private final AtomicLong updateCount = new AtomicLong(0);
    private volatile double smoothedQpsQueries = 0.0;
    private volatile double smoothedQpsUpdates = 0.0;
    private final AtomicLong lastQueryTotal = new AtomicLong(0);
    private final AtomicLong lastUpdateTotal = new AtomicLong(0);
    private final AtomicLong lastCheckTime = new AtomicLong(System.currentTimeMillis());
    private BukkitTask qpsUpdateTask;
    private BukkitTask loggingCheckTask;

    public void incrementQueryCount() {
        queryCount.incrementAndGet();
    }

    public void incrementUpdateCount() {
        updateCount.incrementAndGet();
    }

    public DatabaseManager(String host, int port, String database, String username, String password) {
        this.asyncExecutor = Executors.newFixedThreadPool(32);

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

                    config.setMaximumPoolSize(30);
                    config.setMinimumIdle(5);
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

                    try (Connection ignored = ds.getConnection()) {
                        ApiManagerStatements.logToConsole("Connected to MariaDB via HikariCP", NamedTextColor.GREEN);
                    }

                    this.dataSource = ds;
                    ready.complete(null);

                    startQpsUpdateTask();
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

    private void startQpsUpdateTask() {
        qpsUpdateTask = Bukkit.getScheduler().runTaskTimer(ApiManager.getInstance(),
            this::updateQpsMetrics, 20L, 20L);

        loggingCheckTask = Bukkit.getScheduler().runTaskTimer(ApiManager.getInstance(),
            this::checkLoggingTimeout, 1L, 1L);
    }

    private void updateQpsMetrics() {
        long now = System.currentTimeMillis();
        long lastCheck = lastCheckTime.get();
        long elapsed = now - lastCheck;

        if (elapsed < 100) {
            return;
        }

        lastCheckTime.set(now);

        long currentQ = queryCount.get();
        long lastQ = lastQueryTotal.get();
        long deltaQ = currentQ - lastQ;
        lastQueryTotal.set(currentQ);

        long currentU = updateCount.get();
        long lastU = lastUpdateTotal.get();
        long deltaU = currentU - lastU;
        lastUpdateTotal.set(currentU);

        double instantQpsQ = (deltaQ * 1000.0) / elapsed;
        double instantQpsU = (deltaU * 1000.0) / elapsed;

        if (smoothedQpsQueries == 0.0 && smoothedQpsUpdates == 0.0) {
            smoothedQpsQueries = instantQpsQ;
            smoothedQpsUpdates = instantQpsU;
        } else {
            smoothedQpsQueries = smoothedQpsQueries * 0.7 + instantQpsQ * 0.3;
            smoothedQpsUpdates = smoothedQpsUpdates * 0.7 + instantQpsU * 0.3;
        }
    }

    private void checkLoggingTimeout() {
        if (sqlLoggingEnabled.get() && sqlLoggingEnabledUntil > 0) {
            if (System.currentTimeMillis() >= sqlLoggingEnabledUntil) {
                sqlLoggingEnabled.set(false);
                sqlLoggingEnabledUntil = 0;
                ApiManagerStatements.logToConsole("SQL query logging automatically disabled (timeout reached)",
                    NamedTextColor.GRAY);
            }
        }
    }

    public CompletableFuture<Void> readyFuture() {
        return ready;
    }

    public boolean isReady() {
        return ready.isDone() && dataSource != null;
    }

    public void setSqlLogging(boolean enabled) {
        setSqlLogging(enabled, 0);
    }

    public void setSqlLogging(boolean enabled, long durationMs) {
        if (enabled) {
            if (durationMs > 0) {
                sqlLoggingEnabledUntil = System.currentTimeMillis() + durationMs;
                sqlLoggingEnabled.set(true);
                long seconds = durationMs / 1000;
                ApiManagerStatements.logToConsole("SQL query logging enabled for " + seconds + " seconds",
                    NamedTextColor.GREEN);
            } else {
                sqlLoggingEnabledUntil = 0;
                sqlLoggingEnabled.set(true);
                ApiManagerStatements.logToConsole("SQL query logging enabled indefinitely",
                    NamedTextColor.GREEN);
            }
        } else {
            sqlLoggingEnabledUntil = 0;
            sqlLoggingEnabled.set(false);
            ApiManagerStatements.logToConsole("SQL query logging disabled",
                NamedTextColor.GRAY);
        }
    }

    public boolean isSqlLoggingEnabled() {
        return sqlLoggingEnabled.get();
    }

    public long getSqlLoggingTimeRemaining() {
        if (sqlLoggingEnabledUntil > 0) {
            long remaining = sqlLoggingEnabledUntil - System.currentTimeMillis();
            return Math.max(0, remaining);
        }
        return 0;
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

    public double[] getSmoothedQps() {
        return new double[] { smoothedQpsQueries, smoothedQpsUpdates };
    }

    @Override
    public void close() {
        if (qpsUpdateTask != null && !qpsUpdateTask.isCancelled()) {
            qpsUpdateTask.cancel();
            ApiManagerStatements.logToConsole("QPS update task cancelled.", NamedTextColor.GRAY);
        }

        if (loggingCheckTask != null && !loggingCheckTask.isCancelled()) {
            loggingCheckTask.cancel();
            ApiManagerStatements.logToConsole("Logging check task cancelled.", NamedTextColor.GRAY);
        }

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
        incrementUpdateCount();
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
        incrementQueryCount();
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