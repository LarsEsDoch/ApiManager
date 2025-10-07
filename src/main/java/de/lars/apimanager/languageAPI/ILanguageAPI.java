package de.lars.apimanager.languageAPI;

import org.bukkit.entity.Player;

import java.util.UUID;

public interface ILanguageAPI {

    void setLanguage(Player player, Integer languageId);

    Integer getLanguage(Player player);

    boolean doesUserExist(Player player);
}
