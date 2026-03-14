package dev.lars.apimanager.database;

import dev.lars.apimanager.ApiManager;
import dev.lars.apimanager.utils.ApiManagerStatements;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public record ConnectDatabase(ApiManager plugin) {

    public boolean loadDatabaseConfig() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        YamlConfiguration config = new YamlConfiguration();

        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            ApiManagerStatements.logToConsole("config.yml is damaged or invalid. Safe mode has been enabled.", NamedTextColor.RED);
            plugin.setDatabaseManager(new SafeDatabaseManager());
            return false;
        }

        String host = config.getString("database.host");
        int port = config.getInt("database.port");
        String database = config.getString("database.database");
        String user = config.getString("database.username");
        String password = config.getString("database.password");

        if (host == null || host.isEmpty() || host.equalsIgnoreCase("Enter the IP of your database") ||
                database == null || database.isEmpty() || database.equalsIgnoreCase("Enter the name of the database") ||
                user == null || user.isEmpty() || user.equalsIgnoreCase("Enter the database user") ||
                password == null || password.isEmpty() || password.equalsIgnoreCase("Enter the password of the user") ||
                port <= 0) {
            ApiManagerStatements.logToConsole("Database configuration contains placeholder values. Connection skipped!", NamedTextColor.GOLD);
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
}