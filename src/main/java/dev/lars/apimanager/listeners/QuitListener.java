package dev.lars.apimanager.listeners;

import dev.lars.apimanager.apis.playerAPI.PlayerAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent e) {
        PlayerAPI.getApi().setOnline(e.getPlayer(), false);
    }
}