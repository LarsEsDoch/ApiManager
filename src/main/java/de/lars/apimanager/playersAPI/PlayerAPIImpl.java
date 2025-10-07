package de.lars.apimanager.playersAPI;

import de.lars.apimanager.Main;
import de.lars.apimanager.database.MySQL;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerAPIImpl implements IPlayerAPI {

    private MySQL mySQL;

    @Override
    public void setPlaytime(Player player, Integer playtime) {
        UUID uuid = player.getUniqueId();
        mySQL.update("UPDATE players SET playtime=? WHERE uuid=?", playtime, uuid.toString());
    }

    @Override
    public Integer getPlaytime(Player player) {
        UUID uuid = player.getUniqueId();
        String qry = "SELECT playtime FROM players WHERE uuid=?";
        try (ResultSet rs = mySQL.query(qry, uuid.toString())) {
            if (rs.next()) {
                return rs.getInt("playtime");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void initPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        mySQL.update("INSERT INTO players (uuid, playtime, free_chunks) VALUES (?,?,?)", uuid.toString(), 0, 32);
    }

    @Override
    public boolean doesUserExist(Player player) {
        UUID uuid = player.getUniqueId();
        String qry = "SELECT count(*) AS count FROM players WHERE uuid=?";
        try (ResultSet rs = mySQL.query(qry, uuid.toString())) {
            if (rs.next()) {
                return rs.getInt("count") != 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void createTables() {
        this.mySQL = Main.getInstance().getMySQL();
        mySQL.update("CREATE TABLE IF NOT EXISTS players (uuid VARCHAR(36),playtime INT(255), free_chunks INT(255), PRIMARY KEY (uuid))");
    }
}
