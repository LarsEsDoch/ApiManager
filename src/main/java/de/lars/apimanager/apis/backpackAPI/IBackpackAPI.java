package de.lars.apimanager.apis.backpackAPI;

import org.bukkit.OfflinePlayer;

import java.util.concurrent.CompletableFuture;

public interface IBackpackAPI {
    void setSlots(OfflinePlayer player, int slots);

    CompletableFuture<Void> setSlotsAsync(OfflinePlayer player, int slots);


    Integer getSlots(OfflinePlayer player);

    CompletableFuture<Integer> getSlotsAsync(OfflinePlayer player);


    void setBackpack(OfflinePlayer player, String data);

    CompletableFuture<Void> setBackpackAsync(OfflinePlayer player, String data);


    String getBackpack(OfflinePlayer player);

    CompletableFuture<String> getBackpackAsync(OfflinePlayer player);
}