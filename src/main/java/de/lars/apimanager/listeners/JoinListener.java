package de.lars.apiManager.listeners;

import de.lars.apiManager.Main;
import de.lars.apiManager.backpackAPI.BackpackAPIImpl;
import de.lars.apiManager.banAPI.BanAPIImpl;
import de.lars.apiManager.coinAPI.CoinAPIImpl;
import de.lars.apiManager.languageAPI.LanguageAPIImpl;
import de.lars.apiManager.playersAPI.PlayerAPIImpl;
import de.lars.apiManager.questAPI.QuestAPIImpl;
import de.lars.apiManager.rankAPI.RankAPIImpl;
import de.lars.apiManager.timerAPI.TimerAPIImpl;
import de.lars.apiManager.toggleAPI.ToggleAPIImpl;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    PlayerAPIImpl playerAPI = Main.getInstance().getPlayerAPI();
    CoinAPIImpl coinAPI = Main.getInstance().getCoinAPI();
    LanguageAPIImpl languageAPI = Main.getInstance().getLanguageAPI();
    RankAPIImpl rankAPI = Main.getInstance().getRankAPI();
    ToggleAPIImpl toggleAPI = Main.getInstance().getToggleAPI();
    QuestAPIImpl questAPI = Main.getInstance().getQuestAPI();
    BanAPIImpl banAPI = Main.getInstance().getBanAPI();
    TimerAPIImpl timerAPI = Main.getInstance().getTimerAPI();
    BackpackAPIImpl backpackAPI = Main.getInstance().getBackpackAPI();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        if (!playerAPI.doesUserExist(e.getPlayer())) {
            playerAPI.initPlayer(e.getPlayer());
        }
        if (!coinAPI.doesUserExist(e.getPlayer())) {
            coinAPI.initPlayer(e.getPlayer());
        }
        if (!languageAPI.doesUserExist(e.getPlayer())) {
            languageAPI.initPlayer(e.getPlayer());
        }
        if (!rankAPI.doesUserExist(e.getPlayer())) {
            rankAPI.initPlayer(e.getPlayer());
        }
        if (!toggleAPI.doesUserExist(e.getPlayer())) {
            toggleAPI.initPlayer(e.getPlayer());
        }
        if (!questAPI.doesUserExist(e.getPlayer())) {
            questAPI.initPlayer(e.getPlayer());
        }
        if (!banAPI.doesUserExist(e.getPlayer())) {
            banAPI.initPlayer(e.getPlayer());
        }
        if (!banAPI.doesCriminalUserExist(e.getPlayer())) {
            banAPI.initPlayerC(e.getPlayer());
        }
        if (!timerAPI.doesUserExist(e.getPlayer())) {
            timerAPI.initPlayer(e.getPlayer());
        }
        if (!backpackAPI.doesUserExist(e.getPlayer())) {
            backpackAPI.initPlayer(e.getPlayer());
        }
    }
}
