package dev.lars.apimanager;

import dev.lars.apimanager.apis.backpackAPI.BackpackAPI;
import dev.lars.apimanager.apis.backpackAPI.BackpackAPIImpl;
import dev.lars.apimanager.apis.banAPI.BanAPI;
import dev.lars.apimanager.apis.banAPI.BanAPIImpl;
import dev.lars.apimanager.apis.chunkAPI.ChunkAPI;
import dev.lars.apimanager.apis.chunkAPI.ChunkAPIImpl;
import dev.lars.apimanager.apis.courtAPI.CourtAPI;
import dev.lars.apimanager.apis.courtAPI.CourtAPIImpl;
import dev.lars.apimanager.apis.economyAPI.EconomyAPI;
import dev.lars.apimanager.apis.economyAPI.EconomyAPIImpl;
import dev.lars.apimanager.apis.homeAPI.HomeAPI;
import dev.lars.apimanager.apis.homeAPI.HomeAPIImpl;
import dev.lars.apimanager.apis.languageAPI.LanguageAPI;
import dev.lars.apimanager.apis.languageAPI.LanguageAPIImpl;
import dev.lars.apimanager.apis.limitAPI.LimitAPI;
import dev.lars.apimanager.apis.limitAPI.LimitAPIImpl;
import dev.lars.apimanager.apis.playerIdentityAPI.PlayerIdentityAPI;
import dev.lars.apimanager.apis.playerIdentityAPI.PlayerIdentityAPIImpl;
import dev.lars.apimanager.apis.playerAPI.PlayerAPI;
import dev.lars.apimanager.apis.playerAPI.PlayerAPIImpl;
import dev.lars.apimanager.apis.playerSettingsAPI.PlayerSettingsAPI;
import dev.lars.apimanager.apis.playerSettingsAPI.PlayerSettingsAPIImpl;
import dev.lars.apimanager.apis.prefixAPI.PrefixAPI;
import dev.lars.apimanager.apis.prefixAPI.PrefixAPIImpl;
import dev.lars.apimanager.apis.questAPI.QuestAPI;
import dev.lars.apimanager.apis.questAPI.QuestAPIImpl;
import dev.lars.apimanager.apis.rankAPI.RankAPI;
import dev.lars.apimanager.apis.rankAPI.RankAPIImpl;
import dev.lars.apimanager.apis.serverSettingsAPI.ServerSettingsAPI;
import dev.lars.apimanager.apis.serverSettingsAPI.ServerSettingsAPIImpl;
import dev.lars.apimanager.apis.statusAPI.StatusAPI;
import dev.lars.apimanager.apis.statusAPI.StatusAPIImpl;
import dev.lars.apimanager.apis.timerAPI.TimerAPI;
import dev.lars.apimanager.apis.timerAPI.TimerAPIImpl;
import dev.lars.apimanager.commands.ApiManagerCommand;
import dev.lars.apimanager.database.ConnectDatabase;
import dev.lars.apimanager.database.DatabaseManager;
import dev.lars.apimanager.database.IDatabaseManager;
import dev.lars.apimanager.listeners.JoinListener;
import dev.lars.apimanager.listeners.QuitListener;
import dev.lars.apimanager.utils.ApiManagerStatements;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class ApiManager extends JavaPlugin {
    private static ApiManager instance;

    private volatile IDatabaseManager databaseManager;
    private ConnectDatabase connectDatabase;

    private ServerSettingsAPIImpl serverSettingsAPI;
    private ChunkAPIImpl chunkAPI;
    private HomeAPIImpl homeAPI;
    private PlayerAPIImpl playerAPI;
    private LanguageAPIImpl languageAPI;
    private BackpackAPIImpl backpackAPI;
    private LimitAPIImpl limitAPI;
    private BanAPIImpl banAPI;
    private CourtAPIImpl courtAPI;
    private RankAPIImpl rankAPI;
    private PrefixAPIImpl prefixAPI;
    private StatusAPIImpl statusAPI;
    private PlayerIdentityAPIImpl playerIdentityAPI;
    private PlayerSettingsAPIImpl playerSettingsAPI;
    private EconomyAPIImpl economyAPI;
    private QuestAPIImpl questAPI;
    private TimerAPIImpl timerAPI;

    private final List<Runnable> createTableRunnable = new ArrayList<>();

    @Override
    public void onLoad() {
        instance = this;

        saveDefaultConfig();

        connectDatabase = new ConnectDatabase(this);

        if (!connectDatabase.loadDatabaseConfig()) {
            ApiManagerStatements.logToConsole("Invalid database configuration. Please adjust config.yml!", NamedTextColor.RED);
        }
    }

    @Override
    public void onEnable() {
        instantiateApis();

        buildCreateTableList();

        if (getDatabaseManager() instanceof DatabaseManager dbm) {
            dbm.readyFuture().thenRun(() -> Bukkit.getScheduler().runTask(this, () -> {
                createAllTables();
                onApisReady();
            }));
        } else {
             ApiManagerStatements.logToConsole("Database not connected. Skipping table creation. APIs will run in safe mode (no DB).", NamedTextColor.GOLD);
        }

        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new QuitListener(), this);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            ApiManagerCommand apiManagerCommand = new ApiManagerCommand(this, connectDatabase);

            commands.register("apimanager", "ApiManager commands", apiManagerCommand);
            commands.register("am", "ApiManager commands", apiManagerCommand);
        });

        ApiManagerStatements.logToConsole("ApiManager enabled!", NamedTextColor.DARK_GREEN);
    }

    private void instantiateApis() {
        serverSettingsAPI = new ServerSettingsAPIImpl();
        ServerSettingsAPI.setApi(serverSettingsAPI);

        chunkAPI = new ChunkAPIImpl();
        ChunkAPI.setApi(chunkAPI);

        homeAPI = new HomeAPIImpl();
        HomeAPI.setApi(homeAPI);

        playerAPI = new PlayerAPIImpl();
        PlayerAPI.setApi(playerAPI);

        languageAPI = new LanguageAPIImpl();
        LanguageAPI.setApi(languageAPI);

        backpackAPI = new BackpackAPIImpl();
        BackpackAPI.setApi(backpackAPI);

        limitAPI = new LimitAPIImpl();
        LimitAPI.setApi(limitAPI);

        banAPI = new BanAPIImpl();
        BanAPI.setApi(banAPI);

        courtAPI = new CourtAPIImpl();
        CourtAPI.setApi(courtAPI);

        rankAPI = new RankAPIImpl();
        RankAPI.setApi(rankAPI);

        prefixAPI = new PrefixAPIImpl();
        PrefixAPI.setApi(prefixAPI);

        statusAPI = new StatusAPIImpl();
        StatusAPI.setApi(statusAPI);

        playerIdentityAPI = new PlayerIdentityAPIImpl();
        PlayerIdentityAPI.setApi(playerIdentityAPI);

        playerSettingsAPI = new PlayerSettingsAPIImpl();
        PlayerSettingsAPI.setApi(playerSettingsAPI);

        economyAPI = new EconomyAPIImpl();
        EconomyAPI.setApi(economyAPI);

        questAPI = new QuestAPIImpl();
        QuestAPI.setApi(questAPI);

        timerAPI = new TimerAPIImpl();
        TimerAPI.setApi(timerAPI);
    }

    private void buildCreateTableList() {
        createTableRunnable.clear();
        createTableRunnable.add(() -> serverSettingsAPI.createTables());
        createTableRunnable.add(() -> chunkAPI.createTables());
        createTableRunnable.add(() -> homeAPI.createTables());
        createTableRunnable.add(() -> playerAPI.createTables());
        createTableRunnable.add(() -> languageAPI.createTables());
        createTableRunnable.add(() -> backpackAPI.createTables());
        createTableRunnable.add(() -> limitAPI.createTables());
        createTableRunnable.add(() -> banAPI.createTables());
        createTableRunnable.add(() -> courtAPI.createTables());
        createTableRunnable.add(() -> rankAPI.createTables());
        createTableRunnable.add(() -> prefixAPI.createTables());
        createTableRunnable.add(() -> statusAPI.createTables());
        createTableRunnable.add(() -> playerIdentityAPI.createTables());
        createTableRunnable.add(() -> playerSettingsAPI.createTables());
        createTableRunnable.add(() -> economyAPI.createTables());
        createTableRunnable.add(() -> questAPI.createTables());
        createTableRunnable.add(() -> timerAPI.createTables());
    }

    private void createAllTables() {
        for (Runnable r : createTableRunnable) {
            try {
                r.run();
            } catch (Exception e) {
                ApiManagerStatements.logToConsole("createTables() failed for one API: " + e.getMessage(), NamedTextColor.GOLD);
            }
        }
    }

    private void onApisReady() {
        serverSettingsAPI.setServerOnline(true);
        ApiManagerStatements.logToConsole("All APIs are ready!", NamedTextColor.DARK_GREEN);
    }

    @Override
    public void onDisable() {
        if (serverSettingsAPI != null) {
            try {
                serverSettingsAPI.setServerOnline(false);
            } catch (Exception ignored) {}
        }
        if (databaseManager != null) {
            databaseManager.close();
            ApiManagerStatements.logToConsole("Database successfully disconnected!", NamedTextColor.GREEN);
        }

        ApiManagerStatements.logToConsole("ApiManager successfully disabled!", NamedTextColor.DARK_GREEN);
    }

    public static ApiManager getInstance() {
        return instance;
    }

    public String getVersion() {
        return getPluginMeta().getVersion();
    }

    public String getApiVersion() {
        return getPluginMeta().getAPIVersion();
    }

    public List<String> getDevelopers() {
        return getPluginMeta().getAuthors();
    }

    public String getWebsite() {
        return getPluginMeta().getWebsite();
    }

    public IDatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public ServerSettingsAPIImpl getServerSettingsAPI() {
        return serverSettingsAPI;
    }

    public ChunkAPIImpl getChunkAPI() {
        return chunkAPI;
    }

    public HomeAPIImpl getHomeAPI() {
        return homeAPI;
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

    public PlayerIdentityAPIImpl getPlayerIdentityAPI() {
        return playerIdentityAPI;
    }

    public PlayerSettingsAPIImpl getPlayerSettingsAPI() {
        return playerSettingsAPI;
    }

    public EconomyAPIImpl getEconomyAPI() {
        return economyAPI;
    }

    public QuestAPIImpl getQuestAPI() {
        return questAPI;
    }

    public TimerAPIImpl getTimerAPI() {
        return timerAPI;
    }

    public void setDatabaseManager(IDatabaseManager dbm) {
        this.databaseManager = dbm;
    }
}