package de.lars.apimanager.playersAPI;

import org.bukkit.entity.Player;

public interface IPlayerAPI {

    void setPlaytime(Player player, Integer playtime);

    Integer getPlaytime(Player player);

    boolean doesUserExist(Player player);

}
