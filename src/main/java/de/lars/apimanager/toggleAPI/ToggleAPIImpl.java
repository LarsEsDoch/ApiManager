package de.lars.apimanager.toggleAPI;

import de.lars.apimanager.Main;
import de.lars.apimanager.database.MySQL;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ToggleAPIImpl implements IToggleAPI {
    private MySQL mySQL;

    @Override
    public void setBedToggle(Player player, boolean toggle) {
        UUID uuid = player.getUniqueId();
        if (toggle) {
            mySQL.update("UPDATE togglelist SET bedtoggle=? WHERE uuid=?", 1, uuid.toString());
        } else {
            mySQL.update("UPDATE togglelist SET bedtoggle=? WHERE uuid=?", 0, uuid.toString());
        }
    }

    @Override
    public void setScoreboardToggle(Player player, boolean toggle) {
        UUID uuid = player.getUniqueId();
        if (toggle) {
            mySQL.update("UPDATE togglelist SET scoreboardtoggle=? WHERE uuid=?", 1, uuid.toString());
        } else {
            mySQL.update("UPDATE togglelist SET scoreboardtoggle=? WHERE uuid=?", 0, uuid.toString());
        }
    }

    @Override
    public Boolean getBedToggle(Player player) {
        UUID uuid = player.getUniqueId();
        String qry = "SELECT bedtoggle FROM togglelist WHERE uuid=?";
        try (ResultSet rs = mySQL.query(qry, uuid.toString())) {
            if (rs.next()) {
                if (rs.getInt("bedtoggle") == 1) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Boolean getScoreboardToggle(Player player) {
        UUID uuid = player.getUniqueId();
        String qry = "SELECT scoreboardtoggle FROM togglelist WHERE uuid=?";
        try (ResultSet rs = mySQL.query(qry, uuid.toString())) {
            if (rs.next()) {
                return rs.getInt("scoreboardtoggle") == 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void initPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        mySQL.update("INSERT INTO togglelist (uuid, bedtoggle, scoreboardtoggle) VALUES (?,?,?)", uuid.toString(), 1, 1);
    }

    @Override
    public boolean doesUserExist(Player player) {
        UUID uuid = player.getUniqueId();
        String qry = "SELECT count(*) AS count FROM togglelist WHERE uuid=?";
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
        mySQL.update("CREATE TABLE IF NOT EXISTS togglelist (uuid VARCHAR(36), bedtoggle INT(1), scoreboardtoggle INT(1),FOREIGN KEY (uuid) REFERENCES players(uuid))");
    }
}
