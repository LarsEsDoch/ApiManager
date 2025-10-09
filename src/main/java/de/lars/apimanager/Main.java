package de.lars.apiManager;

import de.lars.apiManager.backpackAPI.BackpackAPI;
import de.lars.apiManager.backpackAPI.BackpackAPIImpl;
import de.lars.apiManager.banAPI.BanAPI;
import de.lars.apiManager.banAPI.BanAPIImpl;
import de.lars.apiManager.chunkAPI.ChunkAPI;
import de.lars.apiManager.chunkAPI.ChunkAPIImpl;
import de.lars.apiManager.coinAPI.CoinAPI;
import de.lars.apiManager.coinAPI.CoinAPIImpl;
import de.lars.apiManager.dataAPI.DataAPI;
import de.lars.apiManager.dataAPI.DataAPIImpl;
import de.lars.apiManager.database.MySQL;
import de.lars.apiManager.homeAPI.HomeAPI;
import de.lars.apiManager.homeAPI.HomeAPIImpl;
import de.lars.apiManager.languageAPI.LanguageAPI;
import de.lars.apiManager.languageAPI.LanguageAPIImpl;
import de.lars.apiManager.listeners.JoinListener;
import de.lars.apiManager.playersAPI.PlayerAPI;
import de.lars.apiManager.playersAPI.PlayerAPIImpl;
import de.lars.apiManager.questAPI.QuestAPI;
import de.lars.apiManager.questAPI.QuestAPIImpl;
import de.lars.apiManager.rankAPI.RankAPI;
import de.lars.apiManager.rankAPI.RankAPIImpl;
import de.lars.apiManager.timerAPI.TimerAPI;
import de.lars.apiManager.timerAPI.TimerAPIImpl;
import de.lars.apiManager.toggleAPI.ToggleAPI;
import de.lars.apiManager.toggleAPI.ToggleAPIImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;
    private MySQL mySQL;
    private PlayerAPIImpl playerAPI;
    private CoinAPIImpl coinAPI;
    private LanguageAPIImpl languageAPI;
    private RankAPIImpl rankAPI;
    private ToggleAPIImpl toggleAPI;
    private HomeAPIImpl homeAPI;
    private QuestAPIImpl questAPI;
    private BanAPIImpl banAPI;
    private ChunkAPIImpl chunkAPI;
    private TimerAPIImpl timerAPI;
    private BackpackAPIImpl backpackAPI;
    private DataAPIImpl dataAPI;


    @Override
    public void onLoad() {
        instance = this;

        saveDefaultConfig();

        String host = getConfig().getString("database.host");
        int port = getConfig().getInt("database.port");
        String database = getConfig().getString("database.database");
        String user = getConfig().getString("database.username");
        String password = getConfig().getString("database.password");

        this.mySQL = MySQL.newBuilder()
                .withUrl(host)
                .withPort(port)
                .withDatabase(database)
                .withUser(user)
                .withPassword(password)
                .create();
    }

    @Override
    public void onEnable() {
        playerAPI = new PlayerAPIImpl();
        playerAPI.createTables();
        PlayerAPI.setApi(playerAPI);

        backpackAPI = new BackpackAPIImpl();
        backpackAPI.createTables();
        BackpackAPI.setApi(backpackAPI);

        coinAPI = new CoinAPIImpl();
        coinAPI.createTables();
        CoinAPI.setApi(coinAPI);

        languageAPI = new LanguageAPIImpl();
        languageAPI.createTables();
        LanguageAPI.setApi(languageAPI);

        rankAPI = new RankAPIImpl();
        rankAPI.createTables();
        RankAPI.setApi(rankAPI);

        toggleAPI = new ToggleAPIImpl();
        toggleAPI.createTables();
        ToggleAPI.setApi(toggleAPI);

        homeAPI = new HomeAPIImpl();
        homeAPI.createTables();
        HomeAPI.setApi(homeAPI);

        questAPI = new QuestAPIImpl();
        questAPI.createTables();
        QuestAPI.setApi(questAPI);

        banAPI = new BanAPIImpl();
        banAPI.createTables();
        BanAPI.setApi(banAPI);

        chunkAPI = new ChunkAPIImpl();
        chunkAPI.createTables();
        ChunkAPI.setApi(chunkAPI);

        timerAPI = new TimerAPIImpl();
        timerAPI.createTables();
        TimerAPI.setApi(timerAPI);

        dataAPI = new DataAPIImpl();
        dataAPI.createTables();
        DataAPI.setApi(dataAPI);

        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
        Component message = Component.text()
                .append(Component.text("[", NamedTextColor.DARK_GRAY))
                .append(Component.text("ApiManager", NamedTextColor.GOLD))
                .append(Component.text("]", NamedTextColor.DARK_GRAY))
                .append(Component.text(" All APIs are ready!", NamedTextColor.DARK_GREEN))
                .build();
        Bukkit.getConsoleSender().sendMessage(message);
    }

    public static Main getInstance() {
        return instance;
    }

    public MySQL getMySQL() {
        return mySQL;
    }

    public PlayerAPIImpl getPlayerAPI() {
        return playerAPI;
    }

    public CoinAPIImpl getCoinAPI() {
        return coinAPI;
    }

    public LanguageAPIImpl getLanguageAPI() {
        return languageAPI;
    }

    public RankAPIImpl getRankAPI() {
        return rankAPI;
    }

    public ToggleAPIImpl getToggleAPI() {
        return toggleAPI;
    }

    public HomeAPIImpl getHomeAPI() {
        return homeAPI;
    }

    public QuestAPIImpl getQuestAPI() {
        return questAPI;
    }

    public BanAPIImpl getBanAPI() {
        return banAPI;
    }

    public ChunkAPIImpl getChunkAPI() {
        return chunkAPI;
    }

    public TimerAPIImpl getTimerAPI() {
        return timerAPI;
    }

    public BackpackAPIImpl getBackpackAPI() {
        return backpackAPI;
    }

    public DataAPIImpl getDataAPI() {
        return dataAPI;
    }
}
