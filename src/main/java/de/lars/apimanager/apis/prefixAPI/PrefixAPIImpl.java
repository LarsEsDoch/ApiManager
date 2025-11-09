package de.lars.apimanager.apis.prefixAPI;

import de.lars.apimanager.ApiManager;
import de.lars.apimanager.database.DatabaseRepository;
import de.lars.apimanager.database.IDatabaseManager;
import de.lars.apimanager.utils.TextFormation;
import de.lars.apimanager.utils.ValidateParameter;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class PrefixAPIImpl implements IPrefixAPI {
    private static final String TABLE = "player_prefixes";

    private DatabaseRepository repo() {
        return new DatabaseRepository();
    }

    private IDatabaseManager db() {
        return ApiManager.getInstance().getDatabaseManager();
    }

    public void createTables() {
        db().update("""
            CREATE TABLE IF NOT EXISTS player_prefixes (
                uuid CHAR(36) NOT NULL PRIMARY KEY,
                color INT DEFAULT 15,
                decoration INT DEFAULT 0,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (uuid) REFERENCES players(uuid) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);
    }

    public void initPlayer(OfflinePlayer player) {
        db().update("""
            INSERT IGNORE INTO player_prefixes (uuid, color, decoration)
            VALUES (?, ?, ?)
        """, player.getUniqueId().toString(), 15, 0);
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
    public void setColor(OfflinePlayer player, NamedTextColor color) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateNamedTextColor(color);
        repo().updateColumn(TABLE, "color", TextFormation.getColorId(color), "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setColorAsync(OfflinePlayer player, NamedTextColor color) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateNamedTextColor(color);
        return repo().updateColumnAsync(TABLE, "color", TextFormation.getColorId(color), "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public NamedTextColor getColor(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        Integer colorId = repo().getInteger(TABLE, "color", "uuid = ?", player.getUniqueId().toString());
        return colorId != null ? TextFormation.getNamedTextColor(colorId) : null;
    }

    @Override
    public CompletableFuture<NamedTextColor> getColorAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getIntegerAsync(TABLE, "color", "uuid = ?", player.getUniqueId().toString())
            .thenApply(colorId -> colorId != null ? TextFormation.getNamedTextColor(colorId) : null);
    }

    @Override
    public void setDecoration(OfflinePlayer player, TextDecoration decoration) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateTextDecoration(decoration);
        setDecoration(player, Set.of(decoration));
    }

    @Override
    public CompletableFuture<Void> setDecorationAsync(OfflinePlayer player, TextDecoration decoration) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateTextDecoration(decoration);
        return setDecorationAsync(player, Set.of(decoration));
    }

    @Override
    public void setDecoration(OfflinePlayer player, Set<TextDecoration> decorations) {
        ValidateParameter.validatePlayer(player);
        int bitmask = TextFormation.combineDecorations(decorations);
        repo().updateColumn(TABLE, "decoration", bitmask, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setDecorationAsync(OfflinePlayer player, Set<TextDecoration> decorations) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateTextDecorations(decorations);
        int bitmask = TextFormation.combineDecorations(decorations);
        return repo().updateColumnAsync(TABLE, "decoration", bitmask, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void addDecoration(OfflinePlayer player, TextDecoration decoration) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateTextDecoration(decoration);
        Set<TextDecoration> current = getDecoration(player);
        if (current == null) current = new HashSet<>();
        current.add(decoration);
        int bitmask = TextFormation.combineDecorations(current);
        repo().updateColumn(TABLE, "decoration", bitmask, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> addDecorationAsync(OfflinePlayer player, TextDecoration decoration) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateTextDecoration(decoration);
        return getDecorationAsync(player).thenCompose(current -> {
            if (current == null) current = new HashSet<>();
            current.add(decoration);
            int bitmask = TextFormation.combineDecorations(current);
            return repo().updateColumnAsync(TABLE, "decoration", bitmask, "uuid = ?", player.getUniqueId().toString());
        });
    }

    @Override
    public void removeDecoration(OfflinePlayer player, TextDecoration decoration) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateTextDecoration(decoration);
        Set<TextDecoration> current = getDecoration(player);
        if (current == null) current = new HashSet<>();
        current.remove(decoration);
        int bitmask = TextFormation.combineDecorations(current);
        repo().updateColumn(TABLE, "decoration", bitmask, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> removeDecorationAsync(OfflinePlayer player, TextDecoration decoration) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateTextDecoration(decoration);
        return getDecorationAsync(player).thenCompose(current -> {
            if (current == null) current = new HashSet<>();
            current.remove(decoration);
            int bitmask = TextFormation.combineDecorations(current);
            return repo().updateColumnAsync(TABLE, "decoration", bitmask, "uuid = ?", player.getUniqueId().toString());
        });
    }

    @Override
    public Set<TextDecoration> getDecoration(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        Integer bitmask = repo().getInteger(TABLE, "decoration", "uuid = ?", player.getUniqueId().toString());
        return bitmask != null ? TextFormation.getTextDecorations(bitmask) : new HashSet<>();
    }

    @Override
    public CompletableFuture<Set<TextDecoration>> getDecorationAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getIntegerAsync(TABLE, "decoration", "uuid = ?", player.getUniqueId().toString())
            .thenApply(bitmask -> bitmask != null ? TextFormation.getTextDecorations(bitmask) : new HashSet<>());
    }
}