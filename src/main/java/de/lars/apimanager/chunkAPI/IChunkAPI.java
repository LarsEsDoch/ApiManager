package de.lars.apimanager.chunkAPI;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface IChunkAPI {
    void claimChunk(Player player, String playerName, String chunk, String friends);

    void addFree(Player player, Integer add);

    void removeFree(Player player, Integer remove);

    void addFriend(Player player, String chunk, String friend);

    void removeFriend(Player player, String chunk, String friend);

    void setEntitySpawning(Player player, String chunk, Boolean spawning);

    Boolean getEntitySpawning(String chunk);

    UUID getChunkOwner(String chunk);

    List<String> getFriends(String chunk);

    void sellChunk(Player player, String chunk);

    int getFreeChunks(Player player);

    List<String> getChunks(Player player);

    boolean doesChunkHaveOwner(String chunk);

    void createTables();
}
