package de.lars.apimanager.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public interface IDatabaseManager {
    void update(String sql, Object... params);

    CompletableFuture<Void> updateAsync(String sql, Object... params);

    <T> T query(DatabaseManager.SQLFunction<java.sql.Connection, T> function);

    <T> CompletableFuture<T> queryAsync(DatabaseManager.SQLFunction<java.sql.Connection, T> function);

    Connection getConnection() throws SQLException;

    void close();
}