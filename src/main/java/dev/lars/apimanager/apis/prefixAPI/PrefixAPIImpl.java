package dev.lars.apimanager.apis.prefixAPI;

import dev.lars.apimanager.ApiManager;
import dev.lars.apimanager.database.DatabaseRepository;
import dev.lars.apimanager.database.IDatabaseManager;
import dev.lars.apimanager.utils.TextFormation;
import dev.lars.apimanager.utils.ValidateParameter;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.Collections;
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
                decorations INT NOT NULL DEFAULT 0,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (uuid) REFERENCES players(uuid) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """);
    }

    public void initPlayer(OfflinePlayer player) {
        db().update("""
            INSERT IGNORE INTO player_prefixes (uuid, color, decorations)
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
        setDecorations(player, Set.of(decoration));
    }

    @Override
    public CompletableFuture<Void> setDecorationAsync(OfflinePlayer player, TextDecoration decoration) {
        ValidateParameter.validatePlayer(player);
        return setDecorationsAsync(player, Set.of(decoration));
    }

    @Override
    public void setDecorations(OfflinePlayer player, Set<TextDecoration> decorations) {
        ValidateParameter.validatePlayer(player);
        if (decorations == null) {
            decorations = Collections.emptySet();
        }
        int bitmask = TextFormation.combineDecorations(decorations);
        repo().updateColumn(TABLE, "decorations", bitmask, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setDecorationsAsync(OfflinePlayer player, Set<TextDecoration> decorations) {
        ValidateParameter.validatePlayer(player);
        if (decorations == null) {
            decorations = Collections.emptySet();
        }
        int bitmask = TextFormation.combineDecorations(decorations);
        return repo().updateColumnAsync(TABLE, "decorations", bitmask, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void addDecoration(OfflinePlayer player, TextDecoration decoration) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateTextDecoration(decoration);
        Set<TextDecoration> current = getDecorations(player);
        if (current == null) current = new HashSet<>();
        current.add(decoration);
        int bitmask = TextFormation.combineDecorations(current);
        repo().updateColumn(TABLE, "decorations", bitmask, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> addDecorationAsync(OfflinePlayer player, TextDecoration decoration) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateTextDecoration(decoration);
        return getDecorationsAsync(player).thenCompose(current -> {
            if (current == null) current = new HashSet<>();
            current.add(decoration);
            int bitmask = TextFormation.combineDecorations(current);
            return repo().updateColumnAsync(TABLE, "decorations", bitmask, "uuid = ?", player.getUniqueId().toString());
        });
    }

    @Override
    public void removeDecoration(OfflinePlayer player, TextDecoration decoration) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateTextDecoration(decoration);
        Set<TextDecoration> current = getDecorations(player);
        if (current == null) current = new HashSet<>();
        current.remove(decoration);
        int bitmask = TextFormation.combineDecorations(current);
        repo().updateColumn(TABLE, "decorations", bitmask, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> removeDecorationAsync(OfflinePlayer player, TextDecoration decoration) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateTextDecoration(decoration);
        return getDecorationsAsync(player).thenCompose(current -> {
            if (current == null) current = new HashSet<>();
            current.remove(decoration);
            int bitmask = TextFormation.combineDecorations(current);
            return repo().updateColumnAsync(TABLE, "decorations", bitmask, "uuid = ?", player.getUniqueId().toString());
        });
    }

    @Override
    public Set<TextDecoration> getDecorations(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        Integer bitmask = repo().getInteger(TABLE, "decorations", "uuid = ?", player.getUniqueId().toString());
        return bitmask != null ? TextFormation.getTextDecorations(bitmask) : new HashSet<>();
    }

    @Override
    public CompletableFuture<Set<TextDecoration>> getDecorationsAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return repo().getIntegerAsync(TABLE, "decorations", "uuid = ?", player.getUniqueId().toString())
            .thenApply(bitmask -> bitmask != null ? TextFormation.getTextDecorations(bitmask) : new HashSet<>());
    }
}