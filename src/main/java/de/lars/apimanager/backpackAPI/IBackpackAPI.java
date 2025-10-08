package de.lars.apiManager.backpackAPI;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public interface IBackpackAPI {
    void setSlots(Player player, int slots);

    void setBackpack(OfflinePlayer player, String data);

    int getSlots(OfflinePlayer player);

    String getBackpack(OfflinePlayer player);

    List<String> getUUIDs();

    void initPlayer(Player player);

    boolean doesUserExist(Player player);

    boolean doesUserExist(OfflinePlayer player);

    void createTables();
}
