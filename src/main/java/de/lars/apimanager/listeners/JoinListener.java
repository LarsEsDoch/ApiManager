package de.lars.apimanager.listeners;

import de.lars.apimanager.ApiManager;
import de.lars.apimanager.apis.backpackAPI.BackpackAPIImpl;
import de.lars.apimanager.apis.banAPI.BanAPIImpl;
import de.lars.apimanager.apis.courtAPI.CourtAPIImpl;
import de.lars.apimanager.apis.economyAPI.EconomyAPIImpl;
import de.lars.apimanager.apis.languageAPI.LanguageAPIImpl;
import de.lars.apimanager.apis.limitAPI.LimitAPIImpl;
import de.lars.apimanager.apis.nickAPI.NickAPIImpl;
import de.lars.apimanager.apis.playerAPI.PlayerAPIImpl;
import de.lars.apimanager.apis.playerSettingsAPI.PlayerSettingsAPIImpl;
import de.lars.apimanager.apis.prefixAPI.PrefixAPIImpl;
import de.lars.apimanager.apis.questAPI.QuestAPIImpl;
import de.lars.apimanager.apis.rankAPI.RankAPIImpl;
import de.lars.apimanager.apis.statusAPI.StatusAPIImpl;
import de.lars.apimanager.apis.timerAPI.TimerAPIImpl;
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
        NickAPIImpl nickAPI = ApiManager.getInstance().getNickAPI();
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

        if (!nickAPI.doesUserExist(player)) {
            nickAPI.initPlayer(player);
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