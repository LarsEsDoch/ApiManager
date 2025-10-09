package de.lars.apiManager.languageAPI;

import org.bukkit.entity.Player;

public interface ILanguageAPI {

    void setLanguage(Player player, Integer languageId);

    Integer getLanguage(Player player);

    boolean doesUserExist(Player player);
}
