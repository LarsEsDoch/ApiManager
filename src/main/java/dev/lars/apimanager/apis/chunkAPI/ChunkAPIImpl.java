package dev.lars.apimanager.apis.chunkAPI;

import dev.lars.apimanager.ApiManager;
import dev.lars.apimanager.database.DatabaseRepository;
import dev.lars.apimanager.database.IDatabaseManager;
import dev.lars.apimanager.utils.ValidateParameter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ChunkAPIImpl implements IChunkAPI {
    private static final String TABLE = "claimed_chunks";

    private DatabaseRepository repo() {
        return new DatabaseRepository();
    }

    private IDatabaseManager db() {
        return ApiManager.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db().update("""
            CREATE TABLE IF NOT EXISTS claimed_chunks (
                owner_uuid CHAR(36) NOT NULL,
                world VARCHAR(64) NOT NULL,
                x INT NOT NULL,
                z INT NOT NULL,
                friend_uuids VARCHAR(1024) DEFAULT '',
                flags JSON DEFAULT (JSON_OBJECT()),
                claimed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                PRIMARY KEY (world, x, z),
                INDEX idx_owner (owner_uuid),
                FOREIGN KEY (owner_uuid) REFERENCES players(uuid) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);
    }

    // Helper method to build WHERE clause for chunk location
    private String chunkWhere() {
        return "world = ? AND x = ? AND z = ?";
    }

    private Object[] chunkParams(Chunk chunk) {
        return new Object[]{chunk.getWorld().getName(), chunk.getX(), chunk.getZ()};
    }

    @Override
    public Instant getCreated(Chunk chunk) {
        ValidateParameter.validateChunk(chunk);
        return repo().getInstant(TABLE, "created_at", chunkWhere(), chunkParams(chunk));
    }

    @Override
    public CompletableFuture<Instant> getCreatedAsync(Chunk chunk) {
        ValidateParameter.validateChunk(chunk);
        return repo().getInstantAsync(TABLE, "created_at", chunkWhere(), chunkParams(chunk));
    }

    @Override
    public Instant getUpdated(Chunk chunk) {
        ValidateParameter.validateChunk(chunk);
        return repo().getInstant(TABLE, "updated_at", chunkWhere(), chunkParams(chunk));
    }

    @Override
    public CompletableFuture<Instant> getUpdatedAsync(Chunk chunk) {
        ValidateParameter.validateChunk(chunk);
        return repo().getInstantAsync(TABLE, "updated_at", chunkWhere(), chunkParams(chunk));
    }

    @Override
    public Instant getClaimed(Chunk chunk) {
        ValidateParameter.validateChunk(chunk);
        return repo().getInstant(TABLE, "claimed_at", chunkWhere(), chunkParams(chunk));
    }

    @Override
    public CompletableFuture<Instant> getClaimedAsync(Chunk chunk) {
        ValidateParameter.validateChunk(chunk);
        return repo().getInstantAsync(TABLE, "claimed_at", chunkWhere(), chunkParams(chunk));
    }

    @Override
    public void claimChunk(OfflinePlayer player, Chunk chunk) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateChunk(chunk);
        String world = chunk.getWorld().getName();
        int x = chunk.getX();
        int z = chunk.getZ();
        String uuid = player.getUniqueId().toString();

        db().update("""
            INSERT INTO claimed_chunks (owner_uuid, world, x, z, claimed_at)
            VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)
            ON DUPLICATE KEY UPDATE
                owner_uuid = VALUES(owner_uuid),
                claimed_at = CURRENT_TIMESTAMP,
                updated_at = CURRENT_TIMESTAMP
        """, uuid, world, x, z);

        ApiManager.getInstance().getLimitAPI().decreaseMaxChunks(player, 1);
    }

    @Override
    public CompletableFuture<Void> claimChunkAsync(OfflinePlayer player, Chunk chunk) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateChunk(chunk);
        String world = chunk.getWorld().getName();
        int x = chunk.getX();
        int z = chunk.getZ();
        String uuid = player.getUniqueId().toString();

        return db().updateAsync("""
            INSERT INTO claimed_chunks (owner_uuid, world, x, z, claimed_at)
            VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)
            ON DUPLICATE KEY UPDATE
                owner_uuid = VALUES(owner_uuid),
                claimed_at = CURRENT_TIMESTAMP,
                updated_at = CURRENT_TIMESTAMP
        """, uuid, world, x, z).thenCompose(v ->
            ApiManager.getInstance().getLimitAPI().decreaseMaxChunksAsync(player, 1));
    }

    @Override
    public void unclaimChunk(OfflinePlayer player, Chunk chunk) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateChunk(chunk);

        repo().delete(TABLE, chunkWhere(), chunkParams(chunk));
        ApiManager.getInstance().getLimitAPI().increaseMaxChunks(player, 1);
    }

    @Override
    public CompletableFuture<Void> unclaimChunkAsync(OfflinePlayer player, Chunk chunk) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateChunk(chunk);

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
        ValidateParameter.validateChunk(chunk);
        repo().updateColumn(TABLE, "flags", flagsJson == null ? "{}" : flagsJson,
            chunkWhere(), chunkParams(chunk));
    }

    @Override
    public CompletableFuture<Void> setFlagsAsync(Chunk chunk, String flagsJson) {
        ValidateParameter.validateChunk(chunk);
        return repo().updateColumnAsync(TABLE, "flags", flagsJson == null ? "{}" : flagsJson,
            chunkWhere(), chunkParams(chunk));
    }

    @Override
    public String getFlags(Chunk chunk) {
        ValidateParameter.validateChunk(chunk);
        String flags = repo().getString(TABLE, "flags", chunkWhere(), chunkParams(chunk));
        return flags != null ? flags : "{}";
    }

    @Override
    public CompletableFuture<String> getFlagsAsync(Chunk chunk) {
        ValidateParameter.validateChunk(chunk);
        return repo().getStringAsync(TABLE, "flags", chunkWhere(), chunkParams(chunk))
            .thenApply(flags -> flags != null ? flags : "{}");
    }

    @Override
    public List<String> getFriends(Chunk chunk) {
        ValidateParameter.validateChunk(chunk);
        return db().query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT friend_uuids FROM claimed_chunks WHERE world = ? AND x = ? AND z = ?")) {
                ps.setString(1, chunk.getWorld().getName());
                ps.setInt(2, chunk.getX());
                ps.setInt(3, chunk.getZ());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String s = rs.getString("friend_uuids");
                        if (s == null) {
                            return Collections.singletonList("*");
                        }
                        if (s.isEmpty()) return new ArrayList<>();
                        return Arrays.stream(s.split(","))
                                .map(String::trim)
                                .filter(tok -> !tok.isBlank())
                                .collect(Collectors.toList());
                    }
                    return new ArrayList<>();
                }
            }
        });
    }

    @Override
    public CompletableFuture<List<String>> getFriendsAsync(Chunk chunk) {
        ValidateParameter.validateChunk(chunk);
        return db().queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT friend_uuids FROM claimed_chunks WHERE world = ? AND x = ? AND z = ?")) {
                ps.setString(1, chunk.getWorld().getName());
                ps.setInt(2, chunk.getX());
                ps.setInt(3, chunk.getZ());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String s = rs.getString("friend_uuids");
                        if (s == null) {
                            return Collections.singletonList("*");
                        }
                        if (s.isEmpty()) return new ArrayList<>();
                        return Arrays.stream(s.split(","))
                                .map(String::trim)
                                .filter(tok -> !tok.isBlank())
                                .collect(Collectors.toList());
                    }
                    return new ArrayList<>();
                }
            }
        });
    }

    @Override
    public List<OfflinePlayer> getFriendPlayers(Chunk chunk) {
        ValidateParameter.validateChunk(chunk);
        List<String> friend_uuids = getFriends(chunk);
        if (friend_uuids.contains("*")) {
            return Arrays.asList(Bukkit.getOfflinePlayers());
        }
        return friend_uuids.stream()
                .map(owner_uuid -> {
                    try {
                        return Bukkit.getOfflinePlayer(UUID.fromString(owner_uuid));
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public CompletableFuture<List<OfflinePlayer>> getFriendPlayersAsync(Chunk chunk) {
        ValidateParameter.validateChunk(chunk);
        return getFriendsAsync(chunk).thenApply(owner_uuids -> {
            if (owner_uuids.contains("*")) {
                return Arrays.asList(Bukkit.getOfflinePlayers());
            }
            return owner_uuids.stream()
                    .map(id -> {
                        try {
                            return Bukkit.getOfflinePlayer(UUID.fromString(id));
                        } catch (IllegalArgumentException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        });
    }

    @Override
    public void addFriend(Chunk chunk, OfflinePlayer friend) {
        ValidateParameter.validateChunk(chunk);
        ValidateParameter.validatePlayer(friend);
        String friendUuid = friend.getUniqueId().toString();

        List<String> friend_uuids = new ArrayList<>(getFriends(chunk));
        if (friend_uuids.contains("*")) {
            return;
        }
        if (!friend_uuids.contains(friendUuid)) {
            friend_uuids.add(friendUuid);
            String joined = String.join(",", friend_uuids);
            repo().updateColumn(TABLE, "friend_uuids", joined, chunkWhere(), chunkParams(chunk));
        }
    }

    @Override
    public CompletableFuture<Void> addFriendAsync(Chunk chunk, OfflinePlayer friend) {
        ValidateParameter.validateChunk(chunk);
        ValidateParameter.validatePlayer(friend);
        String friendUuid = friend.getUniqueId().toString();

        return getFriendsAsync(chunk).thenCompose(list -> {
            List<String> newList = new ArrayList<>(list);
            if (newList.contains("*")) {
                return CompletableFuture.completedFuture(null);
            }
            if (!newList.contains(friendUuid)) newList.add(friendUuid);
            String joined = String.join(",", newList);
            return repo().updateColumnAsync(TABLE, "friend_uuids", joined, chunkWhere(), chunkParams(chunk));
        });
    }

    @Override
    public void removeFriend(Chunk chunk, OfflinePlayer friend) {
        ValidateParameter.validateChunk(chunk);
        ValidateParameter.validatePlayer(friend);
        String friendUuid = friend.getUniqueId().toString();

        List<String> friend_uuids = new ArrayList<>(getFriends(chunk));
        if (friend_uuids.contains("*")) {
            List<String> explicit = Arrays.stream(Bukkit.getOfflinePlayers())
                    .map(p -> p.getUniqueId().toString())
                    .filter(id -> !id.equals(friendUuid))
                    .collect(Collectors.toList());
            String joined = String.join(",", explicit);
            repo().updateColumn(TABLE, "friend_uuids", joined, chunkWhere(), chunkParams(chunk));
            return;
        }

        if (friend_uuids.remove(friendUuid)) {
            String joined = String.join(",", friend_uuids);
            repo().updateColumn(TABLE, "friend_uuids", joined, chunkWhere(), chunkParams(chunk));
        }
    }

    @Override
    public CompletableFuture<Void> removeFriendAsync(Chunk chunk, OfflinePlayer friend) {
        ValidateParameter.validateChunk(chunk);
        ValidateParameter.validatePlayer(friend);
        String friendUuid = friend.getUniqueId().toString();

        return getFriendsAsync(chunk).thenCompose(list -> {
            List<String> newList = new ArrayList<>(list);
            if (newList.contains("*")) {
                List<String> explicit = Arrays.stream(Bukkit.getOfflinePlayers())
                        .map(p -> p.getUniqueId().toString())
                        .filter(id -> !id.equals(friendUuid))
                        .collect(Collectors.toList());
                String joined = String.join(",", explicit);
                return repo().updateColumnAsync(TABLE, "friend_uuids", joined, chunkWhere(), chunkParams(chunk));
            }
            newList.remove(friendUuid);
            String joined = String.join(",", newList);
            return repo().updateColumnAsync(TABLE, "friend_uuids", joined, chunkWhere(), chunkParams(chunk));
        });
    }

    @Override
    public void setAllFriends(Chunk chunk) {
        ValidateParameter.validateChunk(chunk);
        repo().updateColumn(TABLE, "friend_uuids", null, chunkWhere(), chunkParams(chunk));
    }

    @Override
    public CompletableFuture<Void> setAllFriendsAsync(Chunk chunk) {
        ValidateParameter.validateChunk(chunk);
        return repo().updateColumnAsync(TABLE, "friend_uuids", null, chunkWhere(), chunkParams(chunk));
    }

    @Override
    public void clearFriends(Chunk chunk) {
        ValidateParameter.validateChunk(chunk);
        repo().updateColumn(TABLE, "friend_uuids", "", chunkWhere(), chunkParams(chunk));
    }

    @Override
    public CompletableFuture<Void> clearFriendsAsync(Chunk chunk) {
        ValidateParameter.validateChunk(chunk);
        return repo().updateColumnAsync(TABLE, "friend_uuids", "", chunkWhere(), chunkParams(chunk));
    }

    @Override
    public boolean isFriend(Chunk chunk, OfflinePlayer friend) {
        ValidateParameter.validateChunk(chunk);
        ValidateParameter.validatePlayer(friend);
        String friendUuid = friend.getUniqueId().toString();
        List<String> friend_uuids = getFriends(chunk);
        return friend_uuids.contains("*") || friend_uuids.contains(friendUuid);
    }

    @Override
    public CompletableFuture<Boolean> isFriendAsync(Chunk chunk, OfflinePlayer friend) {
        ValidateParameter.validateChunk(chunk);
        ValidateParameter.validatePlayer(friend);
        String friendUuid = friend.getUniqueId().toString();
        return getFriendsAsync(chunk).thenApply(list ->
            list.contains("*") || list.contains(friendUuid));
    }

    @Override
    public UUID getChunkOwner(Chunk chunk) {
        ValidateParameter.validateChunk(chunk);
        return db().query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT owner_uuid FROM claimed_chunks WHERE world = ? AND x = ? AND z = ?")) {
                ps.setString(1, chunk.getWorld().getName());
                ps.setInt(2, chunk.getX());
                ps.setInt(3, chunk.getZ());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String u = rs.getString("owner_uuid");
                        if (u == null || u.isEmpty()) return null;
                        return UUID.fromString(u);
                    }
                    return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<UUID> getChunkOwnerAsync(Chunk chunk) {
        ValidateParameter.validateChunk(chunk);
        return db().queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT owner_uuid FROM claimed_chunks WHERE world = ? AND x = ? AND z = ?")) {
                ps.setString(1, chunk.getWorld().getName());
                ps.setInt(2, chunk.getX());
                ps.setInt(3, chunk.getZ());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String u = rs.getString("owner_uuid");
                        if (u == null || u.isEmpty()) return null;
                        return UUID.fromString(u);
                    }
                    return null;
                }
            }
        });
    }

    @Override
    public List<Chunk> getChunks(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        String uuid = player.getUniqueId().toString();

        return db().query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT world, x, z FROM claimed_chunks WHERE owner_uuid = ?")) {
                ps.setString(1, uuid);
                try (ResultSet rs = ps.executeQuery()) {
                    List<Chunk> chunks = new ArrayList<>();
                    while (rs.next()) {
                        String worldName = rs.getString("world");
                        int x = rs.getInt("x");
                        int z = rs.getInt("z");

                        World world = Bukkit.getWorld(worldName);
                        if (world != null) {
                            chunks.add(world.getChunkAt(x, z));
                        }
                    }
                    return chunks;
                }
            }
        });
    }

    @Override
    public CompletableFuture<List<Chunk>> getChunksAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        String uuid = player.getUniqueId().toString();

        return db().queryAsync(conn -> {
            List<String[]> data = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT world, x, z FROM claimed_chunks WHERE owner_uuid = ?")) {
                ps.setString(1, uuid);
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
                if (world != null) {
                    int x = Integer.parseInt(entry[1]);
                    int z = Integer.parseInt(entry[2]);
                    chunks.add(world.getChunkAt(x, z));
                }
            }
            return chunks;
        });
    }
}