package de.lars.apimanager.apis.prefixAPI;

import de.lars.apimanager.ApiManager;
import de.lars.apimanager.database.IDatabaseManager;
import de.lars.apimanager.utils.TextFormation;
import de.lars.apimanager.utils.ValidateParameter;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class PrefixAPIImpl implements IPrefixAPI {
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
        return db().query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM player_prefixes WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        });
    }

    @Override
    public Instant getCreatedAt(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db().query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT created_at FROM player_prefixes WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("created_at").toInstant();
                    return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Instant> getCreatedAtAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db().queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT created_at FROM player_prefixes WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("created_at").toInstant();
                    return null;
                }
            }
        });
    }

    @Override
    public Instant getUpdatedAt(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db().query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT updated_at FROM player_prefixes WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("updated_at").toInstant();
                    return null;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Instant> getUpdatedAtAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db().queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT updated_at FROM player_prefixes WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getTimestamp("updated_at").toInstant();
                    return null;
                }
            }
        });
    }

    @Override
    public void setColor(OfflinePlayer player, NamedTextColor color) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateNamedTextColor(color);
        db().update("UPDATE player_prefixes SET color = ? WHERE uuid = ? LIMIT 1",
                TextFormation.getColorId(color), player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setColorAsync(OfflinePlayer player, NamedTextColor color) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateNamedTextColor(color);
        return db().updateAsync("UPDATE player_prefixes SET color = ? WHERE uuid = ? LIMIT 1",
                        TextFormation.getColorId(color), player.getUniqueId().toString());
    }

    @Override
    public NamedTextColor getColor(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return TextFormation.getNamedTextColor(Objects.requireNonNull(db().query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT color FROM player_prefixes WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int color = rs.getInt("color");
                        return rs.wasNull() ? null : color;
                    } else {
                        return null;
                    }
                }
            }
        })));
    }

    @Override
    public CompletableFuture<NamedTextColor> getColorAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db().queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT color FROM player_prefixes WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int color = rs.getInt("color");
                        return rs.wasNull() ? null : color;
                    }
                    return null;
                }
            }
        }).thenApply(colorID -> colorID != null ? TextFormation.getNamedTextColor(colorID) : null);
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
        db().update("UPDATE player_prefixes SET decoration=? WHERE uuid=?", bitmask, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> setDecorationAsync(OfflinePlayer player, Set<TextDecoration> decorations) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateTextDecorations(decorations);
        int bitmask = TextFormation.combineDecorations(decorations);
        return db().updateAsync("UPDATE player_prefixes SET decoration=? WHERE uuid=?", bitmask, player.getUniqueId().toString());
    }

    @Override
    public void addDecoration(OfflinePlayer player, TextDecoration decoration) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateTextDecoration(decoration);
        Set<TextDecoration> current = getDecoration(player);
        if (current == null) current = new HashSet<>();

        current.add(decoration);

        int bitmask = TextFormation.combineDecorations(current);
        db().update("UPDATE player_prefixes SET decoration=? WHERE uuid=?", bitmask, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> addDecorationAsync(OfflinePlayer player, TextDecoration decoration) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateTextDecoration(decoration);
        return getDecorationAsync(player).thenCompose(current -> {
            if (current == null) current = new HashSet<>();
            current.add(decoration);
            int bitmask = TextFormation.combineDecorations(current);
            return db().updateAsync("UPDATE player_prefixes SET decoration=? WHERE uuid=?", bitmask, player.getUniqueId().toString());
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
        db().update("UPDATE player_prefixes SET decoration=? WHERE uuid=?", bitmask, player.getUniqueId().toString());
    }

    @Override
    public CompletableFuture<Void> removeDecorationAsync(OfflinePlayer player, TextDecoration decoration) {
        ValidateParameter.validatePlayer(player);
        ValidateParameter.validateTextDecoration(decoration);
        return getDecorationAsync(player).thenCompose(current -> {
            if (current == null) current = new HashSet<>();
            current.remove(decoration);
            int bitmask = TextFormation.combineDecorations(current);
            return db().updateAsync("UPDATE player_prefixes SET decoration=? WHERE uuid=?", bitmask, player.getUniqueId().toString());
        });
    }

    @Override
    public Set<TextDecoration> getDecoration(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db().query(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT decoration FROM player_prefixes WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int bitmask = rs.getInt("decoration");
                        return TextFormation.getTextDecorations(bitmask);
                    }
                }
            }
            return new HashSet<>();
        });
    }

    @Override
    public CompletableFuture<Set<TextDecoration>> getDecorationAsync(OfflinePlayer player) {
        ValidateParameter.validatePlayer(player);
        return db().queryAsync(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT decoration FROM player_prefixes WHERE uuid = ? LIMIT 1")) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int bitmask = rs.getInt("decoration");
                        return TextFormation.getTextDecorations(bitmask);
                    }
                }
                return new HashSet<>();
            }
        });
    }
}