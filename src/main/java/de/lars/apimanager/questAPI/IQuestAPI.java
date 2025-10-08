package de.lars.apiManager.questAPI;

import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;

public interface IQuestAPI {

    void setStreak(Player player, int amount);

    void setQuest(Player player, int amount, int number);

    void setQuestComplete(Player player, boolean amount);

    void setDailyhasnumber(Player player, int number);

    void addDailyhasnumber(Player player, int number);

    void removeDailyhasnumber(Player player, int number);

    Date getQuestDate(Player player);

    Integer getDailyQuestHasNumber(Player player);

    boolean getDailyQuestComplete(Player player);

    Integer getDailyQuestNumber(Player player);

    Integer getDailyQuest(Player player);

    Integer getStreak(Player player);

    boolean doesUserExist(Player player);

    void createTables();
}
