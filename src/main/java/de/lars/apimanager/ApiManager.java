package de.lars.apimanager;

import de.lars.apimanager.apis.backpackAPI.BackpackAPI;
import de.lars.apimanager.apis.backpackAPI.BackpackAPIImpl;
import de.lars.apimanager.apis.banAPI.BanAPI;
import de.lars.apimanager.apis.banAPI.BanAPIImpl;
import de.lars.apimanager.apis.coinAPI.CoinAPI;
import de.lars.apimanager.apis.coinAPI.CoinAPIImpl;
import de.lars.apimanager.apis.courtAPI.CourtAPI;
import de.lars.apimanager.apis.courtAPI.CourtAPIImpl;
import de.lars.apimanager.apis.languageAPI.LanguageAPI;
import de.lars.apimanager.apis.languageAPI.LanguageAPIImpl;
import de.lars.apimanager.apis.limitAPI.LimitAPI;
import de.lars.apimanager.apis.limitAPI.LimitAPIImpl;
import de.lars.apimanager.apis.nickAPI.NickAPI;
import de.lars.apimanager.apis.nickAPI.NickAPIImpl;
import de.lars.apimanager.apis.playerAPI.PlayerAPI;
import de.lars.apimanager.apis.playerAPI.PlayerAPIImpl;
import de.lars.apimanager.apis.prefixAPI.PrefixAPI;
import de.lars.apimanager.apis.prefixAPI.PrefixAPIImpl;
import de.lars.apimanager.apis.questAPI.QuestAPI;
import de.lars.apimanager.apis.questAPI.QuestAPIImpl;
import de.lars.apimanager.apis.rankAPI.RankAPI;
import de.lars.apimanager.apis.rankAPI.RankAPIImpl;
import de.lars.apimanager.apis.serverSettingsAPI.ServerSettingsAPI;
import de.lars.apimanager.apis.serverSettingsAPI.ServerSettingsAPIImpl;
import de.lars.apimanager.apis.statusAPI.StatusAPI;
import de.lars.apimanager.apis.statusAPI.StatusAPIImpl;
import de.lars.apimanager.apis.timerAPI.TimerAPI;
import de.lars.apimanager.apis.timerAPI.TimerAPIImpl;
import de.lars.apimanager.apis.toggleAPI.ToggleAPI;
import de.lars.apimanager.apis.toggleAPI.ToggleAPIImpl;
import de.lars.apimanager.database.DatabaseManager;
import de.lars.apimanager.listeners.JoinListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class ApiManager extends JavaPlugin {
    private static ApiManager instance;
    private DatabaseManager databaseManager;

    private ServerSettingsAPIImpl serverSettingsAPI;
    private PlayerAPIImpl playerAPI;
    private LanguageAPIImpl languageAPI;
    private BackpackAPIImpl backpackAPI;
    private LimitAPIImpl limitAPI;
    private BanAPIImpl banAPI;
    private CourtAPIImpl courtAPI;
    private RankAPIImpl rankAPI;
    private PrefixAPIImpl prefixAPI;
    private StatusAPIImpl statusAPI;
    private NickAPIImpl nickAPI;
    private ToggleAPIImpl toggleAPI;
    private CoinAPIImpl coinAPI;
    private QuestAPIImpl questAPI;
    private TimerAPIImpl timerAPI;

    @Override
    public void onLoad() {
        instance = this;
        saveDefaultConfig();

        String host = getConfig().getString("database.host");
        int port = getConfig().getInt("database.port");
        String database = getConfig().getString("database.database");
        String user = getConfig().getString("database.user");
        String password = getConfig().getString("database.password");

        this.databaseManager = new DatabaseManager(host, port, database, user, password);
    }

    @Override
    public void onEnable() {
        serverSettingsAPI = new ServerSettingsAPIImpl();
        ServerSettingsAPI.setApi(serverSettingsAPI);
        serverSettingsAPI.createTables();

        playerAPI = new PlayerAPIImpl();
        PlayerAPI.setApi(playerAPI);
        playerAPI.createTables();

        languageAPI = new LanguageAPIImpl();
        LanguageAPI.setApi(languageAPI);
        languageAPI.createTables();

        backpackAPI = new BackpackAPIImpl();
        BackpackAPI.setApi(backpackAPI);
        backpackAPI.createTables();

        limitAPI = new LimitAPIImpl();
        LimitAPI.setApi(limitAPI);
        limitAPI.createTables();

        banAPI = new BanAPIImpl();
        BanAPI.setApi(banAPI);
        banAPI.createTables();

        courtAPI = new CourtAPIImpl();
        CourtAPI.setApi(courtAPI);
        courtAPI.createTables();

        rankAPI = new RankAPIImpl();
        RankAPI.setApi(rankAPI);
        rankAPI.createTables();

        prefixAPI = new PrefixAPIImpl();
        PrefixAPI.setApi(prefixAPI);
        prefixAPI.createTables();

        statusAPI = new StatusAPIImpl();
        StatusAPI.setApi(statusAPI);
        statusAPI.createTables();

        nickAPI = new NickAPIImpl();
        NickAPI.setApi(nickAPI);
        nickAPI.createTables();

        toggleAPI = new ToggleAPIImpl();
        ToggleAPI.setApi(toggleAPI);
        toggleAPI.createTables();

        coinAPI = new CoinAPIImpl();
        CoinAPI.setApi(coinAPI);
        coinAPI.createTables();

        questAPI = new QuestAPIImpl();
        QuestAPI.setApi(questAPI);
        questAPI.createTables();

        timerAPI = new TimerAPIImpl();
        TimerAPI.setApi(timerAPI);
        timerAPI.createTables();

        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);

        Component message = Component.text()
                .append(Component.text("[", NamedTextColor.DARK_GRAY))
                .append(Component.text("ApiManager", NamedTextColor.GOLD))
                .append(Component.text("]", NamedTextColor.DARK_GRAY))
                .append(Component.text(" All APIs are ready!", NamedTextColor.DARK_GREEN))
                .build();

        Bukkit.getConsoleSender().sendMessage(message);
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.close();
        }

        Component message = Component.text()
                .append(Component.text("[", NamedTextColor.DARK_GRAY))
                .append(Component.text("ApiManager", NamedTextColor.GOLD))
                .append(Component.text("]", NamedTextColor.DARK_GRAY))
                .append(Component.text(" Database successfully disdonnected!", NamedTextColor.GREEN))
                .build();

        Bukkit.getConsoleSender().sendMessage(message);
    }

    public static ApiManager getInstance() {
        return instance;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public PlayerAPIImpl getPlayerAPI() {
        return playerAPI;
    }

    public LanguageAPIImpl getLanguageAPI() {
        return languageAPI;
    }

    public BackpackAPIImpl getBackpackAPI() {
        return backpackAPI;
    }

    public LimitAPIImpl getLimitAPI() {
        return limitAPI;
    }

    public BanAPIImpl getBanAPI() {
        return banAPI;
    }

    public CourtAPIImpl getCourtAPI() {
        return courtAPI;
    }

    public RankAPIImpl getRankAPI() {
        return rankAPI;
    }

    public PrefixAPIImpl getPrefixAPI() {
        return prefixAPI;
    }

    public StatusAPIImpl getStatusAPI() {
        return statusAPI;
    }

    public NickAPIImpl getNickAPI() {
        return nickAPI;
    }

    public ToggleAPIImpl getToggleAPI() {
        return toggleAPI;
    }

    public CoinAPIImpl getCoinAPI() {
        return coinAPI;
    }

    public QuestAPIImpl getQuestAPI() {
        return questAPI;
    }

    public TimerAPIImpl getTimerAPI() {
        return timerAPI;
    }
}