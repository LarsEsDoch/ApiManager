package de.lars.apiManager.database;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import de.lars.apiManager.Main;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MySQL {

    @SuppressWarnings("UnstableApiUsage")
    private final LoadingCache<Integer, Connection> cache = CacheBuilder
            .newBuilder().expireAfterAccess(10, TimeUnit.SECONDS)
            .removalListener((RemovalListener<Integer, Connection>) removalNotification -> {
                try {
                    if (removalNotification.getValue() != null) {
                        removalNotification.getValue().close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }).build(new CacheLoader<Integer, Connection>() {
                @Override
                public Connection load(Integer integer) throws Exception {
                    return createConnection();
                }
            });

    private String connectionUrl, database, user, password;
    private Integer port;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private MySQL(
            String connectionUrl,
            String database,
            String user,
            String password,
            Integer port
    ) {
        this.connectionUrl = connectionUrl;
        this.database = database;
        this.user = user;
        this.password = password;
        this.port = port;
        startConnectionRenewalTask(1, TimeUnit.HOURS);
    }

    public void updateAsync(String update, Object... objs) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            try {
                Connection connection = cache.get(1);
                try (PreparedStatement p = connection.prepareStatement(update)) {
                    setArguments(objs, p);
                    p.executeUpdate();
                }
            } catch (SQLException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    public void update(String update, Object... objs) {
        try {
            Connection connection = cache.get(1);
            try (PreparedStatement p = connection.prepareStatement(update)) {
                setArguments(objs, p);
                p.executeUpdate();
            }
        } catch (SQLException | ExecutionException e) {
            e.printStackTrace();
        }
    }


    public ResultSet query(String query, Object... objs) {
        try {
            Connection connection = cache.get(1);
            PreparedStatement p = connection.prepareStatement(query);
            setArguments(objs, p);
            return p.executeQuery();
        } catch (SQLException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @FunctionalInterface
    public interface ResultHandler<T> {
        T handle(ResultSet rs) throws SQLException;
    }

    public void renewConnection() {
        try {
            cache.invalidate(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setArguments(Object[] objs, PreparedStatement p) throws SQLException {
        for (int i = 0; i < objs.length; i++) {
            Object obj = objs[i];
            if (obj instanceof String) {
                p.setString(i + 1, (String) obj);
            } else if (obj instanceof Integer) {
                p.setInt(i + 1, (Integer) obj);
            } else if (obj instanceof Date) {
                p.setDate(i + 1, (Date) obj);
            } else if (obj instanceof Timestamp) {
                p.setTimestamp(i + 1, (Timestamp) obj);
            } else if (obj instanceof Boolean) {
                p.setBoolean(i + 1, (Boolean) obj);
            } else if (obj instanceof Float) {
                p.setFloat(i + 1, (Float) obj);
            } else if (obj instanceof Double) {
                p.setDouble(i + 1, (Double) obj);
            } else if (obj instanceof Long) {
                p.setLong(i + 1, (Long) obj);
            }
        }
    }

    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://" + connectionUrl + ":" + port + "/" + database, user, password);
    }

    private void startConnectionRenewalTask(long period, TimeUnit unit) {
        scheduler.scheduleAtFixedRate(this::renewConnection, period, period, unit);
    }

    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(1, TimeUnit.MINUTES)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
        cache.invalidateAll();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private String connectionUrl, database, user, password;
        private Integer port;

        private Builder() {}

        public Builder withUrl(String url) {
            this.connectionUrl = url;
            return this;
        }

        public Builder withDatabase(String database) {
            this.database = database;
            return this;
        }

        public Builder withUser(String user) {
            this.user = user;
            return this;
        }

        public Builder withPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder withPort(Integer port) {
            this.port = port;
            return this;
        }

        public MySQL create() {
            Preconditions.checkNotNull(connectionUrl, "Connection URL is null");
            Preconditions.checkNotNull(database, "Database is null");
            Preconditions.checkNotNull(user, "Username is null");
            Preconditions.checkNotNull(password, "Password is null");
            Preconditions.checkNotNull(port, "Port is null");
            return new MySQL(connectionUrl, database, user, password, port);
        }
    }
}
