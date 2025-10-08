package de.lars.apiManager.timerAPI;

import de.lars.apiManager.Main;
import de.lars.apiManager.database.MySQL;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class TimerAPIImpl implements ITimerAPI {
    private MySQL mySQL;

    @Override
    public void setTime(Player player, int time) {
        UUID playerUUID = player.getUniqueId();
        mySQL.update("UPDATE timers SET time = ? WHERE uuid = ?", time, playerUUID.toString());
    }

    @Override
    public void setOff(Player player, boolean off) {
        UUID playerUUID = player.getUniqueId();
        mySQL.update("UPDATE timers SET off = ? WHERE uuid = ?", off, playerUUID.toString());
    }

    @Override
    public void setRunning(Player player, boolean running) {
        UUID playerUUID = player.getUniqueId();
        mySQL.update("UPDATE timers SET running = ? WHERE uuid = ?", running, playerUUID.toString());
    }

    @Override
    public void setTimer(Player player, boolean timer) {
        UUID playerUUID = player.getUniqueId();
        mySQL.update("UPDATE timers SET timer = ? WHERE uuid = ?", timer, playerUUID.toString());
    }

    @Override
    public void setPublic(Player player, boolean isPublic) {
        UUID playerUUID = player.getUniqueId();
        mySQL.update("UPDATE timers SET public = ? WHERE uuid = ?", isPublic, playerUUID.toString());
    }

    @Override
    public int getTime(Player player) {
        UUID playerUUID = player.getUniqueId();
        ResultSet resultSet = mySQL.query("SELECT time FROM timers WHERE uuid = ?", playerUUID.toString());
        try {
            if (resultSet.next()) {
                return resultSet.getInt("time");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean isPublic(Player player) {
        UUID playerUUID = player.getUniqueId();
        ResultSet resultSet = mySQL.query("SELECT public FROM timers WHERE uuid = ?", playerUUID.toString());
        try {
            if (resultSet.next()) {
                return resultSet.getBoolean("public");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isOff(Player player) {
        UUID playerUUID = player.getUniqueId();
        ResultSet resultSet = mySQL.query("SELECT off FROM timers WHERE uuid = ?", playerUUID.toString());
        try {
            if (resultSet.next()) {
                return resultSet.getBoolean("off");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isRunning(Player player) {
        UUID playerUUID = player.getUniqueId();
        ResultSet resultSet = mySQL.query("SELECT running FROM timers WHERE uuid = ?", playerUUID.toString());
        try {
            if (resultSet.next()) {
                return resultSet.getBoolean("running");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isTimer(Player player) {
        UUID playerUUID = player.getUniqueId();
        ResultSet resultSet = mySQL.query("SELECT timer FROM timers WHERE uuid = ?", playerUUID.toString());
        try {
            if (resultSet.next()) {
                return resultSet.getBoolean("timer");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean publicTimerExists(Player player) {
        UUID playerUUID = player.getUniqueId();
        ResultSet resultSet = mySQL.query("SELECT COUNT(*) as count FROM timers WHERE uuid = ? AND public = 1", playerUUID.toString());
        try {
            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void initPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        mySQL.update("INSERT INTO timers (uuid, time, off, public, running, timer) VALUES (?,?,?,?,?,?)", uuid.toString(), 0, true, false, false, false);
    }

    public boolean doesUserExist(Player player) {
        UUID uuid = player.getUniqueId();
        String qry = "SELECT count(*) AS count FROM timers WHERE uuid=?";
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
        mySQL.update("CREATE TABLE IF NOT EXISTS timers (" +
                "uuid VARCHAR(36) NOT NULL," +
                "time INT NOT NULL," +
                "off BOOLEAN NOT NULL," +
                "public BOOLEAN NOT NULL," +
                "running BOOLEAN NOT NULL," +
                "timer BOOLEAN NOT NULL," +
                "FOREIGN KEY (uuid) REFERENCES players(uuid)" +
                ")");
    }
}
