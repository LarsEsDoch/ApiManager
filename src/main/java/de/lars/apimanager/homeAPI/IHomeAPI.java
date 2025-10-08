package de.lars.apiManager.homeAPI;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface IHomeAPI {

    void createHome(Player player, String name, Location location, boolean isPublic);

    void deleteHome(Player player, int homeID);

    boolean doesHomeExist(Player player, String name);

    boolean doesOwnHomeExist(Player player, String name);

    int getHomeId(Player player, String name);

    Location getHomeLocation(int homeID);

    List<String> getHomes(Player player);

    List<String> getOwnHomes(Player player);

    void setHomePublic(Player player, int homeID, boolean isPublic);

    void createTables();
}
