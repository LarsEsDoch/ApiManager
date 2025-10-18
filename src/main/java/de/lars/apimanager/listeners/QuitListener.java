package de.lars.apimanager.listeners;

import de.lars.apimanager.apis.playerAPI.PlayerAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class QuitListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        PlayerAPI.getApi().setOnline(e.getPlayer(), false);
    }
}
