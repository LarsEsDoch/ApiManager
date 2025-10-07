package de.lars.apimanager.banAPI;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public interface IBanAPI {
    void setBanned(Player player, String reason, Integer time);

    void setCriminal(Player player, String reason, Integer time, Player pplayer);

    void setLocked(Player player, Integer time, Integer cell);

    void setOnWait(Player player);

    void setOnLock(Player player);

    void setOnCourt(Player player);

    void setLockTime(Player player, Integer time);

    void setUnlocked(Player player);

    void setUnBaned(OfflinePlayer player);

    Boolean getBanned(Player player);

    Integer getCriminalTime(Player player);

    Integer getCell(Player player);

    Integer getTime(Player player);

    Calendar getBanDate(Player player);

    String getReason(Player player);

    String getCriminalReason(Player player);

    Integer isCriminal(Player player);

    String getProsecutor(Player player);

    void initPlayer(Player player);

    void initPlayerC(Player player);

    List<String> getBannedPlayers();

    boolean doesUserExist(OfflinePlayer player);

    boolean doesCriminalUserExist(Player player);

    void createTables();
}
