package dev.lars.apimanager.apis.serverStateAPI;

import dev.lars.apimanager.ApiManager;
import dev.lars.apimanager.database.DatabaseRepository;
import dev.lars.apimanager.database.IDatabaseManager;
import dev.lars.apimanager.utils.ApiManagerFormatLocation;
import dev.lars.apimanager.utils.ApiManagerValidateParameter;
import org.bukkit.Location;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ServerStateAPIImpl implements IServerStateAPI {
    private static final String TABLE = "server_state";

    private String serverId() {
        return ApiManager.getServerId();
    }

    private String where() {
        return "server_id = '" + serverId() + "'";
    }

    private DatabaseRepository repo() {
        return new DatabaseRepository();
    }

    private IDatabaseManager db() {
        return ApiManager.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db().update(String.format("""
            CREATE TABLE IF NOT EXISTS %s (
                server_id VARCHAR(64) NOT NULL PRIMARY KEY,
                is_server_online BOOLEAN NOT NULL DEFAULT FALSE,
                max_players INT NOT NULL DEFAULT 20,
                server_name VARCHAR(255) NOT NULL DEFAULT 'A Minecraft Server',
                server_name_startcolor CHAR(7) NOT NULL DEFAULT '#ffffff',
                server_name_endcolor CHAR(7) NOT NULL DEFAULT '#ffffff',
                server_version VARCHAR(50) NOT NULL DEFAULT '1.21.11',
                spawn_location VARCHAR(255) DEFAULT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """, TABLE));

        repo().insertIgnore(TABLE, new String[]{"server_id"}, serverId());
    }

    @Override
    public Instant getCreatedAt() {
        return repo().getInstant(TABLE, "created_at", where());
    }

    @Override
    public CompletableFuture<Instant> getCreatedAtAsync() {
        return repo().getInstantAsync(TABLE, "created_at", where());
    }

    @Override
    public Instant getUpdatedAt() {
        return repo().getInstant(TABLE, "updated_at", where());
    }

    @Override
    public CompletableFuture<Instant> getUpdatedAtAsync() {
        return repo().getInstantAsync(TABLE, "updated_at", where());
    }

    @Override
    public void setServerOnline(boolean online) {
        repo().updateColumn(TABLE, "is_server_online", online, where());
    }

    @Override
    public CompletableFuture<Void> setServerOnlineAsync(boolean online) {
        return repo().updateColumnAsync(TABLE, "is_server_online", online, where());
    }

    @Override
    public boolean isServerOnline() {
        Boolean result = repo().getBoolean(TABLE, "is_server_online", where());
        return result != null && result;
    }

    @Override
    public CompletableFuture<Boolean> isServerOnlineAsync() {
        return repo().getBooleanAsync(TABLE, "is_server_online", where())
            .thenApply(result -> result != null && result);
    }

    @Override
    public void setMaxPlayers(int maxPlayers) {
        repo().updateColumn(TABLE, "max_players", maxPlayers, where());
    }

    @Override
    public CompletableFuture<Void> setMaxPlayersAsync(int maxPlayers) {
        return repo().updateColumnAsync(TABLE, "max_players", maxPlayers, where());
    }

    @Override
    public Integer getMaxPlayers() {
        return repo().getInteger(TABLE, "max_players", where());
    }

    @Override
    public CompletableFuture<Integer> getMaxPlayersAsync() {
        return repo().getIntegerAsync(TABLE, "max_players", where());
    }

    @Override
    public void setServerName(String serverName) {
        ApiManagerValidateParameter.validateServerName(serverName);
        repo().updateColumn(TABLE, "server_name", serverName, where());
    }

    @Override
    public CompletableFuture<Void> setServerNameAsync(String serverName) {
        ApiManagerValidateParameter.validateServerName(serverName);
        return repo().updateColumnAsync(TABLE, "server_name", serverName, where());
    }

    @Override
    public String getServerName() {
        return repo().getString(TABLE, "server_name", where());
    }

    @Override
    public CompletableFuture<String> getServerNameAsync() {
        return repo().getStringAsync(TABLE, "server_name", where());
    }

    @Override
    public void setServerGradient(List<String> serverNameColors) {
        ApiManagerValidateParameter.validateServerNameColors(serverNameColors);
        repo().updateColumns(TABLE,
            new String[]{"server_name_startcolor", "server_name_endcolor"},
            new Object[]{serverNameColors.getFirst(), serverNameColors.getLast()},
            where());
    }

    @Override
    public CompletableFuture<Void> setServerGradientAsync(List<String> serverNameColors) {
        ApiManagerValidateParameter.validateServerNameColors(serverNameColors);
        return repo().updateColumnsAsync(TABLE,
            new String[]{"server_name_startcolor", "server_name_endcolor"},
            new Object[]{serverNameColors.getFirst(), serverNameColors.getLast()},
            where());
    }

    @Override
    public List<String> getServerNameGradient() {
        return List.of(repo().getString(TABLE, "server_name_startcolor", where()), repo().getString(TABLE, "server_name_endcolor", where()));
    }

    @Override
    public CompletableFuture<List<String>> getServerNameGradientAsync() {
        CompletableFuture<String> startColor =
                repo().getStringAsync(TABLE, "server_name_startcolor", where());

        CompletableFuture<String> endColor =
                repo().getStringAsync(TABLE, "server_name_endcolor", where());

        return startColor.thenCombine(endColor, List::of
        );
    }

    @Override
    public void setServerVersion(String serverVersion) {
        ApiManagerValidateParameter.validateServerVersion(serverVersion);
        repo().updateColumn(TABLE, "server_version", serverVersion, where());
    }

    @Override
    public CompletableFuture<Void> setServerVersionAsync(String serverVersion) {
        ApiManagerValidateParameter.validateServerVersion(serverVersion);
        return repo().updateColumnAsync(TABLE, "server_version", serverVersion, where());
    }

    @Override
    public String getServerVersion() {
        return repo().getString(TABLE, "server_version", where());
    }

    @Override
    public CompletableFuture<String> getServerVersionAsync() {
        return repo().getStringAsync(TABLE, "server_version", where());
    }

    @Override
    public void setSpawnLocation(Location location) {
        ApiManagerValidateParameter.validateLocation(location);
        repo().updateColumn(TABLE, "spawn_location", ApiManagerFormatLocation.serializeLocation(location), where());
    }

    @Override
    public CompletableFuture<Void> setSpawnLocationAsync(Location location) {
        ApiManagerValidateParameter.validateLocation(location);
        return repo().updateColumnAsync(TABLE, "spawn_location", ApiManagerFormatLocation.serializeLocation(location), where());
    }

    @Override
    public Location getSpawnLocation() {
        String locData = repo().getString(TABLE, "spawn_location", where());
        return ApiManagerFormatLocation.deserializeLocation(locData);
    }

    @Override
    public CompletableFuture<Location> getSpawnLocationAsync() {
        return repo().getStringAsync(TABLE, "spawn_location", where())
            .thenApply(ApiManagerFormatLocation::deserializeLocation);
    }
}
