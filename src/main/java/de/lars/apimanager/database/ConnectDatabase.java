package de.lars.apimanager.database;

import de.lars.apimanager.ApiManager;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Level;

public class ConnectDatabase {
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
            plugin.setDatabaseManager(new SafeDatabaseManager());
            return false;
        }

        if (!isHostReachable(host, port, 3000)) {
            plugin.getLogger().warning("Couldn't connect to " + host + " on port " + port + " with user " + user);
            plugin.setDatabaseManager(new SafeDatabaseManager());
            return false;
        }

        try {
            IDatabaseManager previous = plugin.getDatabaseManager();
            if (previous != null) {
                previous.close();
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error while closing previous database manager.", e);
        }

        try {
            DatabaseManager newManager = new DatabaseManager(host, port, database, user, password);
            plugin.setDatabaseManager(newManager);
            plugin.getLogger().info("Database manager instance created. Waiting for connection to become ready...");
            return true;
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Couldn't instantiate DatabaseManager for " + host + ":" + port + " with user " + user, e);
            plugin.setDatabaseManager(new SafeDatabaseManager());
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