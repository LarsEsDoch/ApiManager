package dev.lars.apimanager.listeners;

import dev.lars.apimanager.ApiManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class JoinListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreLogin(AsyncPlayerPreLoginEvent e) {
        UUID uuid = e.getUniqueId();
        String name = e.getName();
        ApiManager plugin = ApiManager.getInstance();

        plugin.getPlayerAPI().initPlayer(uuid, name);
        plugin.getLanguageAPI().initPlayer(uuid);
        plugin.getBackpackAPI().initPlayer(uuid);
        plugin.getLimitAPI().initPlayer(uuid);
        plugin.getBanAPI().initPlayer(uuid);
        plugin.getCourtAPI().initPlayer(uuid);
        plugin.getRankAPI().initPlayer(uuid);
        plugin.getPrefixAPI().initPlayer(uuid);
        plugin.getStatusAPI().initPlayer(uuid);
        plugin.getPlayerIdentityAPI().initPlayer(uuid);
        plugin.getPlayerSettingsAPI().initPlayer(uuid);
        plugin.getScoreboardSettingsAPI().initPlayer(uuid);
        plugin.getEconomyAPI().initPlayer(uuid);
        plugin.getQuestAPI().initPlayer(uuid);
        plugin.getTimerAPI().initPlayer(uuid);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent e) {
        ApiManager.getInstance().getPlayerAPI().setOnlineAsync(e.getPlayer(), true);
    }
}