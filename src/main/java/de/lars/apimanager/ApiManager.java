package de.lars.apimanager;

import de.lars.apimanager.apis.backpackAPI.BackpackAPI;
import de.lars.apimanager.apis.backpackAPI.BackpackAPIImpl;
import de.lars.apimanager.apis.banAPI.BanAPI;
import de.lars.apimanager.apis.banAPI.BanAPIImpl;
import de.lars.apimanager.apis.chunkAPI.ChunkAPI;
import de.lars.apimanager.apis.chunkAPI.ChunkAPIImpl;
import de.lars.apimanager.apis.coinAPI.CoinAPI;
import de.lars.apimanager.apis.coinAPI.CoinAPIImpl;
import de.lars.apimanager.apis.courtAPI.CourtAPI;
import de.lars.apimanager.apis.courtAPI.CourtAPIImpl;
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
import de.lars.apimanager.commands.ReloadCommand;
import de.lars.apimanager.database.ConnectDatabase;
import de.lars.apimanager.database.DatabaseManager;
import de.lars.apimanager.database.IDatabaseManager;
import de.lars.apimanager.listeners.JoinListener;
import de.lars.apimanager.listeners.QuitListener;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
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
    private ToggleAPIImpl toggleAPI;
    private CoinAPIImpl coinAPI;
    private QuestAPIImpl questAPI;
    private TimerAPIImpl timerAPI;

    private final List<Runnable> createTableRunnables = new ArrayList<>();

    @Override
    public void onLoad() {
        instance = this;
        saveDefaultConfig();
        connectDatabase = new ConnectDatabase(this);

        if (!connectDatabase.loadDatabaseConfig()) {
            getLogger().warning("Invalid database configuration. Please adjust config.yml!");
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
            getLogger().warning("Database not connected — skipping table creation. APIs will run in safe mode (no DB).");
        }

        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new QuitListener(), this);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            ApiManagerCommand apiManagerCommand = new ApiManagerCommand(this, connectDatabase);

            commands.register("apimanager", "ApiManager commands", apiManagerCommand);
        });
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

        toggleAPI = new ToggleAPIImpl();
        ToggleAPI.setApi(toggleAPI);

        coinAPI = new CoinAPIImpl();
        CoinAPI.setApi(coinAPI);

        questAPI = new QuestAPIImpl();
        QuestAPI.setApi(questAPI);

        timerAPI = new TimerAPIImpl();
        TimerAPI.setApi(timerAPI);
    }

    private void buildCreateTableList() {
        createTableRunnables.clear();
        createTableRunnables.add(() -> serverSettingsAPI.createTables());
        createTableRunnables.add(() -> chunkAPI.createTables());
        createTableRunnables.add(() -> homeAPI.createTables());
        createTableRunnables.add(() -> playerAPI.createTables());
        createTableRunnables.add(() -> languageAPI.createTables());
        createTableRunnables.add(() -> backpackAPI.createTables());
        createTableRunnables.add(() -> limitAPI.createTables());
        createTableRunnables.add(() -> banAPI.createTables());
        createTableRunnables.add(() -> courtAPI.createTables());
        createTableRunnables.add(() -> rankAPI.createTables());
        createTableRunnables.add(() -> prefixAPI.createTables());
        createTableRunnables.add(() -> statusAPI.createTables());
        createTableRunnables.add(() -> nickAPI.createTables());
        createTableRunnables.add(() -> toggleAPI.createTables());
        createTableRunnables.add(() -> coinAPI.createTables());
        createTableRunnables.add(() -> questAPI.createTables());
        createTableRunnables.add(() -> timerAPI.createTables());
    }

    private void createAllTables() {
        for (Runnable r : createTableRunnables) {
            try {
                r.run();
            } catch (Exception e) {
                getLogger().warning("createTables() failed for one API: " + e.getMessage());
                getLogger().throwing(getClass().getName(), "createTables", e);
            }
        }
    }

    private void onApisReady() {
        serverSettingsAPI.setServerOnline(true);
        Component message = Component.text()
                .append(Component.text("[", NamedTextColor.DARK_GRAY))
                .append(Component.text("ApiManager", NamedTextColor.GOLD))
                .append(Component.text("]", NamedTextColor.DARK_GRAY))
                .append(Component.text(" All APIs are ready!", NamedTextColor.DARK_GREEN))
                .build();
        Bukkit.getConsoleSender().sendMessage(message);
        getLogger().info("APIs are ready (tables created).");
    }

    public synchronized void reinitializeApisAfterDbReconnect() {
        if (!(this.getDatabaseManager() instanceof DatabaseManager)) {
            getLogger().warning("Database not a real DatabaseManager after reload — skipping API reinitialization.");
            return;
        }

        instantiateApis();

        DatabaseManager dbm = (DatabaseManager) getDatabaseManager();
        dbm.readyFuture().thenRun(() -> Bukkit.getScheduler().runTask(this, () -> {
            createAllTables();
            getLogger().info("APIs reinitialized to use the new database connection.");
        }));
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
        }

        Component message = Component.text()
                .append(Component.text("[", NamedTextColor.DARK_GRAY))
                .append(Component.text("ApiManager", NamedTextColor.GOLD))
                .append(Component.text("]", NamedTextColor.DARK_GRAY))
                .append(Component.text(" Database successfully disconnected!", NamedTextColor.DARK_GREEN))
                .build();

        Bukkit.getConsoleSender().sendMessage(message);
    }

    public static ApiManager getInstance() {
        return instance;
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

    public void setDatabaseManager(IDatabaseManager dbm) {
        this.databaseManager = dbm;
    }
}