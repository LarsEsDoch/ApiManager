package de.lars.apimanager.apis.chunkAPI;

import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IChunkAPI {
    Timestamp getCreated(Chunk chunk);

    CompletableFuture<Timestamp> getCreatedAsync(Chunk chunk);

    Timestamp getUpdated(Chunk chunk);

    CompletableFuture<Timestamp> getUpdatedAsync(Chunk chunk);

    Timestamp getClaimed(Chunk chunk);

    CompletableFuture<Timestamp> getClaimedAsync(Chunk chunk);

    void claimChunk(OfflinePlayer player, Chunk chunk);

    CompletableFuture<Void> claimChunkAsync(OfflinePlayer player, Chunk chunk);

    void unclaimChunk(OfflinePlayer player, Chunk chunk);

    CompletableFuture<Void> unclaimChunkAsync(OfflinePlayer player, Chunk chunk);

    void setFlags(OfflinePlayer player, Chunk chunk, String flagsJson);

    CompletableFuture<Void> setFlagsAsync(OfflinePlayer player, Chunk chunk, String flagsJson);

    List<OfflinePlayer> getFriendPlayers(Chunk chunk);

    CompletableFuture<List<OfflinePlayer>> getFriendPlayersAsync(Chunk chunk);

    List<String> getFriends(Chunk chunk);

    CompletableFuture<List<String>> getFriendsAsync(Chunk chunk);

    void addFriend(OfflinePlayer owner, Chunk chunk, OfflinePlayer friend);

    CompletableFuture<Void> addFriendAsync(OfflinePlayer owner, Chunk chunk, OfflinePlayer friend);

    void removeFriend(OfflinePlayer owner, Chunk chunk, OfflinePlayer friend);

    CompletableFuture<Void> removeFriendAsync(OfflinePlayer owner, Chunk chunk, OfflinePlayer friend);

    boolean isFriend(Chunk chunk, OfflinePlayer friend);

    CompletableFuture<Boolean> isFriendAsync(Chunk chunk, OfflinePlayer friend);

    UUID getChunkOwner(Chunk chunk);

    CompletableFuture<UUID> getChunkOwnerAsync(Chunk chunk);

    List<String> getChunks(OfflinePlayer player);

    CompletableFuture<List<String>> getChunksAsync(OfflinePlayer player);
}