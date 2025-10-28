package de.lars.apimanager.listeners;

import de.lars.apimanager.Main;
import de.lars.apimanager.apis.backpackAPI.BackpackAPIImpl;
import de.lars.apimanager.apis.banAPI.BanAPIImpl;
import de.lars.apimanager.apis.coinAPI.CoinAPIImpl;
import de.lars.apimanager.apis.courtAPI.CourtAPIImpl;
import de.lars.apimanager.apis.languageAPI.LanguageAPIImpl;
import de.lars.apimanager.apis.limitAPI.LimitAPIImpl;
import de.lars.apimanager.apis.nickAPI.NickAPIImpl;
import de.lars.apimanager.apis.playerAPI.PlayerAPIImpl;
import de.lars.apimanager.apis.prefixAPI.PrefixAPIImpl;
import de.lars.apimanager.apis.questAPI.QuestAPIImpl;
import de.lars.apimanager.apis.rankAPI.RankAPIImpl;
import de.lars.apimanager.apis.statusAPI.StatusAPIImpl;
import de.lars.apimanager.apis.timerAPI.TimerAPIImpl;
import de.lars.apimanager.apis.toggleAPI.ToggleAPIImpl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    PlayerAPIImpl playerAPI = Main.getInstance().getPlayerAPI();
    LanguageAPIImpl languageAPI = Main.getInstance().getLanguageAPI();
    BackpackAPIImpl backpackAPI = Main.getInstance().getBackpackAPI();
    LimitAPIImpl limitAPI = Main.getInstance().getLimitAPI();
    BanAPIImpl banAPI = Main.getInstance().getBanAPI();
    CourtAPIImpl courtAPI = Main.getInstance().getCourtAPI();
    RankAPIImpl rankAPI = Main.getInstance().getRankAPI();
    PrefixAPIImpl prefixAPI = Main.getInstance().getPrefixAPI();
    StatusAPIImpl statusAPI = Main.getInstance().getStatusAPI();
    NickAPIImpl nickAPI = Main.getInstance().getNickAPI();
    ToggleAPIImpl toggleAPI = Main.getInstance().getToggleAPI();
    CoinAPIImpl coinAPI = Main.getInstance().getCoinAPI();
    QuestAPIImpl questAPI = Main.getInstance().getQuestAPI();
    TimerAPIImpl timerAPI = Main.getInstance().getTimerAPI();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        if (!playerAPI.doesUserExist(player)) {
            playerAPI.initPlayer(player);
            playerAPI.setOnlineAsync(player, true);
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

        if (!toggleAPI.doesUserExist(player)) {
            toggleAPI.initPlayer(player);
        }

        if (!coinAPI.doesUserExist(player)) {
            coinAPI.initPlayer(player);
        }

        if (!questAPI.doesUserExist(player)) {
            questAPI.initPlayer(player);
        }

        if (!timerAPI.doesUserExist(player)) {
            timerAPI.initPlayer(player);
        }
    }
}