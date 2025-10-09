package de.lars.apiManager.languageAPI;

import de.lars.apiManager.Main;
import de.lars.apiManager.database.MySQL;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class LanguageAPIImpl implements ILanguageAPI {
    private MySQL mySQL;

    @Override
    public void setLanguage(Player player, Integer languageId) {
        UUID uuid = player.getUniqueId();
        mySQL.updateAsync("UPDATE languages SET language_id=? WHERE uuid=?", languageId, uuid.toString());
    }

    @Override
    public Integer getLanguage(Player player) {
        UUID uuid = player.getUniqueId();
        String qry = "SELECT language_id FROM languages WHERE uuid=?";
        try (ResultSet rs = mySQL.query(qry, uuid.toString())) {
            if (rs.next()) {
                return rs.getInt("language_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void initPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        mySQL.updateAsync("INSERT INTO languages (uuid, language_id) VALUES (?,?)", uuid.toString(), 1);
    }

    @Override
    public boolean doesUserExist(Player player) {
        UUID uuid = player.getUniqueId();
        String qry = "SELECT count(*) AS count FROM languages WHERE uuid=?";
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
        mySQL.update("CREATE TABLE IF NOT EXISTS languages (uuid VARCHAR(36), language_id INT(35),FOREIGN KEY (uuid) REFERENCES players(uuid))");
    }
}
