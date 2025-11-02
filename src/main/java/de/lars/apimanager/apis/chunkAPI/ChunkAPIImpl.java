package de.lars.apimanager.apis.chunkAPI;

import de.lars.apimanager.ApiManager;
import de.lars.apimanager.database.IDatabaseManager;
import de.lars.apimanager.utils.ValidateParameter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ChunkAPIImpl implements IChunkAPI {
    private IDatabaseManager db() {
        return ApiManager.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db().update("""
            CREATE TABLE IF NOT EXISTS claimed_chunks (
                uuid CHAR(36) NOT NULL,
                world VARCHAR(64) NOT NULL,
                x INT NOT NULL,
                z INT NOT NULL,
                friends VARCHAR(1024) DEFAULT '',
                flags JSON DEFAULT (JSON_OBJECT()),
                claimed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                PRIMARY KEY (world, x, z),
                INDEX idx_owner (uuid),
                FOREIGN KEY (uuid) REFERENCES players(uuid) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);
    }

    @Override
    public Instant getCreated(Chunk chunk) {
        ValidateParameter.validateChunk(chunk);
        return db().query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT created_at FROM claimed_chunks WHERE world = ? AND x = ? AND z = ? LIMIT 1")) {
                ps.setString(1, chunk.getWorld().getName());
                ps.setInt(2, chunk.getX());
                ps.setInt(3, chunk.getZ());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Timestamp ts = rs.getTimestamp("created_at");
                        if (ts != null) {
                            return ts.toInstant();
                        } else {
                            return null;
                        }
                    }
                    return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Instant> getCreatedAsync(Chunk chunk) {
        ValidateParameter.validateChunk(chunk);
        return db().queryAsync(conn -> {
                try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT created_at FROM claimed_chunks WHERE world = ? AND x = ? AND z = ? LIMIT 1")) {
                ps.setString(1, chunk.getWorld().getName());
                ps.setInt(2, chunk.getX());
                ps.setInt(3, chunk.getZ());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Timestamp ts = rs.getTimestamp("created_at");
                        if (ts != null) {
                            return ts.toInstant();
                        } else {
                            return null;
                        }
                    }
                    return null;
                }
            }
        });
    }

    @Override
    public Instant getUpdated(Chunk chunk) {
        ValidateParameter.validateChunk(chunk);
        return db().query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT updated_at FROM claimed_chunks WHERE world = ? AND x = ? AND z = ? LIMIT 1")) {
                ps.setString(1, chunk.getWorld().getName());
                ps.setInt(2, chunk.getX());
                ps.setInt(3, chunk.getZ());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Timestamp ts = rs.getTimestamp("updated_at");
                        if (ts != null) {
                            return ts.toInstant();
                        } else {
                            return null;
                        }
                    }
                    return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Instant> getUpdatedAsync(Chunk chunk) {
        ValidateParameter.validateChunk(chunk);
        return db().queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT updated_at FROM claimed_chunks WHERE world = ? AND x = ? AND z = ? LIMIT 1")) {
                ps.setString(1, chunk.getWorld().getName());
                ps.setInt(2, chunk.getX());
                ps.setInt(3, chunk.getZ());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Timestamp ts = rs.getTimestamp("updated_at");
                        if (ts != null) {
                            return ts.toInstant();
                        } else {
                            return null;
                        }
                    }
                    return null;
                }
            }
        });
    }

    @Override
    public Instant getClaimed(Chunk chunk) {
        ValidateParameter.validateChunk(chunk);
        return db().query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT claimed_at FROM claimed_chunks WHERE world = ? AND x = ? AND z = ? LIMIT 1")) {
                ps.setString(1, chunk.getWorld().getName());
                ps.setInt(2, chunk.getX());
                ps.setInt(3, chunk.getZ());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("claimed_at").toInstant();
                    return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Instant> getClaimedAsync(Chunk chunk) {
        ValidateParameter.validateChunk(chunk);
        return db().queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT claimed_at FROM claimed_chunks WHERE world = ? AND x = ? AND z = ? LIMIT 1")) {
                ps.setString(1, chunk.getWorld().getName());
                ps.setInt(2, chunk.getX());
                ps.setInt(3, chunk.getZ());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("claimed_at").toInstant();
                    return null;
                }
            }
        });
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
            INSERT INTO claimed_chunks (uuid, world, x, z, claimed_at)
            VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)
            ON DUPLICATE KEY UPDATE
                uuid = VALUES(uuid),
                claimed_at = CURRENT_TIMESTAMP,
                updated_at = CURRENT_TIMESTAMP
        """, uuid, world, x, z);

        ApiManager.getInstance().getLimitAPI().decreaseChunkLimit(player, 1);
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
            INSERT INTO claimed_chunks (uuid, world, x, z, claimed_at)
            VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)
            ON DUPLICATE KEY UPDATE
                uuid = VALUES(uuid),
                claimed_at = CURRENT_TIMESTAMP,
                updated_at = CURRENT_TIMESTAMP
        """, uuid, world, x, z).thenCompose(v -> ApiManager.getInstance().getLimitAPI().decreaseChunkLimitAsync(player, 1));
    }

    @Override
    public void unclaimChunk(OfflinePlayer player, Chunk chunk) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateChunk(chunk);
        String world = chunk.getWorld().getName();
        int x = chunk.getX();
        int z = chunk.getZ();

        db().update("""
            DELETE FROM claimed_chunks
            WHERE world = ? AND x = ? AND z = ?
        """, world, x, z);

        ApiManager.getInstance().getLimitAPI().increaseChunkLimit(player, 1);
    }

    @Override
    public CompletableFuture<Void> unclaimChunkAsync(OfflinePlayer player, Chunk chunk) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateChunk(chunk);
        String world = chunk.getWorld().getName();
        int x = chunk.getX();
        int z = chunk.getZ();

        return db().updateAsync("""
            DELETE FROM claimed_chunks
            WHERE world = ? AND x = ? AND z = ?
        """, world, x, z).thenCompose(v -> {
            try {
                return ApiManager.getInstance().getLimitAPI().increaseChunkLimitAsync(player, 1);
            } catch (Exception ex) {
                return CompletableFuture.completedFuture(null);
            }
        });
    }

    @Override
    public void setFlags(Chunk chunk, String flagsJson) {
        ValidateParameter.validateChunk(chunk);
        String world = chunk.getWorld().getName();
        int x = chunk.getX();
        int z = chunk.getZ();

        db().update("""
                    UPDATE claimed_chunks
                    SET flags = ?
                    WHERE world = ? AND x = ? AND z = ?
                """, flagsJson == null ? "{}" : flagsJson, world, x, z);
    }

    @Override
    public CompletableFuture<Void> setFlagsAsync(Chunk chunk, String flagsJson) {
        ValidateParameter.validateChunk(chunk);
        String world = chunk.getWorld().getName();
        int x = chunk.getX();
        int z = chunk.getZ();

        return db().updateAsync("""
                    UPDATE claimed_chunks
                    SET flags = ?
                    WHERE world = ? AND x = ? AND z = ?
                """, flagsJson == null ? "{}" : flagsJson, world, x, z);
    }

    @Override
    public String getFlags(Chunk chunk) {
        ValidateParameter.validateChunk(chunk);
        String world = chunk.getWorld().getName();
        int x = chunk.getX();
        int z = chunk.getZ();

        return db().query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT flags FROM claimed_chunks WHERE world = ? AND x = ? AND z = ? LIMIT 1")) {
                ps.setString(1, world);
                ps.setInt(2, x);
                ps.setInt(3, z);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString("flags");
                    return "{}";
                }
            }
        });
    }

    @Override
    public CompletableFuture<String> getFlagsAsync(Chunk chunk) {
        ValidateParameter.validateChunk(chunk);
        String world = chunk.getWorld().getName();
        int x = chunk.getX();
        int z = chunk.getZ();

        return db().queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT flags FROM claimed_chunks WHERE world = ? AND x = ? AND z = ? LIMIT 1")) {
                ps.setString(1, world);
                ps.setInt(2, x);
                ps.setInt(3, z);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString("flags");
                    return "{}";
                }
            }
        });
    }

    @Override
    public List<String> getFriends(Chunk chunk) {
        ValidateParameter.validateChunk(chunk);
        String world = chunk.getWorld().getName();
        int x = chunk.getX();
        int z = chunk.getZ();

        return db().query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT friends FROM claimed_chunks WHERE world = ? AND x = ? AND z = ?")) {
                ps.setString(1, world);
                ps.setInt(2, x);
                ps.setInt(3, z);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String s = rs.getString("friends");
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
        String world = chunk.getWorld().getName();
        int x = chunk.getX();
        int z = chunk.getZ();

        return db().queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT friends FROM claimed_chunks WHERE world = ? AND x = ? AND z = ?")) {
                ps.setString(1, world);
                ps.setInt(2, x);
                ps.setInt(3, z);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String s = rs.getString("friends");
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
        List<String> friends = getFriends(chunk);
        if (friends.contains("*")) {
            return Arrays.asList(Bukkit.getOfflinePlayers());
        }
        return friends.stream()
                .map(uuid -> {
                    try {
                        return Bukkit.getOfflinePlayer(UUID.fromString(uuid));
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
        return getFriendsAsync(chunk).thenApply(uuids -> {
            if (uuids.contains("*")) {
                return Arrays.asList(Bukkit.getOfflinePlayers());
            }
            return uuids.stream()
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

        List<String> friends = new ArrayList<>(getFriends(chunk));
        if (friends.contains("*")) {
            return;
        }
        if (!friends.contains(friendUuid)) {
            friends.add(friendUuid);
            String joined = String.join(",", friends);
            db().update("UPDATE claimed_chunks SET friends = ?, updated_at = CURRENT_TIMESTAMP WHERE world = ? AND x = ? AND z = ?",
                    joined, chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
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
            return db().updateAsync("UPDATE claimed_chunks SET friends = ?, updated_at = CURRENT_TIMESTAMP WHERE world = ? AND x = ? AND z = ?",
                    joined, chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
        });
    }

    @Override
    public void removeFriend(Chunk chunk, OfflinePlayer friend) {
        ValidateParameter.validateChunk(chunk);
        ValidateParameter.validatePlayer(friend);
        String friendUuid = friend.getUniqueId().toString();

        List<String> friends = new ArrayList<>(getFriends(chunk));
        if (friends.contains("*")) {
            List<String> explicit = Arrays.stream(Bukkit.getOfflinePlayers())
                    .map(p -> p.getUniqueId().toString())
                    .filter(id -> !id.equals(friendUuid))
                    .collect(Collectors.toList());
            String joined = String.join(",", explicit);
            db().update("UPDATE claimed_chunks SET friends = ?, updated_at = CURRENT_TIMESTAMP WHERE world = ? AND x = ? AND z = ?",
                    joined, chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
            return;
        }

        if (friends.remove(friendUuid)) {
            String joined = String.join(",", friends);
            db().update("UPDATE claimed_chunks SET friends = ?, updated_at = CURRENT_TIMESTAMP WHERE world = ? AND x = ? AND z = ?",
                    joined, chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
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
                return db().updateAsync("UPDATE claimed_chunks SET friends = ?, updated_at = CURRENT_TIMESTAMP WHERE world = ? AND x = ? AND z = ?",
                        joined, chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
            }
            newList.remove(friendUuid);
            String joined = String.join(",", newList);
            return db().updateAsync("UPDATE claimed_chunks SET friends = ?, updated_at = CURRENT_TIMESTAMP WHERE world = ? AND x = ? AND z = ?",
                    joined, chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
        });
    }

    @Override
    public void setAllFriends(Chunk chunk) {
        ValidateParameter.validateChunk(chunk);
        db().update("UPDATE claimed_chunks SET friends = NULL, updated_at = CURRENT_TIMESTAMP WHERE world = ? AND x = ? AND z = ?",
                chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    @Override
    public CompletableFuture<Void> setAllFriendsAsync(Chunk chunk) {
        ValidateParameter.validateChunk(chunk);
        return db().updateAsync("UPDATE claimed_chunks SET friends = NULL, updated_at = CURRENT_TIMESTAMP WHERE world = ? AND x = ? AND z = ?",
                chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    @Override
    public void clearFriends(Chunk chunk) {
        ValidateParameter.validateChunk(chunk);
        db().update("UPDATE claimed_chunks SET friends = '', updated_at = CURRENT_TIMESTAMP WHERE world = ? AND x = ? AND z = ?",
                chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    @Override
    public CompletableFuture<Void> clearFriendsAsync(Chunk chunk) {
        ValidateParameter.validateChunk(chunk);
        return db().updateAsync("UPDATE claimed_chunks SET friends = '', updated_at = CURRENT_TIMESTAMP WHERE world = ? AND x = ? AND z = ?",
                chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    @Override
    public boolean isFriend(Chunk chunk, OfflinePlayer friend) {
        ValidateParameter.validateChunk(chunk);
        ValidateParameter.validatePlayer(friend);
        String friendUuid = friend.getUniqueId().toString();
        List<String> friends = getFriends(chunk);
        return friends.contains("*") || friends.contains(friendUuid);
    }

    @Override
    public CompletableFuture<Boolean> isFriendAsync(Chunk chunk, OfflinePlayer friend) {
        ValidateParameter.validateChunk(chunk);
        ValidateParameter.validatePlayer(friend);
        String friendUuid = friend.getUniqueId().toString();
        return getFriendsAsync(chunk).thenApply(list -> list.contains("*") || list.contains(friendUuid));
    }

    @Override
    public UUID getChunkOwner(Chunk chunk) {
        ValidateParameter.validateChunk(chunk);
        String world = chunk.getWorld().getName();
        int x = chunk.getX();
        int z = chunk.getZ();

        return db().query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT uuid FROM claimed_chunks WHERE world = ? AND x = ? AND z = ?")) {
                ps.setString(1, world);
                ps.setInt(2, x);
                ps.setInt(3, z);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String u = rs.getString("uuid");
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
        String world = chunk.getWorld().getName();
        int x = chunk.getX();
        int z = chunk.getZ();

        return db().queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT uuid FROM claimed_chunks WHERE world = ? AND x = ? AND z = ?")) {
                ps.setString(1, world);
                ps.setInt(2, x);
                ps.setInt(3, z);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String u = rs.getString("uuid");
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
            try (PreparedStatement ps = conn.prepareStatement("SELECT world, x, z FROM claimed_chunks WHERE uuid = ?")) {
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
            try (PreparedStatement ps = conn.prepareStatement("SELECT world, x, z FROM claimed_chunks WHERE uuid = ?")) {
                ps.setString(1, uuid);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        data.add(new String[]{rs.getString("world"), String.valueOf(rs.getInt("x")), String.valueOf(rs.getInt("z"))});
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