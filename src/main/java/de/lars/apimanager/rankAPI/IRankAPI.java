package de.lars.apiManager.rankAPI;

import org.bukkit.entity.Player;

import java.util.Calendar;

public interface IRankAPI {
    void setRankID(Player player, int rang, int time, Calendar date);

    void addRankDays(Player player, int days);

    void setPrefix(Player player, int count);

    void setPrefixType(Player player, int count);

    void setStatus(Player player, String status);

    Integer getRankID(Player player);

    Integer getRankTime(Player player);

    Calendar getRankDate(Player player);

    Integer getPrefix(Player player);

    Integer getPrefixType(Player player);

    String getStatus(Player player);

    boolean doesUserExist(Player player);
}
