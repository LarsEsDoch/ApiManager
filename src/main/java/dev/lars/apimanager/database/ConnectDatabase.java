package dev.lars.apimanager.database;

import dev.lars.apimanager.ApiManager;
import dev.lars.apimanager.utils.ApiManagerStatements;
import net.kyori.adventure.text.format.NamedTextColor;

import java.net.InetSocketAddress;
import java.net.Socket;

public record ConnectDatabase(ApiManager plugin) {
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
            ApiManagerStatements.logToConsole("Database configuration contains placeholder values. Connection skipped!", NamedTextColor.GOLD);
            plugin.setDatabaseManager(new SafeDatabaseManager());
            return false;
        }

        if (!isHostReachable(host, port, 3000)) {
            ApiManagerStatements.logToConsole("Couldn't connect to " + host + " on port " + port + " with user " + user, NamedTextColor.RED);
            plugin.setDatabaseManager(new SafeDatabaseManager());
            return false;
        }

        try {
            IDatabaseManager previous = plugin.getDatabaseManager();
            if (previous != null) {
                previous.close();
            }
        } catch (Exception e) {
            ApiManagerStatements.logToConsole("Error while closing previous database manager. " + e.getMessage(), NamedTextColor.GOLD);
        }

        try {
            DatabaseManager newManager = new DatabaseManager(host, port, database, user, password);
            plugin.setDatabaseManager(newManager);
            ApiManagerStatements.logToConsole("Database manager instance created. Waiting for connection to become ready...", NamedTextColor.GRAY);
            return true;
        } catch (Exception e) {
            ApiManagerStatements.logToConsole("Couldn't instantiate DatabaseManager for " + host + ":" + port + " with user " + user + " " + e.getMessage(), NamedTextColor.GOLD);
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