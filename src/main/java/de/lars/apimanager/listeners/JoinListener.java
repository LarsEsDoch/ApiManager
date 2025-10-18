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
import org.bukkit.entity.Player;
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
        Player player = e.getPlayer();

           if (!playerAPI.doesUserExist(player)) {
        if (!playerAPI.doesUserExist(player)) {
            playerAPI.initPlayer(player);
            playerAPI.setOnlineAsync(player, true);
        }
        if (!coinAPI.doesUserExist(player)) {
            coinAPI.initPlayer(player);
        }
        if (!languageAPI.doesUserExist(player)) {
            languageAPI.initPlayer(player);
        }
        if (!rankAPI.doesUserExist(player)) {
            rankAPI.initPlayer(player);
        }
        if (!toggleAPI.doesUserExist(player)) {
            toggleAPI.initPlayer(player);
        }
        if (!questAPI.doesUserExist(player)) {
            questAPI.initPlayer(player);
        }
        if (!banAPI.doesUserExist(player)) {
            banAPI.initPlayer(player);
        }
        if (!timerAPI.doesUserExist(player)) {
            timerAPI.initPlayer(player);
        }
        if (!backpackAPI.doesUserExist(player)) {
            backpackAPI.initPlayer(player);
        }
    }
}
