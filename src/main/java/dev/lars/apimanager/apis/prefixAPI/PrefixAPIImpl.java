package dev.lars.apimanager.apis.prefixAPI;

import dev.lars.apimanager.ApiManager;
import dev.lars.apimanager.database.DatabaseRepository;
import dev.lars.apimanager.database.IDatabaseManager;
import dev.lars.apimanager.utils.ApiManagerTextFormation;
import dev.lars.apimanager.utils.ApiManagerValidateParameter;
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
                color INT NOT NULL DEFAULT 15,
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
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getInstant(TABLE, "created_at", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Instant> getCreatedAtAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getInstantAsync(TABLE, "created_at", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public Instant getUpdatedAt(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getInstant(TABLE, "updated_at", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Instant> getUpdatedAtAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getInstantAsync(TABLE, "updated_at", "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void setColor(OfflinePlayer player, NamedTextColor color) {
        ApiManagerValidateParameter.validatePlayer(player);
        ApiManagerValidateParameter.validateNamedTextColor(color);
        repo().updateColumn(TABLE, "color", ApiManagerTextFormation.getColorId(color), "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setColorAsync(OfflinePlayer player, NamedTextColor color) {
        ApiManagerValidateParameter.validatePlayer(player);
        ApiManagerValidateParameter.validateNamedTextColor(color);
        return repo().updateColumnAsync(TABLE, "color", ApiManagerTextFormation.getColorId(color), "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public NamedTextColor getColor(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        Integer colorId = repo().getInteger(TABLE, "color", "uuid = ?", player.getUniqueId().toString());
        return colorId != null ? ApiManagerTextFormation.getNamedTextColor(colorId) : null;
    }

    @Override
    public CompletableFuture<NamedTextColor> getColorAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getIntegerAsync(TABLE, "color", "uuid = ?", player.getUniqueId().toString())
            .thenApply(colorId -> colorId != null ? ApiManagerTextFormation.getNamedTextColor(colorId) : null);
    }

    @Override
    public void setDecoration(OfflinePlayer player, TextDecoration decoration) {
        ApiManagerValidateParameter.validatePlayer(player);
        if (decoration == null) {
            setDecorations(player, null);
        } else {
            setDecorations(player, Set.of(decoration));
        }
    }

    @Override
    public CompletableFuture<Void> setDecorationAsync(OfflinePlayer player, TextDecoration decoration) {
        ApiManagerValidateParameter.validatePlayer(player);
        if (decoration == null) {
            return setDecorationsAsync(player, null);
        } else {
            return setDecorationsAsync(player, Set.of(decoration));
        }
    }

    @Override
    public void setDecorations(OfflinePlayer player, Set<TextDecoration> decorations) {
        ApiManagerValidateParameter.validatePlayer(player);
        if (decorations == null) {
            decorations = Collections.emptySet();
        }
        int bitmask = ApiManagerTextFormation.combineDecorations(decorations);
        repo().updateColumn(TABLE, "decorations", bitmask, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setDecorationsAsync(OfflinePlayer player, Set<TextDecoration> decorations) {
        ApiManagerValidateParameter.validatePlayer(player);
        if (decorations == null) {
            decorations = Collections.emptySet();
        }
        int bitmask = ApiManagerTextFormation.combineDecorations(decorations);
        return repo().updateColumnAsync(TABLE, "decorations", bitmask, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public void addDecoration(OfflinePlayer player, TextDecoration decoration) {
        ApiManagerValidateParameter.validatePlayer(player);
        ApiManagerValidateParameter.validateTextDecoration(decoration);
        Set<TextDecoration> current = getDecorations(player);
        if (current == null) current = new HashSet<>();
        current.add(decoration);
        int bitmask = ApiManagerTextFormation.combineDecorations(current);
        repo().updateColumn(TABLE, "decorations", bitmask, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> addDecorationAsync(OfflinePlayer player, TextDecoration decoration) {
        ApiManagerValidateParameter.validatePlayer(player);
        ApiManagerValidateParameter.validateTextDecoration(decoration);
        return getDecorationsAsync(player).thenCompose(current -> {
            if (current == null) current = new HashSet<>();
            current.add(decoration);
            int bitmask = ApiManagerTextFormation.combineDecorations(current);
            return repo().updateColumnAsync(TABLE, "decorations", bitmask, "uuid = ?", player.getUniqueId().toString());
        });
    }

    @Override
    public void removeDecoration(OfflinePlayer player, TextDecoration decoration) {
        ApiManagerValidateParameter.validatePlayer(player);
        ApiManagerValidateParameter.validateTextDecoration(decoration);
        Set<TextDecoration> current = getDecorations(player);
        if (current == null) current = new HashSet<>();
        current.remove(decoration);
        int bitmask = ApiManagerTextFormation.combineDecorations(current);
        repo().updateColumn(TABLE, "decorations", bitmask, "uuid = ?", player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> removeDecorationAsync(OfflinePlayer player, TextDecoration decoration) {
        ApiManagerValidateParameter.validatePlayer(player);
        ApiManagerValidateParameter.validateTextDecoration(decoration);
        return getDecorationsAsync(player).thenCompose(current -> {
            if (current == null) current = new HashSet<>();
            current.remove(decoration);
            int bitmask = ApiManagerTextFormation.combineDecorations(current);
            return repo().updateColumnAsync(TABLE, "decorations", bitmask, "uuid = ?", player.getUniqueId().toString());
        });
    }

    @Override
    public Set<TextDecoration> getDecorations(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        Integer bitmask = repo().getInteger(TABLE, "decorations", "uuid = ?", player.getUniqueId().toString());
        return bitmask != null ? ApiManagerTextFormation.getTextDecorations(bitmask) : new HashSet<>();
    }

    @Override
    public CompletableFuture<Set<TextDecoration>> getDecorationsAsync(OfflinePlayer player) {
        ApiManagerValidateParameter.validatePlayer(player);
        return repo().getIntegerAsync(TABLE, "decorations", "uuid = ?", player.getUniqueId().toString())
            .thenApply(bitmask -> bitmask != null ? ApiManagerTextFormation.getTextDecorations(bitmask) : new HashSet<>());
    }
}