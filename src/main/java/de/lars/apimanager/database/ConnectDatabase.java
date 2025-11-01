package de.lars.apimanager.database;

import de.lars.apimanager.ApiManager;

import java.net.InetSocketAddress;
import java.net.Socket;

public class ConnectDatabase{
    private final ApiManager plugin;

    public ConnectDatabase(ApiManager plugin) {
        this.plugin = plugin;
    }

    public boolean loadDatabaseConfig() {
        String host = plugin.getConfig().getString("database.host");
        int port = plugin.getConfig().getInt("database.port");
        String database = plugin.getConfig().getString("database.database");
        String user = plugin.getConfig().getString("database.username");
        String password = plugin.getConfig().getString("database.password");

        if (host == null || host.isEmpty() || host.equalsIgnoreCase("Enter the IP of your database") ||
            database == null || database.isEmpty() || database.equalsIgnoreCase("Enter the name of the database") ||
            user == null || user.isEmpty() || user.equalsIgnoreCase("Enter the database user") ||
            password == null || password.isEmpty() || password.equalsIgnoreCase("Enter the password of the user") ||
            port <= 0) {
            plugin.getLogger().warning("Database configuration contains placeholder values. Connection skipped!");
            plugin.databaseManager = new SafeDatabaseManager();
            return false;
        }

        if (!isHostReachable(host, port, 3000)) {
            plugin.getLogger().warning("Couldn't connect to " + host + " on port " + port + " with user " + user);
            plugin.databaseManager = new SafeDatabaseManager();
            return false;
        }

        if (plugin.databaseManager != null) {
            plugin.databaseManager.close();
        }

        try {
            plugin.databaseManager = new DatabaseManager(host, port, database, user, password);
            plugin.getLogger().info("Database successfully connected!");
            return true;
        } catch (Exception e) {
            plugin.getLogger().warning("Couldn't connect to " + host + " on port " + port + " with user " + user);
            plugin.databaseManager = new SafeDatabaseManager();
            return false;
        }
    }

    private boolean isHostReachable(String host, int port, int timeoutMs) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeoutMs);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}