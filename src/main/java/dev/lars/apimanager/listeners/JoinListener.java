package dev.lars.apimanager.listeners;

import dev.lars.apimanager.ApiManager;
import dev.lars.apimanager.apis.backpackAPI.BackpackAPIImpl;
import dev.lars.apimanager.apis.banAPI.BanAPIImpl;
import dev.lars.apimanager.apis.courtAPI.CourtAPIImpl;
import dev.lars.apimanager.apis.economyAPI.EconomyAPIImpl;
import dev.lars.apimanager.apis.languageAPI.LanguageAPIImpl;
import dev.lars.apimanager.apis.limitAPI.LimitAPIImpl;
import dev.lars.apimanager.apis.playerAPI.PlayerAPIImpl;
import dev.lars.apimanager.apis.playerIdentityAPI.PlayerIdentityAPIImpl;
import dev.lars.apimanager.apis.playerSettingsAPI.PlayerSettingsAPIImpl;
import dev.lars.apimanager.apis.prefixAPI.PrefixAPIImpl;
import dev.lars.apimanager.apis.questAPI.QuestAPIImpl;
import dev.lars.apimanager.apis.rankAPI.RankAPIImpl;
import dev.lars.apimanager.apis.scoreboardSettingsAPI.ScoreboardSettingsAPIImpl;
import dev.lars.apimanager.apis.statusAPI.StatusAPIImpl;
import dev.lars.apimanager.apis.timerAPI.TimerAPIImpl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class JoinListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(PlayerLoginEvent e) {
        Player player = e.getPlayer();

        ApiManager plugin = ApiManager.getInstance();

        plugin.getPlayerAPI().initPlayer(player);
        plugin.getLanguageAPI().initPlayer(player);
        plugin.getBackpackAPI().initPlayer(player);
        plugin.getLimitAPI().initPlayer(player);
        plugin.getBanAPI().initPlayer(player);
        plugin.getCourtAPI().initPlayer(player);
        plugin.getRankAPI().initPlayer(player);
        plugin.getPrefixAPI().initPlayer(player);
        plugin.getStatusAPI().initPlayer(player);
        plugin.getPlayerIdentityAPI().initPlayer(player);
        plugin.getPlayerSettingsAPI().initPlayer(player);
        plugin.getScoreboardSettingsAPI().initPlayer(player);
        plugin.getEconomyAPI().initPlayer(player);
        plugin.getQuestAPI().initPlayer(player);
        plugin.getTimerAPI().initPlayer(player);

        plugin.getPlayerAPI().setOnline(player, true);
    }
}