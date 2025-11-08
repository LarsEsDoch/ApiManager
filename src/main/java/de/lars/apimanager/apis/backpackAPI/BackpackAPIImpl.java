package de.lars.apimanager.apis.backpackAPI;

import de.lars.apimanager.ApiManager;
import de.lars.apimanager.database.DatabaseRepository;
import de.lars.apimanager.database.IDatabaseManager;
import de.lars.apimanager.utils.Statements;
import de.lars.apimanager.utils.ValidateParameter;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.OfflinePlayer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class BackpackAPIImpl implements IBackpackAPI {
    private static final String TABLE = "player_backpacks";

    private DatabaseRepository repo() {
        return new DatabaseRepository();
    }

    private IDatabaseManager db() {
        return ApiManager.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db().update("""
            CREATE TABLE IF NOT EXISTS player_backpacks (
                uuid CHAR(36) NOT NULL PRIMARY KEY,
                data LONGBLOB,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (uuid) REFERENCES players(uuid) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);
    }

    public void initPlayer(OfflinePlayer player) {
        if (!doesUserExist(player)) {
            db().update("INSERT IGNORE INTO player_backpacks (uuid, data) VALUES (?, ?)",
                    player.getUniqueId().toString(), null);
        }
    }

    public boolean doesUserExist(OfflinePlayer player) {
        return repo().exists(TABLE, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public Instant getCreatedAt(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getInstant(TABLE, "created_at", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Instant> getCreatedAtAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getInstantAsync(TABLE, "created_at", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public Instant getUpdatedAt(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getInstant(TABLE, "updated_at", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Instant> getUpdatedAtAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getInstantAsync(TABLE, "updated_at", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setBackpack(OfflinePlayer player, String data) {
        ValidateParameter.validatePlayer(player);
        String compressed = compressToBase64(data);
        repo().updateColumn(TABLE, "data", compressed, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setBackpackAsync(OfflinePlayer player, String data) {
        ValidateParameter.validatePlayer(player);
        String compressed = compressToBase64(data);
        return repo().updateColumnAsync(TABLE, "data", compressed, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public String getBackpack(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        String base64 = repo().getString(TABLE, "data", "uuid = ?", player.getUniqueId().toString());
        return base64 == null || base64.isEmpty() ? "" : decompressFromBase64(base64);
    }

    @Override
    public CompletableFuture<String> getBackpackAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getStringAsync(TABLE, "data", "uuid = ?", player.getUniqueId().toString())
            .thenApply(base64 -> base64 == null || base64.isEmpty() ? "" : decompressFromBase64(base64));
    }

    private String compressToBase64(String input) {
        if (input == null || input.isEmpty()) return "";
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             DeflaterOutputStream dos = new DeflaterOutputStream(bos)) {
            dos.write(input.getBytes(StandardCharsets.UTF_8));
            dos.finish();
            return Base64.getEncoder().encodeToString(bos.toByteArray());
        } catch (IOException e) {
            Statements.logToConsole("Failed to compress backpack data! " + e.getMessage(), NamedTextColor.GOLD);
            return "";
        }
    }

    private String decompressFromBase64(String base64) {
        if (base64 == null || base64.isEmpty()) return "";
        byte[] compressed = Base64.getDecoder().decode(base64);
        try (ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
             InflaterInputStream iis = new InflaterInputStream(bis);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = iis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            return bos.toString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            Statements.logToConsole("Failed to decompress backpack data! " + e.getMessage(), NamedTextColor.GOLD);
            return "";
        }
    }
}