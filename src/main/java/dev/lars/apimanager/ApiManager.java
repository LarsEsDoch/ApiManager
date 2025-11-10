package de.lars.apimanager;

import de.lars.apimanager.apis.backpackAPI.BackpackAPI;
import de.lars.apimanager.apis.backpackAPI.BackpackAPIImpl;
import de.lars.apimanager.apis.banAPI.BanAPI;
import de.lars.apimanager.apis.banAPI.BanAPIImpl;
import de.lars.apimanager.apis.chunkAPI.ChunkAPI;
import de.lars.apimanager.apis.chunkAPI.ChunkAPIImpl;
import de.lars.apimanager.apis.courtAPI.CourtAPI;
import de.lars.apimanager.apis.courtAPI.CourtAPIImpl;
import de.lars.apimanager.apis.economyAPI.EconomyAPI;
import de.lars.apimanager.apis.economyAPI.EconomyAPIImpl;
import de.lars.apimanager.apis.homeAPI.HomeAPI;
import de.lars.apimanager.apis.homeAPI.HomeAPIImpl;
import de.lars.apimanager.apis.languageAPI.LanguageAPI;
import de.lars.apimanager.apis.languageAPI.LanguageAPIImpl;
import de.lars.apimanager.apis.limitAPI.LimitAPI;
import de.lars.apimanager.apis.limitAPI.LimitAPIImpl;
import de.lars.apimanager.apis.nickAPI.NickAPI;
import de.lars.apimanager.apis.nickAPI.NickAPIImpl;
import de.lars.apimanager.apis.playerAPI.PlayerAPI;
import de.lars.apimanager.apis.playerAPI.PlayerAPIImpl;
import de.lars.apimanager.apis.playerSettingsAPI.PlayerSettingsAPI;
import de.lars.apimanager.apis.playerSettingsAPI.PlayerSettingsAPIImpl;
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
import de.lars.apimanager.commands.ApiManagerCommand;
import de.lars.apimanager.database.ConnectDatabase;
import de.lars.apimanager.database.DatabaseManager;
import de.lars.apimanager.database.IDatabaseManager;
import de.lars.apimanager.listeners.JoinListener;
import de.lars.apimanager.listeners.QuitListener;
import de.lars.apimanager.utils.Statements;
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
    private NickAPIImpl nickAPI;
    private PlayerSettingsAPIImpl playerSettingsAPI;
    private EconomyAPIImpl economyAPI;
    private QuestAPIImpl questAPI;
    private TimerAPIImpl timerAPI;

    private final List<Runnable> createTableRunnables = new ArrayList<>();

    @Override
    public void onLoad() {
        instance = this;
        saveDefaultConfig();
        connectDatabase = new ConnectDatabase(this);

        if (!connectDatabase.loadDatabaseConfig()) {
            Statements.logToConsole("Invalid database configuration. Please adjust config.yml!", NamedTextColor.RED);
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
             Statements.logToConsole("Database not connected. Skipping table creation. APIs will run in safe mode (no DB).", NamedTextColor.GOLD);
        }

        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new QuitListener(), this);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            ApiManagerCommand apiManagerCommand = new ApiManagerCommand(this, connectDatabase);

            commands.register("apimanager", "ApiManager commands", apiManagerCommand);
            commands.register("am", "ApiManager commands", apiManagerCommand);
        });

        Statements.logToConsole("UtilsManager enabled!", NamedTextColor.DARK_GREEN);
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

        nickAPI = new NickAPIImpl();
        NickAPI.setApi(nickAPI);

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
        createTableRunnable.add(() -> nickAPI.createTables());
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
                Statements.logToConsole("createTables() failed for one API: " + e.getMessage(), NamedTextColor.GOLD);
            }
        }
    }

    private void onApisReady() {
        serverSettingsAPI.setServerOnline(true);
        Statements.logToConsole("All APIs are ready!", NamedTextColor.DARK_GREEN);
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
            Statements.logToConsole("Database successfully disconnected!", NamedTextColor.GREEN);
        }

        Statements.logToConsole("UtilsManager successfully disabled!", NamedTextColor.DARK_RED);
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

    public NickAPIImpl getNickAPI() {
        return nickAPI;
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