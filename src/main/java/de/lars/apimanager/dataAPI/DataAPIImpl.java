package de.lars.apiManager.dataAPI;

import de.lars.apiManager.Main;
import de.lars.apiManager.database.MySQL;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DataAPIImpl implements IDataAPI {
    private MySQL mySQL;

    @Override
    public boolean isRealTimeActivated() {
        String qry = "SELECT realTimeActivated FROM data";
        try (ResultSet rs = mySQL.query(qry)) {
            if (rs.next()) {
                return rs.getBoolean("realTimeActivated");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isMaintenanceActive() {
        String qry = "SELECT maintenance FROM data";
        try (ResultSet rs = mySQL.query(qry)) {
            if (rs.next()) {
                return rs.getBoolean("maintenance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String getMaintenanceReason() {
        String qry = "SELECT maintenanceReason FROM data";
        try (ResultSet rs = mySQL.query(qry)) {
            if (rs.next()) {
                return rs.getString("maintenanceReason");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public int getMaintenanceTime() {
        String qry = "SELECT maintenanceTime FROM data";
        try (ResultSet rs = mySQL.query(qry)) {
            if (rs.next()) {
                return rs.getInt("maintenanceTime");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void setRealTimeActivated(boolean activated) {
        mySQL.updateAsync("UPDATE data SET realTimeActivated=?", activated);
    }

    @Override
    public void setMaintenanceTime(Integer maintenanceTime) {
        mySQL.updateAsync("UPDATE data SET maintenanceTime=?", maintenanceTime);
    }

    @Override
    public void setMaintenanceReason(String maintenanceReason) {
        mySQL.updateAsync("UPDATE data SET maintenanceTime=?", maintenanceReason);
    }

    @Override
    public void activateMaintenance(String reason, Integer time) {
        mySQL.updateAsync("UPDATE data SET maintenance=?", true);
        mySQL.updateAsync("UPDATE data SET maintenanceReason=?", reason);
        mySQL.updateAsync("UPDATE data SET maintenanceTime=?", time);
    }

    @Override
    public void deactivateMaintenance() {
        mySQL.updateAsync("UPDATE data SET maintenance=?", false);
        mySQL.updateAsync("UPDATE data SET maintenanceReason=?", "");
        mySQL.updateAsync("UPDATE data SET maintenanceTime=?", 0);
    }

    public void createTables() {
        this.mySQL = Main.getInstance().getMySQL();
        mySQL.update("CREATE TABLE IF NOT EXISTS data (realTimeActivated TINYINT(1) DEFAULT TRUE, realWeatherActivated TINYINT(1) DEFAULT FALSE, maintenance TINYINT(1) DEFAULT FALSE, maintenanceReason VARCHAR(255), maintenanceTime INT(35))");
        if (countRowsInTable() < 1) {
            mySQL.update("INSERT INTO data (realTimeActivated, realWeatherActivated, maintenance, maintenanceReason, maintenanceTime) VALUES (?,?,?,?,?)", true, false, false, "", 0);
        }
    }

    public int countRowsInTable() {
        String countQuery = "SELECT COUNT(*) AS row_count FROM data";
        try (ResultSet resultSet = mySQL.query(countQuery)) {
            if (resultSet.next()) {
                return resultSet.getInt("row_count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

}
