package de.lars.apimanager.toggleAPI;

import org.bukkit.entity.Player;

import java.util.UUID;

public interface IToggleAPI {
    void setBedToggle(Player player, boolean toggle);

    void setScoreboardToggle(Player player, boolean toggle);

    Boolean getBedToggle(Player player);

    Boolean getScoreboardToggle(Player player);

    boolean doesUserExist(Player player);
}
