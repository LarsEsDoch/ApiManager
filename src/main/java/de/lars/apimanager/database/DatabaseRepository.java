package de.lars.apimanager.database;

import de.lars.apimanager.ApiManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Centralized database repository with generic CRUD operations
 */
public class DatabaseRepository {
    private IDatabaseManager db() {
        return ApiManager.getInstance().getDatabaseManager();
    }

    // ==================== Generic Getters ====================

    /**
     * Get a String value from a table
     */
    public String getString(String table, String column, String whereClause, Object... params) {
        return db().query(conn -> {
            String sql = "SELECT " + column + " FROM " + table + " WHERE " + whereClause + " LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                setParameters(ps, params);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString(column);
                    return null;
                }
            }
        });
    }

    public CompletableFuture<String> getStringAsync(String table, String column, String whereClause, Object... params) {
        return db().queryAsync(conn -> {
            String sql = "SELECT " + column + " FROM " + table + " WHERE " + whereClause + " LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                setParameters(ps, params);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString(column);
                    return null;
                }
            }
        });
    }

    /**
     * Get an Integer value from a table
     */
    public Integer getInteger(String table, String column, String whereClause, Object... params) {
        return db().query(conn -> {
            String sql = "SELECT " + column + " FROM " + table + " WHERE " + whereClause + " LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                setParameters(ps, params);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt(column);
                    return null;
                }
            }
        });
    }

    public CompletableFuture<Integer> getIntegerAsync(String table, String column, String whereClause, Object... params) {
        return db().queryAsync(conn -> {
            String sql = "SELECT " + column + " FROM " + table + " WHERE " + whereClause + " LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                setParameters(ps, params);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt(column);
                    return null;
                }
            }
        });
    }

    /**
     * Get a Boolean value from a table
     */
    public Boolean getBoolean(String table, String column, String whereClause, Object... params) {
        return db().query(conn -> {
            String sql = "SELECT " + column + " FROM " + table + " WHERE " + whereClause + " LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                setParameters(ps, params);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getBoolean(column);
                    return null;
                }
            }
        });
    }

    public CompletableFuture<Boolean> getBooleanAsync(String table, String column, String whereClause, Object... params) {
        return db().queryAsync(conn -> {
            String sql = "SELECT " + column + " FROM " + table + " WHERE " + whereClause + " LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                setParameters(ps, params);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getBoolean(column);
                    return null;
                }
            }
        });
    }

    /**
     * Get an Instant (timestamp) value from a table
     */
    public Instant getInstant(String table, String column, String whereClause, Object... params) {
        return db().query(conn -> {
            String sql = "SELECT " + column + " FROM " + table + " WHERE " + whereClause + " LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                setParameters(ps, params);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Timestamp ts = rs.getTimestamp(column);
                        return ts != null ? ts.toInstant() : null;
                    }
                    return null;
                }
            }
        });
    }

    public CompletableFuture<Instant> getInstantAsync(String table, String column, String whereClause, Object... params) {
        return db().queryAsync(conn -> {
            String sql = "SELECT " + column + " FROM " + table + " WHERE " + whereClause + " LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                setParameters(ps, params);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Timestamp ts = rs.getTimestamp(column);
                        return ts != null ? ts.toInstant() : null;
                    }
                    return null;
                }
            }
        });
    }

    /**
     * Get a list of Strings from a table
     */
    public List<String> getStringList(String table, String column, String whereClause, Object... params) {
        return db().query(conn -> {
            String sql = "SELECT " + column + " FROM " + table;
            if (whereClause != null && !whereClause.isEmpty()) {
                sql += " WHERE " + whereClause;
            }
            List<String> results = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                setParameters(ps, params);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        results.add(rs.getString(column));
                    }
                }
            }
            return results;
        });
    }

    public CompletableFuture<List<String>> getStringListAsync(String table, String column, String whereClause, Object... params) {
        return db().queryAsync(conn -> {
            String sql = "SELECT " + column + " FROM " + table;
            if (whereClause != null && !whereClause.isEmpty()) {
                sql += " WHERE " + whereClause;
            }
            List<String> results = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                setParameters(ps, params);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        results.add(rs.getString(column));
                    }
                }
            }
            return results;
        });
    }

    /**
     * Check if a record exists
     */
    public boolean exists(String table, String whereClause, Object... params) {
        return db().query(conn -> {
            String sql = "SELECT 1 FROM " + table + " WHERE " + whereClause + " LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                setParameters(ps, params);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        });
    }

    public CompletableFuture<Boolean> existsAsync(String table, String whereClause, Object... params) {
        return db().queryAsync(conn -> {
            String sql = "SELECT 1 FROM " + table + " WHERE " + whereClause + " LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                setParameters(ps, params);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        });
    }

    // ==================== Generic Setters ====================

    /**
     * Update a single column value
     */
    public void updateColumn(String table, String column, Object value, String whereClause, Object... whereParams) {
        String sql = "UPDATE " + table + " SET " + column + " = ? WHERE " + whereClause;
        Object[] allParams = combineParams(new Object[]{value}, whereParams);
        db().update(sql, allParams);
    }

    public CompletableFuture<Void> updateColumnAsync(String table, String column, Object value, String whereClause, Object... whereParams) {
        String sql = "UPDATE " + table + " SET " + column + " = ? WHERE " + whereClause;
        Object[] allParams = combineParams(new Object[]{value}, whereParams);
        return db().updateAsync(sql, allParams);
    }

    /**
     * Update multiple columns at once
     */
    public void updateColumns(String table, String[] columns, Object[] values, String whereClause, Object... whereParams) {
        StringBuilder sql = new StringBuilder("UPDATE " + table + " SET ");
        for (int i = 0; i < columns.length; i++) {
            sql.append(columns[i]).append(" = ?");
            if (i < columns.length - 1) sql.append(", ");
        }
        sql.append(" WHERE ").append(whereClause);

        Object[] allParams = combineParams(values, whereParams);
        db().update(sql.toString(), allParams);
    }

    public CompletableFuture<Void> updateColumnsAsync(String table, String[] columns, Object[] values, String whereClause, Object... whereParams) {
        StringBuilder sql = new StringBuilder("UPDATE " + table + " SET ");
        for (int i = 0; i < columns.length; i++) {
            sql.append(columns[i]).append(" = ?");
            if (i < columns.length - 1) sql.append(", ");
        }
        sql.append(" WHERE ").append(whereClause);

        Object[] allParams = combineParams(values, whereParams);
        return db().updateAsync(sql.toString(), allParams);
    }

    /**
     * Delete records from table
     */
    public void delete(String table, String whereClause, Object... params) {
        String sql = "DELETE FROM " + table + " WHERE " + whereClause;
        db().update(sql, params);
    }

    public CompletableFuture<Void> deleteAsync(String table, String whereClause, Object... params) {
        String sql = "DELETE FROM " + table + " WHERE " + whereClause;
        return db().updateAsync(sql, params);
    }

    /**
     * Count rows in table
     */
    public int count(String table, String whereClause, Object... params) {
        return db().query(conn -> {
            String sql = "SELECT COUNT(*) AS row_count FROM " + table;
            if (whereClause != null && !whereClause.isEmpty()) {
                sql += " WHERE " + whereClause;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                setParameters(ps, params);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("row_count");
                }
            }
            return 0;
        });
    }

    public CompletableFuture<Integer> countAsync(String table, String whereClause, Object... params) {
        return db().queryAsync(conn -> {
            String sql = "SELECT COUNT(*) AS row_count FROM " + table;
            if (whereClause != null && !whereClause.isEmpty()) {
                sql += " WHERE " + whereClause;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                setParameters(ps, params);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("row_count");
                }
            }
            return 0;
        });
    }

    // ==================== Helper Methods ====================

    /**
     * Set parameters on a PreparedStatement
     */
    private void setParameters(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            if (param instanceof Instant) {
                ps.setTimestamp(i + 1, Timestamp.from((Instant) param));
            } else if (param == null) {
                ps.setObject(i + 1, null);
            } else {
                ps.setObject(i + 1, param);
            }
        }
    }

    /**
     * Combine two parameter arrays
     */
    private Object[] combineParams(Object[] first, Object[] second) {
        Object[] result = new Object[first.length + second.length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}