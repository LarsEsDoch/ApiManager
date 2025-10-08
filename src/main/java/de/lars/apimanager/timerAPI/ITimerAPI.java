package de.lars.apiManager.timerAPI;

import org.bukkit.entity.Player;

public interface ITimerAPI {
    void setTime(Player player, int time);

    void setOff(Player player, boolean off);

    void setRunning(Player player, boolean running);

    void setTimer(Player player, boolean timer);

    void setPublic(Player player, boolean isPublic);

    int getTime(Player player);

    boolean isPublic(Player player);

    boolean isOff(Player player);

    boolean isRunning(Player player);

    boolean isTimer(Player player);

    boolean publicTimerExists(Player player);

    boolean doesUserExist(Player player);

    void createTables();
}
