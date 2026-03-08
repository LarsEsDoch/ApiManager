package dev.lars.apimanager.database;

import dev.lars.apimanager.ApiManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DatabaseRepository {
    private IDatabaseManager db() {
        return ApiManager.getInstance().getDatabaseManager();
    }

    public String getString(String table, String column, String whereClause, Object... params) {
        String safeTable = SqlIdentifierValidator.validate(table);
        String safeColumn = SqlIdentifierValidator.validate(column);
        return db().query(conn -> {
            String sql = "SELECT " + safeColumn + " FROM " + safeTable + " WHERE " + whereClause + " LIMIT 1";
            db().logSqlQuery(sql, params);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                setParameters(ps, params);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString(safeColumn);
                    return null;
                }
            }
        });
    }

    public CompletableFuture<String> getStringAsync(String table, String column, String whereClause, Object... params) {
        String safeTable = SqlIdentifierValidator.validate(table);
        String safeColumn = SqlIdentifierValidator.validate(column);
        return db().queryAsync(conn -> {
            String sql = "SELECT " + safeColumn + " FROM " + safeTable + " WHERE " + whereClause + " LIMIT 1";
            db().logSqlQuery(sql, params);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                setParameters(ps, params);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString(safeColumn);
                    return null;
                }
            }
        });
    }

    public Integer getInteger(String table, String column, String whereClause, Object... params) {
        String safeTable = SqlIdentifierValidator.validate(table);
        String safeColumn = SqlIdentifierValidator.validate(column);
        return db().query(conn -> {
            String sql = "SELECT " + safeColumn + " FROM " + safeTable + " WHERE " + whereClause + " LIMIT 1";
            db().logSqlQuery(sql, params);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                setParameters(ps, params);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int value = rs.getInt(safeColumn);
                        if (rs.wasNull()) return null;
                        return value;
                    }
                    return null;
                }
            }
        });
    }

    public CompletableFuture<Integer> getIntegerAsync(String table, String column, String whereClause, Object... params) {
        String safeTable = SqlIdentifierValidator.validate(table);
        String safeColumn = SqlIdentifierValidator.validate(column);
        return db().queryAsync(conn -> {
            String sql = "SELECT " + safeColumn + " FROM " + safeTable + " WHERE " + whereClause + " LIMIT 1";
            db().logSqlQuery(sql, params);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                setParameters(ps, params);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int value = rs.getInt(safeColumn);
                        if (rs.wasNull()) return null;
                        return value;
                    }
                    return null;
                }
            }
        });
    }

    public Boolean getBoolean(String table, String column, String whereClause, Object... params) {
        String safeTable = SqlIdentifierValidator.validate(table);
        String safeColumn = SqlIdentifierValidator.validate(column);
        return db().query(conn -> {
            String sql = "SELECT " + safeColumn + " FROM " + safeTable + " WHERE " + whereClause + " LIMIT 1";
            db().logSqlQuery(sql, params);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                setParameters(ps, params);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        boolean value = rs.getBoolean(safeColumn);
                        return rs.wasNull() ? null : value;
                    }
                    return null;
                }
            }
        });
    }

    public CompletableFuture<Boolean> getBooleanAsync(String table, String column, String whereClause, Object... params) {
        String safeTable = SqlIdentifierValidator.validate(table);
        String safeColumn = SqlIdentifierValidator.validate(column);
        return db().queryAsync(conn -> {
            String sql = "SELECT " + safeColumn + " FROM " + safeTable + " WHERE " + whereClause + " LIMIT 1";
            db().logSqlQuery(sql, params);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                setParameters(ps, params);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        boolean value = rs.getBoolean(safeColumn);
                        return rs.wasNull() ? null : value;
                    }
                    return null;
                }
            }
        });
    }

    public Instant getInstant(String table, String column, String whereClause, Object... params) {
        String safeTable = SqlIdentifierValidator.validate(table);
        String safeColumn = SqlIdentifierValidator.validate(column);
        return db().query(conn -> {
            String sql = "SELECT " + safeColumn + " FROM " + safeTable + " WHERE " + whereClause + " LIMIT 1";
            db().logSqlQuery(sql, params);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                setParameters(ps, params);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Timestamp ts = rs.getTimestamp(safeColumn);
                        return ts != null ? ts.toInstant() : null;
                    }
                    return null;
                }
            }
        });
    }

    public CompletableFuture<Instant> getInstantAsync(String table, String column, String whereClause, Object... params) {
        String safeTable = SqlIdentifierValidator.validate(table);
        String safeColumn = SqlIdentifierValidator.validate(column);
        return db().queryAsync(conn -> {
            String sql = "SELECT " + safeColumn + " FROM " + safeTable + " WHERE " + whereClause + " LIMIT 1";
            db().logSqlQuery(sql, params);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                setParameters(ps, params);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Timestamp ts = rs.getTimestamp(safeColumn);
                        return ts != null ? ts.toInstant() : null;
                    }
                    return null;
                }
            }
        });
    }

    public List<String> getStringList(String table, String column, String whereClause, Object... params) {
        String safeTable = SqlIdentifierValidator.validate(table);
        String safeColumn = SqlIdentifierValidator.validate(column);
        return db().query(conn -> {
            String sql = "SELECT " + safeColumn + " FROM " + safeTable;
            if (whereClause != null && !whereClause.isEmpty()) {
                sql += " WHERE " + whereClause;
            }
            db().logSqlQuery(sql, params);
            List<String> results = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                setParameters(ps, params);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        results.add(rs.getString(safeColumn));
                    }
                }
            }
            return results;
        });
    }

    public CompletableFuture<List<String>> getStringListAsync(String table, String column, String whereClause, Object... params) {
        String safeTable = SqlIdentifierValidator.validate(table);
        String safeColumn = SqlIdentifierValidator.validate(column);
        return db().queryAsync(conn -> {
            String sql = "SELECT " + safeColumn + " FROM " + safeTable;
            if (whereClause != null && !whereClause.isEmpty()) {
                sql += " WHERE " + whereClause;
            }
            db().logSqlQuery(sql, params);
            List<String> results = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                setParameters(ps, params);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        results.add(rs.getString(safeColumn));
                    }
                }
            }
            return results;
        });
    }

    public boolean exists(String table, String whereClause, Object... params) {
        String safeTable = SqlIdentifierValidator.validate(table);
        return db().query(conn -> {
            String sql = "SELECT 1 FROM " + safeTable + " WHERE " + whereClause + " LIMIT 1";
            db().logSqlQuery(sql, params);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                setParameters(ps, params);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        });
    }

    public CompletableFuture<Boolean> existsAsync(String table, String whereClause, Object... params) {
        String safeTable = SqlIdentifierValidator.validate(table);
        return db().queryAsync(conn -> {
            String sql = "SELECT 1 FROM " + safeTable + " WHERE " + whereClause + " LIMIT 1";
            db().logSqlQuery(sql, params);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                setParameters(ps, params);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        });
    }

    public void updateColumn(String table, String column, Object value, String whereClause, Object... whereParams) {
        String safeTable = SqlIdentifierValidator.validate(table);
        String safeColumn = SqlIdentifierValidator.validate(column);
        String sql = "UPDATE " + safeTable + " SET " + safeColumn + " = ? WHERE " + whereClause;
        Object[] allParams = combineParams(new Object[]{value}, whereParams);
        db().update(sql, allParams);
    }

    public CompletableFuture<Void> updateColumnAsync(String table, String column, Object value, String whereClause, Object... whereParams) {
        String safeTable = SqlIdentifierValidator.validate(table);
        String safeColumn = SqlIdentifierValidator.validate(column);
        String sql = "UPDATE " + safeTable + " SET " + safeColumn + " = ? WHERE " + whereClause;
        Object[] allParams = combineParams(new Object[]{value}, whereParams);
        return db().updateAsync(sql, allParams);
    }

    public void updateColumns(String table, String[] columns, Object[] values, String whereClause, Object... whereParams) {
        String safeTable = SqlIdentifierValidator.validate(table);
        String[] safeColumns = SqlIdentifierValidator.validateAll(columns);
        StringBuilder sql = new StringBuilder("UPDATE " + safeTable + " SET ");
        for (int i = 0; i < safeColumns.length; i++) {
            sql.append(safeColumns[i]).append(" = ?");
            if (i < safeColumns.length - 1) sql.append(", ");
        }
        sql.append(" WHERE ").append(whereClause);
        Object[] allParams = combineParams(values, whereParams);
        db().update(sql.toString(), allParams);
    }

    public CompletableFuture<Void> updateColumnsAsync(String table, String[] columns, Object[] values, String whereClause, Object... whereParams) {
        String safeTable = SqlIdentifierValidator.validate(table);
        String[] safeColumns = SqlIdentifierValidator.validateAll(columns);
        StringBuilder sql = new StringBuilder("UPDATE " + safeTable + " SET ");
        for (int i = 0; i < safeColumns.length; i++) {
            sql.append(safeColumns[i]).append(" = ?");
            if (i < safeColumns.length - 1) sql.append(", ");
        }
        sql.append(" WHERE ").append(whereClause);
        Object[] allParams = combineParams(values, whereParams);
        return db().updateAsync(sql.toString(), allParams);
    }

    public void increaseColumn(String table, String column, Number amount, String whereClause, Object... whereParams) {
        String safeTable = SqlIdentifierValidator.validate(table);
        String safeColumn = SqlIdentifierValidator.validate(column);
        String sql = "UPDATE " + safeTable + " SET " + safeColumn + " = " + safeColumn + " + ? WHERE " + whereClause;
        Object[] allParams = combineParams(new Object[]{amount}, whereParams);
        db().update(sql, allParams);
    }

    public CompletableFuture<Void> increaseColumnAsync(String table, String column, Number amount, String whereClause, Object... whereParams) {
        String safeTable = SqlIdentifierValidator.validate(table);
        String safeColumn = SqlIdentifierValidator.validate(column);
        String sql = "UPDATE " + safeTable + " SET " + safeColumn + " = " + safeColumn + " + ? WHERE " + whereClause;
        Object[] allParams = combineParams(new Object[]{amount}, whereParams);
        return db().updateAsync(sql, allParams);
    }

    public void decreaseColumn(String table, String column, Number amount, String whereClause, Object... whereParams) {
        String safeTable = SqlIdentifierValidator.validate(table);
        String safeColumn = SqlIdentifierValidator.validate(column);
        String sql = "UPDATE " + safeTable + " SET " + safeColumn + " = " + safeColumn + " - ? WHERE " + whereClause;
        Object[] allParams = combineParams(new Object[]{amount}, whereParams);
        db().update(sql, allParams);
    }

    public CompletableFuture<Void> decreaseColumnAsync(String table, String column, Number amount, String whereClause, Object... whereParams) {
        String safeTable = SqlIdentifierValidator.validate(table);
        String safeColumn = SqlIdentifierValidator.validate(column);
        String sql = "UPDATE " + safeTable + " SET " + safeColumn + " = " + safeColumn + " - ? WHERE " + whereClause;
        Object[] allParams = combineParams(new Object[]{amount}, whereParams);
        return db().updateAsync(sql, allParams);
    }

    public void insert(String table, String[] columns, Object... values) {
        String safeTable = SqlIdentifierValidator.validate(table);
        String[] safeColumns = SqlIdentifierValidator.validateAll(columns);

        String columnList = String.join(", ", safeColumns);
        String placeholders = "?, ".repeat(safeColumns.length);
        placeholders = placeholders.substring(0, placeholders.length() - 2); // trim trailing ", "

        String sql = "INSERT INTO " + safeTable + " (" + columnList + ") VALUES (" + placeholders + ")";
        db().update(sql, values);
    }

    public void insertIgnore(String table, String[] columns, Object... values) {
        String safeTable = SqlIdentifierValidator.validate(table);
        String[] safeColumns = SqlIdentifierValidator.validateAll(columns);

        String columnList = String.join(", ", safeColumns);
        String placeholders = "?, ".repeat(safeColumns.length);
        placeholders = placeholders.substring(0, placeholders.length() - 2);

        String sql = "INSERT IGNORE INTO " + safeTable + " (" + columnList + ") VALUES (" + placeholders + ")";
        db().update(sql, values);
    }

    public CompletableFuture<Void> insertAsync(String table, String[] columns, Object... values) {
        String safeTable = SqlIdentifierValidator.validate(table);
        String[] safeColumns = SqlIdentifierValidator.validateAll(columns);

        String columnList = String.join(", ", safeColumns);
        String placeholders = "?, ".repeat(safeColumns.length);
        placeholders = placeholders.substring(0, placeholders.length() - 2);

        String sql = "INSERT INTO " + safeTable + " (" + columnList + ") VALUES (" + placeholders + ")";
        return db().updateAsync(sql, values);
    }

    public CompletableFuture<Void> insertIgnoreAsync(String table, String[] columns, Object... values) {
        String safeTable = SqlIdentifierValidator.validate(table);
        String[] safeColumns = SqlIdentifierValidator.validateAll(columns);

        String columnList = String.join(", ", safeColumns);
        String placeholders = "?, ".repeat(safeColumns.length);
        placeholders = placeholders.substring(0, placeholders.length() - 2);

        String sql = "INSERT IGNORE INTO " + safeTable + " (" + columnList + ") VALUES (" + placeholders + ")";
        return db().updateAsync(sql, values);
    }

    public void delete(String table, String whereClause, Object... params) {
        String safeTable = SqlIdentifierValidator.validate(table);
        String sql = "DELETE FROM " + safeTable + " WHERE " + whereClause;
        db().update(sql, params);
    }

    public CompletableFuture<Void> deleteAsync(String table, String whereClause, Object... params) {
        String safeTable = SqlIdentifierValidator.validate(table);
        String sql = "DELETE FROM " + safeTable + " WHERE " + whereClause;
        return db().updateAsync(sql, params);
    }

    public int count(String table, String whereClause, Object... params) {
        String safeTable = SqlIdentifierValidator.validate(table);
        return db().query(conn -> {
            String sql = "SELECT COUNT(*) AS row_count FROM " + safeTable;
            if (whereClause != null && !whereClause.isEmpty()) {
                sql += " WHERE " + whereClause;
            }
            db().logSqlQuery(sql, params);
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
        String safeTable = SqlIdentifierValidator.validate(table);
        return db().queryAsync(conn -> {
            String sql = "SELECT COUNT(*) AS row_count FROM " + safeTable;
            if (whereClause != null && !whereClause.isEmpty()) {
                sql += " WHERE " + whereClause;
            }
            db().logSqlQuery(sql, params);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                setParameters(ps, params);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("row_count");
                }
            }
            return 0;
        });
    }

    private void setParameters(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            if (param instanceof Instant) {
                ps.setTimestamp(i + 1, Timestamp.from((Instant) param));
            } else ps.setObject(i + 1, param);
        }
    }

    private Object[] combineParams(Object[] first, Object[] second) {
        Object[] result = new Object[first.length + second.length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}