package dev.lars.apimanager.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.lars.apimanager.ApiManager;
import dev.lars.apimanager.utils.ApiManagerStatements;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class DatabaseManager implements IDatabaseManager {

    private static final String CONSOLE_KEY = "__CONSOLE__";

    private volatile HikariDataSource dataSource;
    private final ExecutorService asyncExecutor;
    private final CompletableFuture<Void> ready = new CompletableFuture<>();

    private final ConcurrentHashMap<String, Long> loggingSubscribers = new ConcurrentHashMap<>();

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
            this::checkLoggingTimeouts, 1L, 1L);
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

    private void checkLoggingTimeouts() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<String, Long>> it = loggingSubscribers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Long> entry = it.next();
            long expiry = entry.getValue();
            if (expiry > 0 && now >= expiry) {
                it.remove();
                notifySubscriber(entry.getKey(),
                    Component.text("SQL query logging automatically disabled (timeout reached)", NamedTextColor.GRAY));
            }
        }
    }

    private static String senderKey(CommandSender sender) {
        if (sender instanceof Player player) {
            return player.getUniqueId().toString();
        }
        return CONSOLE_KEY;
    }

    private void notifySubscriber(String key, Component message) {
        Component full = ApiManagerStatements.getPrefix().append(message);
        if (CONSOLE_KEY.equals(key)) {
            Bukkit.getConsoleSender().sendMessage(full);
        } else {
            Player player = Bukkit.getPlayer(UUID.fromString(key));
            if (player != null && player.isOnline()) {
                player.sendMessage(full);
            }
        }
    }

    @Override
    public void enableSqlLogging(CommandSender sender, long durationMs) {
        String key = senderKey(sender);
        if (durationMs > 0) {
            loggingSubscribers.put(key, System.currentTimeMillis() + durationMs);
        } else {
            loggingSubscribers.put(key, 0L);
        }
    }

    @Override
    public void disableSqlLogging(CommandSender sender) {
        loggingSubscribers.remove(senderKey(sender));
    }

    @Override
    public boolean isSqlLoggingEnabled(CommandSender sender) {
        return loggingSubscribers.containsKey(senderKey(sender));
    }

    @Override
    public long getSqlLoggingTimeRemaining(CommandSender sender) {
        Long expiry = loggingSubscribers.get(senderKey(sender));
        if (expiry == null) return 0;
        if (expiry == 0) return 0;
        long remaining = expiry - System.currentTimeMillis();
        return Math.max(0, remaining);
    }

    @Override
    public boolean hasAnyLoggingSubscribers() {
        return !loggingSubscribers.isEmpty();
    }

    @Override
    public void logSqlQuery(String sql, Object... params) {
        if (loggingSubscribers.isEmpty()) return;
        Component message = buildLogMessage("[SQL Query] ", sql, params, NamedTextColor.AQUA);
        broadcastToSubscribers(message);
    }

    private void logSqlUpdate(String sql, Object... params) {
        if (loggingSubscribers.isEmpty()) return;
        Component message = buildLogMessage("[SQL Update] ", sql, params, NamedTextColor.LIGHT_PURPLE);
        broadcastToSubscribers(message);
    }

    private Component buildLogMessage(String prefix, String sql, Object[] params, NamedTextColor color) {
        StringBuilder log = new StringBuilder();
        log.append(prefix).append(sql.trim());

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

        return Component.text(log.toString(), color);
    }

    private void broadcastToSubscribers(Component message) {
        Component full = ApiManagerStatements.getPrefix().append(message);
        for (String key : loggingSubscribers.keySet()) {
            if (CONSOLE_KEY.equals(key)) {
                Bukkit.getConsoleSender().sendMessage(full);
            } else {
                Player player = Bukkit.getPlayer(UUID.fromString(key));
                if (player != null && player.isOnline()) {
                    player.sendMessage(full);
                } else {
                    loggingSubscribers.remove(key);
                }
            }
        }
    }

    public CompletableFuture<Void> readyFuture() {
        return ready;
    }

    public boolean isReady() {
        return ready.isDone() && dataSource != null;
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

    @Override
    public void update(String sql, Object... params) {
        incrementUpdateCount();
        logSqlUpdate(sql, params);

        Connection conn = null;
        PreparedStatement ps = null;
        boolean originalAutoCommit = true;

        try {
            conn = getConnection();
            originalAutoCommit = conn.getAutoCommit();

            conn.setAutoCommit(false);

            ps = conn.prepareStatement(sql);
            setParams(ps, params);
            ps.executeUpdate();

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    ApiManagerStatements.logToConsole(
                        "Rollback failed: " + rollbackEx.getMessage(),
                        NamedTextColor.RED
                    );
                }
            }
            ApiManagerStatements.logToConsole(
                "SQL update failed: " + sql + " | Error: " + e.getMessage(),
                NamedTextColor.RED
            );
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    ApiManagerStatements.logToConsole(
                        "Failed to close PreparedStatement: " + e.getMessage(),
                        NamedTextColor.GOLD
                    );
                }
            }

            if (conn != null) {
                try {
                    conn.setAutoCommit(originalAutoCommit);
                } catch (SQLException e) {
                    ApiManagerStatements.logToConsole(
                        "Failed to restore autoCommit state: " + e.getMessage(),
                        NamedTextColor.GOLD
                    );
                }

                try {
                    conn.close();
                } catch (SQLException e) {
                    ApiManagerStatements.logToConsole(
                        "Failed to close connection: " + e.getMessage(),
                        NamedTextColor.GOLD
                    );
                }
            }
        }
    }

    @Override
    public CompletableFuture<Void> updateAsync(String sql, Object... params) {
        return CompletableFuture.runAsync(() -> update(sql, params), asyncExecutor);
    }

    @Override
    public <T> T query(SQLFunction<Connection, T> function) {
        incrementQueryCount();

        try (Connection conn = getConnection()) {
            return function.apply(conn);
        } catch (SQLException e) {
            ApiManagerStatements.logToConsole(
                "SQL query failed: " + e.getMessage(),
                NamedTextColor.RED
            );
            return null;
        }
    }

    @Override
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