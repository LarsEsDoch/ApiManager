package de.lars.apimanager.coinAPI;

import org.bukkit.entity.Player;

import java.util.List;

public interface ICoinAPI {

  Integer getCoins(Player player);

  void addCoins(Player player, int amount);

  void removeCoins(Player player, int amount);

  void setCoins(Player player, int amount);

  void addGift(Player player, String gift);

  void removeGift(Player player, String gift);

  List<String> getGifts(Player player);

  boolean doesUserExist(Player player);
}
