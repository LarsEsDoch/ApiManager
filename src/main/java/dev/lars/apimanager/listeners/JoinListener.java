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

        PlayerAPIImpl playerAPI = ApiManager.getInstance().getPlayerAPI();
        LanguageAPIImpl languageAPI = ApiManager.getInstance().getLanguageAPI();
        BackpackAPIImpl backpackAPI = ApiManager.getInstance().getBackpackAPI();
        LimitAPIImpl limitAPI = ApiManager.getInstance().getLimitAPI();
        BanAPIImpl banAPI = ApiManager.getInstance().getBanAPI();
        CourtAPIImpl courtAPI = ApiManager.getInstance().getCourtAPI();
        RankAPIImpl rankAPI = ApiManager.getInstance().getRankAPI();
        PrefixAPIImpl prefixAPI = ApiManager.getInstance().getPrefixAPI();
        StatusAPIImpl statusAPI = ApiManager.getInstance().getStatusAPI();
        PlayerIdentityAPIImpl playerIdentityAPI = ApiManager.getInstance().getPlayerIdentityAPI();
        PlayerSettingsAPIImpl playerSettingsAPI = ApiManager.getInstance().getPlayerSettingsAPI();
        EconomyAPIImpl economyAPI = ApiManager.getInstance().getEconomyAPI();
        QuestAPIImpl questAPI = ApiManager.getInstance().getQuestAPI();
        TimerAPIImpl timerAPI = ApiManager.getInstance().getTimerAPI();

        if (!playerAPI.doesUserExist(player)) {
            playerAPI.initPlayer(player);
        }

        if (!languageAPI.doesUserExist(player)) {
            languageAPI.initPlayer(player);
        }

        if (!backpackAPI.doesUserExist(player)) {
            backpackAPI.initPlayer(player);
        }

        if (!limitAPI.doesUserExist(player)) {
            limitAPI.initPlayer(player);
        }

        if (!banAPI.doesUserExist(player)) {
            banAPI.initPlayer(player);
        }

        if (!courtAPI.doesUserExist(player)) {
            courtAPI.initPlayer(player);
        }

        if (!rankAPI.doesUserExist(player)) {
            rankAPI.initPlayer(player);
        }

        if (!prefixAPI.doesUserExist(player)) {
            prefixAPI.initPlayer(player);
        }

        if (!statusAPI.doesUserExist(player)) {
            statusAPI.initPlayer(player);
        }

        if (!playerIdentityAPI.doesUserExist(player)) {
            playerIdentityAPI.initPlayer(player);
        }

        if (!playerSettingsAPI.doesUserExist(player)) {
            playerSettingsAPI.initPlayer(player);
        }

        if (!economyAPI.doesUserExist(player)) {
            economyAPI.initPlayer(player);
        }

        if (!questAPI.doesUserExist(player)) {
            questAPI.initPlayer(player);
        }

        if (!timerAPI.doesUserExist(player)) {
            timerAPI.initPlayer(player);
        }
        playerAPI.setOnline(player, true);
    }
}