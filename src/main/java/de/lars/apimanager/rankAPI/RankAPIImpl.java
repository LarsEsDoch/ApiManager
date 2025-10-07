package de.lars.apimanager.rankAPI;

import de.lars.apimanager.Main;
import de.lars.apimanager.database.MySQL;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.UUID;

public class RankAPIImpl implements IRankAPI {
    private MySQL mySQL;

    @Override
    public void setRankID(Player player, int rang, int time, Calendar date) {
        UUID uuid = player.getUniqueId();
        mySQL.update("UPDATE raenge SET rang=? WHERE uuid=?", rang, uuid.toString());
        mySQL.update("UPDATE raenge SET time=? WHERE uuid=?", time, uuid.toString());
        mySQL.update("UPDATE raenge SET dated=? WHERE uuid=?", date.get(Calendar.DAY_OF_MONTH), uuid.toString());
        mySQL.update("UPDATE raenge SET datem=? WHERE uuid=?", date.get(Calendar.MONTH) + 1, uuid.toString());
        mySQL.update("UPDATE raenge SET datey=? WHERE uuid=?", date.get(Calendar.YEAR), uuid.toString());
    }

    @Override
    public void addRankDays(Player player, int days) {
        UUID uuid = player.getUniqueId();
        mySQL.update("UPDATE raenge SET time=? WHERE uuid=?", days, uuid.toString());
    }

    @Override
    public void setPrefix(Player player, int count) {
        UUID uuid = player.getUniqueId();
        mySQL.update("UPDATE raenge SET prefix=? WHERE uuid=?", count, uuid.toString());
    }

    @Override
    public void setPrefixType(Player player, int count) {
        UUID uuid = player.getUniqueId();
        mySQL.update("UPDATE raenge SET prefixtype=? WHERE uuid=?", count, uuid.toString());
    }

    @Override
    public void setStatus(Player player, String status) {
        UUID uuid = player.getUniqueId();
        mySQL.update("UPDATE raenge SET status=? WHERE uuid=?", status, uuid.toString());
    }


    @Override
    public Integer getRankID(Player player) {
        UUID uuid = player.getUniqueId();
        String qry1 = "SELECT rang FROM raenge WHERE uuid=?";
        try (ResultSet rs = mySQL.query(qry1, uuid.toString())) {
            if (rs.next()) {
                return rs.getInt("rang");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public Integer getRankTime(Player player) {
        UUID uuid = player.getUniqueId();
        String qry1 = "SELECT time FROM raenge WHERE uuid=?";
        try (ResultSet rs1 = mySQL.query(qry1, uuid.toString())) {
            if (rs1.next()) {
                return rs1.getInt("time");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public Calendar getRankDate(Player player) {
        UUID uuid = player.getUniqueId();
        Calendar calendar = Calendar.getInstance();
        String qry1 = "SELECT dated FROM raenge WHERE uuid=?";
        try (ResultSet rs1 = mySQL.query(qry1, uuid.toString())) {
            if (rs1.next()) {
                calendar.set(Calendar.DAY_OF_MONTH, rs1.getInt("dated"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String qry2 = "SELECT datem FROM raenge WHERE uuid=?";
        try (ResultSet rs2 = mySQL.query(qry2, uuid.toString())) {
            if (rs2.next()) {
                calendar.set(Calendar.MONTH, rs2.getInt("datem") + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String qry3 = "SELECT datey FROM raenge WHERE uuid=?";
        try (ResultSet rs3 = mySQL.query(qry3, uuid.toString())) {
            if (rs3.next()) {
                calendar.set(Calendar.YEAR, rs3.getInt("datey"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    @Override
    public Integer getPrefix(Player player) {
        UUID uuid = player.getUniqueId();
        String qry1 = "SELECT prefix FROM raenge WHERE uuid=?";
        try (ResultSet rs4 = mySQL.query(qry1, uuid.toString())) {
            if (rs4.next()) {
                return rs4.getInt("prefix");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public Integer getPrefixType(Player player) {
        UUID uuid = player.getUniqueId();
        String qry1 = "SELECT prefixtype FROM raenge WHERE uuid=?";
        try (ResultSet rs4 = mySQL.query(qry1, uuid.toString())) {
            if (rs4.next()) {
                return rs4.getInt("prefixtype");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public String getStatus(Player player) {
        UUID uuid = player.getUniqueId();
        String qry1 = "SELECT status FROM raenge WHERE uuid=?";
        try (ResultSet rs4 = mySQL.query(qry1, uuid.toString())) {
            if (rs4.next()) {
                return rs4.getString("status");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "null";
    }

    public void initPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        mySQL.update("INSERT INTO raenge (uuid, rang, time, dated, datem, datey, prefix, prefixtype, status) VALUES (?,?,?,?,?,?,?,?,?)", uuid.toString(), 0, 0, 0, 0, 0, 0, 0, "00-00");
    }

    @Override
    public boolean doesUserExist(Player player) {
        UUID uuid = player.getUniqueId();
        String qry = "SELECT count(*) AS count FROM raenge WHERE uuid=?";
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
        mySQL.update("CREATE TABLE IF NOT EXISTS raenge (uuid VARCHAR(36), rang INT(3), time INT(10), dated INT(2), datem INT(2), datey INT(4), prefix INT(3), prefixtype INT(3), status VARCHAR(255),FOREIGN KEY (uuid) REFERENCES players(uuid))");
    }
}
