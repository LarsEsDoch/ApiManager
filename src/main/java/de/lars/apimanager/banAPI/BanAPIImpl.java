package de.lars.apiManager.banAPI;

import de.lars.apiManager.Main;
import de.lars.apiManager.database.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class BanAPIImpl implements IBanAPI {
    private MySQL mySQL;

    @Override
    public void setBanned(Player player, String reason, Integer time) {
        UUID uuid = player.getUniqueId();
        mySQL.updateAsync("UPDATE banlist SET baned=? WHERE uuid=?", 1, uuid.toString());
        mySQL.updateAsync("UPDATE banlist SET reason=? WHERE uuid=?", reason, uuid.toString());
        mySQL.updateAsync("UPDATE banlist SET time=? WHERE uuid=?", time, uuid.toString());
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        mySQL.updateAsync("UPDATE banlist SET dated=? WHERE uuid=?", day, uuid.toString());
        mySQL.updateAsync("UPDATE banlist SET datem=? WHERE uuid=?", month, uuid.toString());
        mySQL.updateAsync("UPDATE banlist SET datey=? WHERE uuid=?", year, uuid.toString());
    }

    @Override
    public void setCriminal(Player player, String reason, Integer time, Player pplayer) {
        UUID uuid = player.getUniqueId();
        UUID Puuid = pplayer.getUniqueId();
        mySQL.updateAsync("UPDATE criminallist SET reason=? WHERE uuid=?", reason, uuid.toString());
        mySQL.updateAsync("UPDATE criminallist SET locked=? WHERE uuid=?", 1, uuid.toString());
        mySQL.updateAsync("UPDATE criminallist SET prosecutor=? WHERE uuid=?", Puuid.toString(), uuid.toString());
    }

    @Override
    public void setOnWait(Player player) {
        UUID uuid = player.getUniqueId();
        mySQL.updateAsync("UPDATE criminallist SET locked=? WHERE uuid=?", 2, uuid.toString());
    }

    @Override
    public void setOnCourt(Player player) {
        UUID uuid = player.getUniqueId();
        mySQL.updateAsync("UPDATE criminallist SET locked=? WHERE uuid=?", 3, uuid.toString());
    }

    @Override
    public void setOnLock(Player player) {
        UUID uuid = player.getUniqueId();
        mySQL.updateAsync("UPDATE criminallist SET locked=? WHERE uuid=?", 4, uuid.toString());
    }

    @Override
    public void setLockTime(Player player, Integer time) {
        UUID uuid = player.getUniqueId();
        mySQL.updateAsync("UPDATE criminallist SET time=? WHERE uuid=?", time, uuid.toString());
    }

    @Override
    public void setLocked(Player player, Integer time, Integer cell) {
        UUID uuid = player.getUniqueId();
        mySQL.updateAsync("UPDATE criminallist SET time=? WHERE uuid=?", time, uuid.toString());
        mySQL.updateAsync("UPDATE criminallist SET locked=? WHERE uuid=?", 5, uuid.toString());
        mySQL.updateAsync("UPDATE criminallist SET cell=? WHERE uuid=?", cell, uuid.toString());
    }

    @Override
    public void setUnlocked(Player player) {
        UUID uuid = player.getUniqueId();
        mySQL.updateAsync("UPDATE criminallist SET locked=? WHERE uuid=?", 0, uuid.toString());
        mySQL.updateAsync("UPDATE criminallist SET reason=? WHERE uuid=?", "", uuid.toString());
        mySQL.updateAsync("UPDATE criminallist SET time=? WHERE uuid=?", 0, uuid.toString());
        mySQL.updateAsync("UPDATE criminallist SET prosecutor=? WHERE uuid=?", "", uuid.toString());
        mySQL.updateAsync("UPDATE criminallist SET cell=? WHERE uuid=?", 0, uuid.toString());
    }

    @Override
    public void setUnBaned(OfflinePlayer player) {
        UUID uuid = player.getUniqueId();
        mySQL.updateAsync("UPDATE banlist SET baned=? WHERE uuid=?", 0, uuid.toString());
        mySQL.updateAsync("UPDATE banlist SET reason=? WHERE uuid=?", "", uuid.toString());
        mySQL.updateAsync("UPDATE banlist SET time=? WHERE uuid=?", 0, uuid.toString());
        mySQL.updateAsync("UPDATE banlist SET dated=? WHERE uuid=?", 0, uuid.toString());
        mySQL.updateAsync("UPDATE banlist SET datem=? WHERE uuid=?", 0, uuid.toString());
        mySQL.updateAsync("UPDATE banlist SET datey=? WHERE uuid=?", 0, uuid.toString());
    }

    @Override
    public Boolean getBanned(Player player) {
        UUID uuid = player.getUniqueId();
        String qry = "SELECT baned FROM banlist WHERE uuid=?";
        try (ResultSet rs = mySQL.query(qry, uuid.toString())) {
            if (rs.next()) {
                if (rs.getInt("baned") == 1) {
                    return true;
                }
                if (rs.getInt("baned") == 0) {
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Integer getCriminalTime(Player player) {
        UUID uuid = player.getUniqueId();
        String qry = "SELECT time FROM criminallist WHERE uuid=?";
        try (ResultSet rs = mySQL.query(qry, uuid.toString())) {
            if (rs.next()) {
                return rs.getInt("time");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public Integer getCell(Player player) {
        UUID uuid = player.getUniqueId();
        String qry = "SELECT cell FROM criminallist WHERE uuid=?";
        try (ResultSet rs = mySQL.query(qry, uuid.toString())) {
            if (rs.next()) {
                return rs.getInt("cell");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public Integer getTime(Player player) {
        UUID uuid = player.getUniqueId();
        String qry = "SELECT time FROM banlist WHERE uuid=?";
        try (ResultSet rs = mySQL.query(qry, uuid.toString())) {
            if (rs.next()) {
                return rs.getInt("time");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public Calendar getBanDate(Player player) {
        UUID uuid = player.getUniqueId();
        String qry1 = "SELECT dated FROM banlist WHERE uuid=?";
        String qry2 = "SELECT datem FROM banlist WHERE uuid=?";
        String qry3 = "SELECT datey FROM banlist WHERE uuid=?";
        Calendar calendar = Calendar.getInstance();

        try (ResultSet rs3 = mySQL.query(qry3, uuid.toString())) {
            if (rs3.next()) {
                calendar.set(Calendar.YEAR, rs3.getInt("datey"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (ResultSet rs2 = mySQL.query(qry2, uuid.toString())) {
            if (rs2.next()) {
                calendar.set(Calendar.MONTH, rs2.getInt("datem"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (ResultSet rs1 = mySQL.query(qry1, uuid.toString())) {
            if (rs1.next()) {
                calendar.set(Calendar.DAY_OF_MONTH, rs1.getInt("dated"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar;

    }

    @Override
    public String getReason(Player player) {
        UUID uuid = player.getUniqueId();
        String qry = "SELECT reason FROM banlist WHERE uuid=?";
        try (ResultSet rs = mySQL.query(qry, uuid.toString())) {
            if (rs.next()) {
                return rs.getString("reason");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "ERROR";
    }

    @Override
    public String getCriminalReason(Player player) {
        UUID uuid = player.getUniqueId();
        String qry = "SELECT reason FROM criminallist WHERE uuid=?";
        try (ResultSet rs = mySQL.query(qry, uuid.toString())) {
            if (rs.next()) {
                return rs.getString("reason");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "ERROR";
    }

    @Override
    public String getProsecutor(Player player) {
        UUID uuid = player.getUniqueId();
        String qry = "SELECT prosecutor FROM criminallist WHERE uuid=?";
        try (ResultSet rs = mySQL.query(qry, uuid.toString())) {
            if (rs.next()) {
                return rs.getString("prosecutor");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "ERROR";
    }

    @Override
    public Integer isCriminal(Player player) {
        UUID uuid = player.getUniqueId();
        String qry = "SELECT locked FROM criminallist WHERE uuid=?";
        try (ResultSet rs = mySQL.query(qry, uuid.toString())) {
            if (rs.next()) {
                return rs.getInt("locked");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void initPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        mySQL.updateAsync("INSERT INTO banlist (uuid, baned, dated, datem, datey, reason, time) VALUES (?,?,?,?,?,?,?)", uuid.toString(), 0, 0, 0, 0, "", 0);
    }

    @Override
    public void initPlayerC(Player player) {
        UUID uuid = player.getUniqueId();
        mySQL.updateAsync("INSERT INTO criminallist (uuid, prosecutor, locked, reason, time, cell) VALUES (?,?,?,?,?,?)", uuid.toString(), "", 0, "", 0, 0);
    }

    @Override
    public List<String> getBannedPlayers() {
        List<String> bannedPlayers = new ArrayList<>();

        ResultSet resultSet = mySQL.query("SELECT uuid FROM banlist");

        try {
            while (resultSet.next()) {
                String uuidString = resultSet.getString("uuid");
                UUID uuid = UUID.fromString(uuidString);
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                if (offlinePlayer.hasPlayedBefore() || offlinePlayer.isOnline()) {
                    bannedPlayers.add(offlinePlayer.getName());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bannedPlayers;
    }

    @Override
    public boolean doesUserExist(OfflinePlayer player) {
        UUID uuid = player.getUniqueId();
        String qry = "SELECT count(*) AS count FROM banlist WHERE uuid=?";
        try (ResultSet rs = mySQL.query(qry, uuid.toString())) {
            if (rs.next()) {
                return rs.getInt("count") != 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean doesCriminalUserExist(Player player) {
        UUID uuid = player.getUniqueId();
        String qry = "SELECT count(*) AS count FROM criminallist WHERE uuid=?";
        try (ResultSet rs = mySQL.query(qry, uuid.toString())) {
            if (rs.next()) {
                return rs.getInt("count") != 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void createTables() {
        this.mySQL = Main.getInstance().getMySQL();
        mySQL.update("CREATE TABLE IF NOT EXISTS banlist (uuid VARCHAR(36), baned INT(35), dated INT(34), datem INT(33), datey INT(32), reason VARCHAR(255), time INT(30),FOREIGN KEY (uuid) REFERENCES players(uuid))");
        mySQL.update("CREATE TABLE IF NOT EXISTS criminallist (uuid VARCHAR(255), prosecutor VARCHAR(255), locked INT(34), reason VARCHAR(255), time INT(32), cell INT(255),FOREIGN KEY (uuid) REFERENCES players(uuid))");
    }
}
