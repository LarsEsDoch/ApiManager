package de.lars.apimanager;

import de.lars.apimanager.backpackAPI.BackpackAPI;
import de.lars.apimanager.backpackAPI.BackpackAPIImpl;
import de.lars.apimanager.banAPI.BanAPI;
import de.lars.apimanager.banAPI.BanAPIImpl;
import de.lars.apimanager.chunkAPI.ChunkAPI;
import de.lars.apimanager.chunkAPI.ChunkAPIImpl;
import de.lars.apimanager.coinAPI.CoinAPI;
import de.lars.apimanager.coinAPI.CoinAPIImpl;
import de.lars.apimanager.dataAPI.DataAPI;
import de.lars.apimanager.dataAPI.DataAPIImpl;
import de.lars.apimanager.database.MySQL;
import de.lars.apimanager.homeAPI.HomeAPI;
import de.lars.apimanager.homeAPI.HomeAPIImpl;
import de.lars.apimanager.languageAPI.LanguageAPI;
import de.lars.apimanager.languageAPI.LanguageAPIImpl;
import de.lars.apimanager.listeners.JoinListener;
import de.lars.apimanager.playersAPI.PlayerAPI;
import de.lars.apimanager.playersAPI.PlayerAPIImpl;
import de.lars.apimanager.questAPI.QuestAPI;
import de.lars.apimanager.questAPI.QuestAPIImpl;
import de.lars.apimanager.rankAPI.RankAPI;
import de.lars.apimanager.rankAPI.RankAPIImpl;
import de.lars.apimanager.timerAPI.TimerAPI;
import de.lars.apimanager.timerAPI.TimerAPIImpl;
import de.lars.apimanager.toggleAPI.ToggleAPI;
import de.lars.apimanager.toggleAPI.ToggleAPIImpl;
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

        backpackAPI = new BackpackAPIImpl();
        backpackAPI.createTables();
        BackpackAPI.setApi(backpackAPI);
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
                .append(Component.text(" All APIs are loaded!", NamedTextColor.DARK_GREEN))
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
