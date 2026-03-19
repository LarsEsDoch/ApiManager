package dev.lars.apimanager.apis.chunkAPI;

import dev.lars.apimanager.ApiManager;
import dev.lars.apimanager.database.DatabaseRepository;
import dev.lars.apimanager.database.IDatabaseManager;
import dev.lars.apimanager.utils.ApiManagerValidateParameter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ChunkAPIImpl implements IChunkAPI {
    private static final String TABLE = "claimed_chunks";
    private static final String PERMISSION_TABLE = "chunk_permissions";

    private DatabaseRepository repo() {
        return new DatabaseRepository();
    }

    private IDatabaseManager db() {
        return ApiManager.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db().update(String.format("""
            CREATE TABLE IF NOT EXISTS %s (
                owner_uuid CHAR(36) NOT NULL,
                server_id VARCHAR(64) NOT NULL,
                world VARCHAR(64) NOT NULL,
                x INT NOT NULL,
                z INT NOT NULL,
                trust_all BOOLEAN NOT NULL DEFAULT FALSE,
                flags JSON NOT NULL DEFAULT (JSON_OBJECT()),
                claimed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                PRIMARY KEY (server_id, world, x, z),
                INDEX idx_owner (owner_uuid),
                FOREIGN KEY (owner_uuid) REFERENCES players(uuid) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """, TABLE));

        db().update(String.format("""
            CREATE TABLE IF NOT EXISTS %s (
                server_id VARCHAR(64) NOT NULL,
                world VARCHAR(64) NOT NULL,
                x INT NOT NULL,
                z INT NOT NULL,
                player_uuid CHAR(36) NOT NULL,
                permission_type ENUM('ALLOW', 'DENY') NOT NULL,
                PRIMARY KEY (server_id, world, x, z, player_uuid),
                FOREIGN KEY (server_id, world, x, z)
                    REFERENCES claimed_chunks(server_id, world, x, z) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """, PERMISSION_TABLE));
    }

    private String chunkWhere() {
        return "server_id = ? AND world = ? AND x = ? AND z = ?";
    }

    private Object[] chunkParams(Chunk chunk) {
        return new Object[]{ApiManager.getServerId(), chunk.getWorld().getName(), chunk.getX(), chunk.getZ()};
    }

    private Object[] append(Object[] base, Object extra) {
        Object[] result = Arrays.copyOf(base, base.length + 1);
        result[base.length] = extra;
        return result;
    }

    private void setParameters(PreparedStatement ps, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }

    private boolean isTrustAllActive(Chunk chunk) {
        Boolean val = repo().getBoolean(TABLE, "trust_all", chunkWhere(), chunkParams(chunk));
        return val != null && val;
    }

    private CompletableFuture<Boolean> isTrustAllActiveAsync(Chunk chunk) {
        return repo().getBooleanAsync(TABLE, "trust_all", chunkWhere(), chunkParams(chunk))
                .thenApply(val -> val != null && val);
    }

    @Override
    public Instant getCreated(Chunk chunk) {
        ApiManagerValidateParameter.validateChunk(chunk);
        return repo().getInstant(TABLE, "created_at", chunkWhere(), chunkParams(chunk));
    }

    @Override
    public CompletableFuture<Instant> getCreatedAsync(Chunk chunk) {
        ApiManagerValidateParameter.validateChunk(chunk);
        return repo().getInstantAsync(TABLE, "created_at", chunkWhere(), chunkParams(chunk));
    }

    @Override
    public Instant getUpdated(Chunk chunk) {
        ApiManagerValidateParameter.validateChunk(chunk);
        return repo().getInstant(TABLE, "updated_at", chunkWhere(), chunkParams(chunk));
    }

    @Override
    public CompletableFuture<Instant> getUpdatedAsync(Chunk chunk) {
        ApiManagerValidateParameter.validateChunk(chunk);
        return repo().getInstantAsync(TABLE, "updated_at", chunkWhere(), chunkParams(chunk));
    }

    @Override
    public Instant getClaimed(Chunk chunk) {
        ApiManagerValidateParameter.validateChunk(chunk);
        return repo().getInstant(TABLE, "claimed_at", chunkWhere(), chunkParams(chunk));
    }

    @Override
    public CompletableFuture<Instant> getClaimedAsync(Chunk chunk) {
        ApiManagerValidateParameter.validateChunk(chunk);
        return repo().getInstantAsync(TABLE, "claimed_at", chunkWhere(), chunkParams(chunk));
    }

    private static final String CLAIM_SQL_TEMPLATE = """
            INSERT INTO %s (owner_uuid, server_id, world, x, z, claimed_at)
            VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
            ON DUPLICATE KEY UPDATE
                owner_uuid = VALUES(owner_uuid),
                claimed_at = CURRENT_TIMESTAMP,
                updated_at = CURRENT_TIMESTAMP
            """;

    @Override
    public void claimChunk(OfflinePlayer player, Chunk chunk) {
        ApiManagerValidateParameter.validatePlayer(player);
        ApiManagerValidateParameter.validateChunk(chunk);

        db().update(String.format(CLAIM_SQL_TEMPLATE, TABLE),
                player.getUniqueId().toString(),
                ApiManager.getServerId(),
                chunk.getWorld().getName(),
                chunk.getX(),
                chunk.getZ());

        ApiManager.getInstance().getLimitAPI().decreaseMaxChunks(player, 1);
    }

    @Override
    public CompletableFuture<Void> claimChunkAsync(OfflinePlayer player, Chunk chunk) {
        ApiManagerValidateParameter.validatePlayer(player);
        ApiManagerValidateParameter.validateChunk(chunk);

        return db().updateAsync(String.format(CLAIM_SQL_TEMPLATE, TABLE),
                        player.getUniqueId().toString(),
                        ApiManager.getServerId(),
                        chunk.getWorld().getName(),
                        chunk.getX(),
                        chunk.getZ())
                .thenCompose(v -> ApiManager.getInstance().getLimitAPI().decreaseMaxChunksAsync(player, 1));
    }

    @Override
    public void unclaimChunk(OfflinePlayer player, Chunk chunk) {
        ApiManagerValidateParameter.validatePlayer(player);
        ApiManagerValidateParameter.validateChunk(chunk);

        repo().delete(TABLE, chunkWhere(), chunkParams(chunk));
        ApiManager.getInstance().getLimitAPI().increaseMaxChunks(player, 1);
    }

    @Override
    public CompletableFuture<Void> unclaimChunkAsync(OfflinePlayer player, Chunk chunk) {
        ApiManagerValidateParameter.validatePlayer(player);
        ApiManagerValidateParameter.validateChunk(chunk);

        return repo().deleteAsync(TABLE, chunkWhere(), chunkParams(chunk))
                .thenCompose(v -> {
                    try {
                        return ApiManager.getInstance().getLimitAPI().increaseMaxChunksAsync(player, 1);
                    } catch (Exception ex) {
                        return CompletableFuture.completedFuture(null);
                    }
                });
    }

    @Override
    public void setFlags(Chunk chunk, String flagsJson) {
        ApiManagerValidateParameter.validateChunk(chunk);
        repo().updateColumn(TABLE, "flags", flagsJson == null ? "{}" : flagsJson,
                chunkWhere(), chunkParams(chunk));
    }

    @Override
    public CompletableFuture<Void> setFlagsAsync(Chunk chunk, String flagsJson) {
        ApiManagerValidateParameter.validateChunk(chunk);
        return repo().updateColumnAsync(TABLE, "flags", flagsJson == null ? "{}" : flagsJson,
                chunkWhere(), chunkParams(chunk));
    }

    @Override
    public String getFlags(Chunk chunk) {
        ApiManagerValidateParameter.validateChunk(chunk);
        String flags = repo().getString(TABLE, "flags", chunkWhere(), chunkParams(chunk));
        return flags != null ? flags : "{}";
    }

    @Override
    public CompletableFuture<String> getFlagsAsync(Chunk chunk) {
        ApiManagerValidateParameter.validateChunk(chunk);
        return repo().getStringAsync(TABLE, "flags", chunkWhere(), chunkParams(chunk))
                .thenApply(flags -> flags != null ? flags : "{}");
    }

    /**
     * Returns the effective list of trusted player UUIDs for this chunk.
     * <p>
     * When trust_all is active with no DENY exceptions, returns ["*"] as a sentinel
     * meaning "everyone is trusted". When trust_all is active but DENY records exist,
     * the sentinel is dropped and the full expanded list (all offline players minus
     * denied) is returned instead, so callers always get a concrete picture.
     * When trust_all is inactive, returns the explicitly ALLOW'd UUIDs.
     */
    @Override
    public List<String> getFriends(Chunk chunk) {
        ApiManagerValidateParameter.validateChunk(chunk);
        return db().query(conn -> {
            boolean trustAll = false;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT trust_all FROM " + TABLE + " WHERE " + chunkWhere())) {
                setParameters(ps, chunkParams(chunk));
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return new ArrayList<>();
                    trustAll = rs.getBoolean("trust_all");
                }
            }

            if (trustAll) {
                Set<String> denied = new HashSet<>();
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT player_uuid FROM " + PERMISSION_TABLE
                                + " WHERE " + chunkWhere() + " AND permission_type = 'DENY'")) {
                    setParameters(ps, chunkParams(chunk));
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) denied.add(rs.getString("player_uuid"));
                    }
                }
                if (denied.isEmpty()) return Collections.singletonList("*");
                return Arrays.stream(Bukkit.getOfflinePlayers())
                        .map(p -> p.getUniqueId().toString())
                        .filter(id -> !denied.contains(id))
                        .collect(Collectors.toList());
            }

            List<String> allowed = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT player_uuid FROM " + PERMISSION_TABLE
                            + " WHERE " + chunkWhere() + " AND permission_type = 'ALLOW'")) {
                setParameters(ps, chunkParams(chunk));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) allowed.add(rs.getString("player_uuid"));
                }
            }
            return allowed;
        });
    }

    @Override
    public CompletableFuture<List<String>> getFriendsAsync(Chunk chunk) {
        ApiManagerValidateParameter.validateChunk(chunk);
        return db().queryAsync(conn -> {
            boolean trustAll = false;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT trust_all FROM " + TABLE + " WHERE " + chunkWhere())) {
                setParameters(ps, chunkParams(chunk));
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return new ArrayList<>();
                    trustAll = rs.getBoolean("trust_all");
                }
            }

            if (trustAll) {
                Set<String> denied = new HashSet<>();
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT player_uuid FROM " + PERMISSION_TABLE
                                + " WHERE " + chunkWhere() + " AND permission_type = 'DENY'")) {
                    setParameters(ps, chunkParams(chunk));
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) denied.add(rs.getString("player_uuid"));
                    }
                }
                if (denied.isEmpty()) return Collections.singletonList("*");
                return Arrays.stream(Bukkit.getOfflinePlayers())
                        .map(p -> p.getUniqueId().toString())
                        .filter(id -> !denied.contains(id))
                        .collect(Collectors.toList());
            }

            List<String> allowed = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT player_uuid FROM " + PERMISSION_TABLE
                            + " WHERE " + chunkWhere() + " AND permission_type = 'ALLOW'")) {
                setParameters(ps, chunkParams(chunk));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) allowed.add(rs.getString("player_uuid"));
                }
            }
            return allowed;
        });
    }

    @Override
    public List<OfflinePlayer> getFriendPlayers(Chunk chunk) {
        ApiManagerValidateParameter.validateChunk(chunk);
        List<String> uuids = getFriends(chunk);
        if (uuids.contains("*")) {
            return Arrays.asList(Bukkit.getOfflinePlayers());
        }
        return uuids.stream()
                .map(id -> {
                    try { return (OfflinePlayer) Bukkit.getOfflinePlayer(UUID.fromString(id)); }
                    catch (IllegalArgumentException e) { return null; }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public CompletableFuture<List<OfflinePlayer>> getFriendPlayersAsync(Chunk chunk) {
        ApiManagerValidateParameter.validateChunk(chunk);
        return getFriendsAsync(chunk).thenApply(uuids -> {
            if (uuids.contains("*")) {
                return Arrays.asList(Bukkit.getOfflinePlayers());
            }
            return uuids.stream()
                    .map(id -> {
                        try { return (OfflinePlayer) Bukkit.getOfflinePlayer(UUID.fromString(id)); }
                        catch (IllegalArgumentException e) { return null; }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        });
    }

    @Override
    public void addFriend(Chunk chunk, OfflinePlayer friend) {
        ApiManagerValidateParameter.validateChunk(chunk);
        ApiManagerValidateParameter.validatePlayer(friend);
        String friendUuid = friend.getUniqueId().toString();

        if (isTrustAllActive(chunk)) {
            db().update("DELETE FROM " + PERMISSION_TABLE
                            + " WHERE " + chunkWhere() + " AND player_uuid = ?",
                    append(chunkParams(chunk), friendUuid));
        } else {
            db().update("INSERT INTO " + PERMISSION_TABLE
                            + " (server_id, world, x, z, player_uuid, permission_type)"
                            + " VALUES (?, ?, ?, ?, ?, 'ALLOW')"
                            + " ON DUPLICATE KEY UPDATE permission_type = 'ALLOW'",
                    append(chunkParams(chunk), friendUuid));
        }
    }

    @Override
    public CompletableFuture<Void> addFriendAsync(Chunk chunk, OfflinePlayer friend) {
        ApiManagerValidateParameter.validateChunk(chunk);
        ApiManagerValidateParameter.validatePlayer(friend);
        String friendUuid = friend.getUniqueId().toString();

        return isTrustAllActiveAsync(chunk).thenCompose(trustAll -> {
            if (trustAll) {
                return db().updateAsync("DELETE FROM " + PERMISSION_TABLE
                                + " WHERE " + chunkWhere() + " AND player_uuid = ?",
                        append(chunkParams(chunk), friendUuid));
            } else {
                return db().updateAsync("INSERT INTO " + PERMISSION_TABLE
                                + " (server_id, world, x, z, player_uuid, permission_type)"
                                + " VALUES (?, ?, ?, ?, ?, 'ALLOW')"
                                + " ON DUPLICATE KEY UPDATE permission_type = 'ALLOW'",
                        append(chunkParams(chunk), friendUuid));
            }
        });
    }

    @Override
    public void removeFriend(Chunk chunk, OfflinePlayer friend) {
        ApiManagerValidateParameter.validateChunk(chunk);
        ApiManagerValidateParameter.validatePlayer(friend);
        String friendUuid = friend.getUniqueId().toString();

        if (isTrustAllActive(chunk)) {
            db().update("INSERT INTO " + PERMISSION_TABLE
                            + " (server_id, world, x, z, player_uuid, permission_type)"
                            + " VALUES (?, ?, ?, ?, ?, 'DENY')"
                            + " ON DUPLICATE KEY UPDATE permission_type = 'DENY'",
                    append(chunkParams(chunk), friendUuid));
        } else {
            db().update("DELETE FROM " + PERMISSION_TABLE
                            + " WHERE " + chunkWhere() + " AND player_uuid = ? AND permission_type = 'ALLOW'",
                    append(chunkParams(chunk), friendUuid));
        }
    }

    @Override
    public CompletableFuture<Void> removeFriendAsync(Chunk chunk, OfflinePlayer friend) {
        ApiManagerValidateParameter.validateChunk(chunk);
        ApiManagerValidateParameter.validatePlayer(friend);
        String friendUuid = friend.getUniqueId().toString();

        return isTrustAllActiveAsync(chunk).thenCompose(trustAll -> {
            if (trustAll) {
                return db().updateAsync("INSERT INTO " + PERMISSION_TABLE
                                + " (server_id, world, x, z, player_uuid, permission_type)"
                                + " VALUES (?, ?, ?, ?, ?, 'DENY')"
                                + " ON DUPLICATE KEY UPDATE permission_type = 'DENY'",
                        append(chunkParams(chunk), friendUuid));
            } else {
                return db().updateAsync("DELETE FROM " + PERMISSION_TABLE
                                + " WHERE " + chunkWhere() + " AND player_uuid = ? AND permission_type = 'ALLOW'",
                        append(chunkParams(chunk), friendUuid));
            }
        });
    }

    @Override
    public void setAllFriends(Chunk chunk) {
        ApiManagerValidateParameter.validateChunk(chunk);
        repo().updateColumn(TABLE, "trust_all", true, chunkWhere(), chunkParams(chunk));
        db().update("DELETE FROM " + PERMISSION_TABLE + " WHERE " + chunkWhere(),
                chunkParams(chunk));
    }

    @Override
    public CompletableFuture<Void> setAllFriendsAsync(Chunk chunk) {
        ApiManagerValidateParameter.validateChunk(chunk);
        return repo().updateColumnAsync(TABLE, "trust_all", true, chunkWhere(), chunkParams(chunk))
                .thenCompose(v -> db().updateAsync(
                        "DELETE FROM " + PERMISSION_TABLE + " WHERE " + chunkWhere(),
                        chunkParams(chunk)));
    }

    @Override
    public void clearFriends(Chunk chunk) {
        ApiManagerValidateParameter.validateChunk(chunk);
        repo().updateColumn(TABLE, "trust_all", false, chunkWhere(), chunkParams(chunk));
        db().update("DELETE FROM " + PERMISSION_TABLE + " WHERE " + chunkWhere(),
                chunkParams(chunk));
    }

    @Override
    public CompletableFuture<Void> clearFriendsAsync(Chunk chunk) {
        ApiManagerValidateParameter.validateChunk(chunk);
        return repo().updateColumnAsync(TABLE, "trust_all", false, chunkWhere(), chunkParams(chunk))
                .thenCompose(v -> db().updateAsync(
                        "DELETE FROM " + PERMISSION_TABLE + " WHERE " + chunkWhere(),
                        chunkParams(chunk)));
    }

    @Override
    public boolean isFriend(Chunk chunk, OfflinePlayer friend) {
        ApiManagerValidateParameter.validateChunk(chunk);
        ApiManagerValidateParameter.validatePlayer(friend);
        String friendUuid = friend.getUniqueId().toString();

        return Boolean.TRUE.equals(db().query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT permission_type FROM " + PERMISSION_TABLE
                            + " WHERE " + chunkWhere() + " AND player_uuid = ?")) {
                setParameters(ps, append(chunkParams(chunk), friendUuid));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return "ALLOW".equals(rs.getString("permission_type"));
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT trust_all FROM " + TABLE + " WHERE " + chunkWhere())) {
                setParameters(ps, chunkParams(chunk));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getBoolean("trust_all");
                }
            }
            return false;
        }));
    }

    @Override
    public CompletableFuture<Boolean> isFriendAsync(Chunk chunk, OfflinePlayer friend) {
        ApiManagerValidateParameter.validateChunk(chunk);
        ApiManagerValidateParameter.validatePlayer(friend);
        String friendUuid = friend.getUniqueId().toString();

        return db().queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT permission_type FROM " + PERMISSION_TABLE
                            + " WHERE " + chunkWhere() + " AND player_uuid = ?")) {
                setParameters(ps, append(chunkParams(chunk), friendUuid));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return "ALLOW".equals(rs.getString("permission_type"));
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT trust_all FROM " + TABLE + " WHERE " + chunkWhere())) {
                setParameters(ps, chunkParams(chunk));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getBoolean("trust_all");
                }
            }
            return false;
        });
    }

    @Override
    public UUID getChunkOwner(Chunk chunk) {
        ApiManagerValidateParameter.validateChunk(chunk);
        return db().query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT owner_uuid FROM " + TABLE + " WHERE " + chunkWhere())) {
                setParameters(ps, chunkParams(chunk));
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return null;
                    String u = rs.getString("owner_uuid");
                    return (u == null || u.isEmpty()) ? null : UUID.fromString(u);
                }
            }
        });
    }

    @Override
    public CompletableFuture<UUID> getChunkOwnerAsync(Chunk chunk) {
        ApiManagerValidateParameter.validateChunk(chunk);
        return db().queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT owner_uuid FROM " + TABLE + " WHERE " + chunkWhere())) {
                setParameters(ps, chunkParams(chunk));
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return null;
                    String u = rs.getString("owner_uuid");
                    return (u == null || u.isEmpty()) ? null : UUID.fromString(u);
                }
            }
        });
    }

    @Override
    public List<Chunk> getChunks(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return db().query(conn -> {
            List<Chunk> chunks = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT world, x, z FROM " + TABLE + " WHERE owner_uuid = ? AND server_id = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                ps.setString(2, ApiManager.getServerId());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        World world = Bukkit.getWorld(rs.getString("world"));
                        if (world != null) chunks.add(world.getChunkAt(rs.getInt("x"), rs.getInt("z")));
                    }
                }
            }
            return chunks;
        });
    }

    @Override
    public CompletableFuture<List<Chunk>> getChunksAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return db().queryAsync(conn -> {
            List<String[]> data = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT world, x, z FROM " + TABLE + " WHERE owner_uuid = ? AND server_id = ?")) {
                ps.setString(1, player.getUniqueId().toString());
                ps.setString(2, ApiManager.getServerId());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        data.add(new String[]{
                                rs.getString("world"),
                                String.valueOf(rs.getInt("x")),
                                String.valueOf(rs.getInt("z"))
                        });
                    }
                }
            }
            return data;
        }).thenApply(data -> {
            List<Chunk> chunks = new ArrayList<>();
            for (String[] entry : data) {
                World world = Bukkit.getWorld(entry[0]);
                if (world != null) chunks.add(world.getChunkAt(Integer.parseInt(entry[1]), Integer.parseInt(entry[2])));
            }
            return chunks;
        });
    }
}