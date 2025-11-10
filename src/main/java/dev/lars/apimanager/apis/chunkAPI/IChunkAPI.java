package dev.lars.apimanager.apis.chunkAPI;

import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IChunkAPI {
    Instant getCreated(Chunk chunk);

    CompletableFuture<Instant> getCreatedAsync(Chunk chunk);

    Instant getUpdated(Chunk chunk);

    CompletableFuture<Instant> getUpdatedAsync(Chunk chunk);

    Instant getClaimed(Chunk chunk);

    CompletableFuture<Instant> getClaimedAsync(Chunk chunk);

    void claimChunk(OfflinePlayer player, Chunk chunk);

    CompletableFuture<Void> claimChunkAsync(OfflinePlayer player, Chunk chunk);

    void unclaimChunk(OfflinePlayer player, Chunk chunk);

    CompletableFuture<Void> unclaimChunkAsync(OfflinePlayer player, Chunk chunk);

    void setFlags(Chunk chunk, String flagsJson);

    CompletableFuture<Void> setFlagsAsync(Chunk chunk, String flagsJson);

    String getFlags(Chunk chunk);

    CompletableFuture<String> getFlagsAsync(Chunk chunk);

    List<OfflinePlayer> getFriendPlayers(Chunk chunk);

    CompletableFuture<List<OfflinePlayer>> getFriendPlayersAsync(Chunk chunk);

    List<String> getFriends(Chunk chunk);

    CompletableFuture<List<String>> getFriendsAsync(Chunk chunk);

    void addFriend(Chunk chunk, OfflinePlayer friend);

    CompletableFuture<Void> addFriendAsync(Chunk chunk, OfflinePlayer friend);

    void removeFriend(Chunk chunk, OfflinePlayer friend);

    CompletableFuture<Void> removeFriendAsync(Chunk chunk, OfflinePlayer friend);

    void setAllFriends(Chunk chunk);

    CompletableFuture<Void> setAllFriendsAsync(Chunk chunk);

    void clearFriends(Chunk chunk);

    CompletableFuture<Void> clearFriendsAsync(Chunk chunk);

    boolean isFriend(Chunk chunk, OfflinePlayer friend);

    CompletableFuture<Boolean> isFriendAsync(Chunk chunk, OfflinePlayer friend);

    UUID getChunkOwner(Chunk chunk);

    CompletableFuture<UUID> getChunkOwnerAsync(Chunk chunk);

    List<Chunk> getChunks(OfflinePlayer player);

    CompletableFuture<List<Chunk>> getChunksAsync(OfflinePlayer player);
}