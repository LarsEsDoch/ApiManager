package de.lars.apiManager.questAPI;

import de.lars.apiManager.Main;
import de.lars.apiManager.database.MySQL;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class QuestAPIImpl implements IQuestAPI {
    private MySQL mySQL;
    private Integer number;

    @Override
    public void setStreak(Player player, int amount) {
        UUID uuid = player.getUniqueId();
        mySQL.update("UPDATE quests SET streak=? WHERE uuid=?", amount, uuid.toString());
    }

    @Override
    public void setQuest(Player player, int amount, int number) {
        UUID uuid = player.getUniqueId();
        mySQL.update("UPDATE quests SET questcomplete=? WHERE uuid=?", 0, uuid.toString());
        mySQL.update("UPDATE quests SET hasnumber=? WHERE uuid=?", 0, uuid.toString());
        mySQL.update("UPDATE quests SET quest=? WHERE uuid=?", amount, uuid.toString());
        mySQL.update("UPDATE quests SET number=? WHERE uuid=?", number, uuid.toString());
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        mySQL.update("UPDATE quests SET dated=? WHERE uuid=?", day, uuid.toString());
        mySQL.update("UPDATE quests SET datem=? WHERE uuid=?", month, uuid.toString());
        mySQL.update("UPDATE quests SET datey=? WHERE uuid=?", year, uuid.toString());
    }

    @Override
    public void setQuestComplete(Player player, boolean amount) {
        UUID uuid = player.getUniqueId();
        if (amount == true) {
            number = 1;
        }
        if (amount == false) {
            number = 0;
        }
        mySQL.update("UPDATE quests SET questcomplete=? WHERE uuid=?", number, uuid.toString());
    }

    @Override
    public boolean getDailyQuestComplete(Player player) {
        UUID uuid = player.getUniqueId();
        String qry = "SELECT questcomplete FROM quests WHERE uuid=?";
        try (ResultSet rs = mySQL.query(qry, uuid.toString())) {
            if (rs.next()) {
                if (rs.getInt("questcomplete") == 1) {
                    return true;
                }
                if (rs.getInt("questcomplete") == 0) {
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Integer getDailyQuest(Player player) {
        UUID uuid = player.getUniqueId();
        String qry = "SELECT quest FROM quests WHERE uuid=?";
        try (ResultSet rs = mySQL.query(qry, uuid.toString())) {
            if (rs.next()) {
                return rs.getInt("quest");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public Integer getDailyQuestNumber(Player player) {
        UUID uuid = player.getUniqueId();
        String qry = "SELECT number FROM quests WHERE uuid=?";
        try (ResultSet rs = mySQL.query(qry, uuid.toString())) {
            if (rs.next()) {
                return rs.getInt("number");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public Integer getDailyQuestHasNumber(Player player) {
        UUID uuid = player.getUniqueId();
        String qry = "SELECT hasnumber FROM quests WHERE uuid=?";
        try (ResultSet rs = mySQL.query(qry, uuid.toString())) {
            if (rs.next()) {
                return rs.getInt("hasnumber");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void addDailyhasnumber(Player player, int number) {
        UUID uuid = player.getUniqueId();
        int amount = 0;
        String qry = "SELECT hasnumber FROM quests WHERE uuid=?";
        try (ResultSet rs = mySQL.query(qry, uuid.toString())) {
            if (rs.next()) {
                amount = rs.getInt("hasnumber");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int plus;
        plus = number + amount;
        mySQL.update("UPDATE quests SET hasnumber=? WHERE uuid=?", plus, uuid.toString());
    }

    @Override
    public void removeDailyhasnumber(Player player, int number) {
        UUID uuid = player.getUniqueId();
        int amount = 0;
        String qry = "SELECT hasnumber FROM quests WHERE uuid=?";
        try (ResultSet rs = mySQL.query(qry, uuid.toString())) {
            if (rs.next()) {
                amount = rs.getInt("hasnumber");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int minus;
        minus = amount - number;
        mySQL.update("UPDATE quests SET hasnumber=? WHERE uuid=?", minus, uuid.toString());
    }

    @Override
    public void setDailyhasnumber(Player player, int number) {
        UUID uuid = player.getUniqueId();
        mySQL.update("UPDATE quests SET hasnumber=? WHERE uuid=?", number, uuid.toString());
    }

    @Override
    public Date getQuestDate(Player player) {
        UUID uuid = player.getUniqueId();
        String qry1 = "SELECT dated FROM quests WHERE uuid=?";
        String qry2 = "SELECT datem FROM quests WHERE uuid=?";
        String qry3 = "SELECT datey FROM quests WHERE uuid=?";
        Date date = new Date();

        try (ResultSet rs3 = mySQL.query(qry3, uuid.toString())) {
            if (rs3.next()) {
                date.setYear(rs3.getInt("datey") - 1900);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (ResultSet rs2 = mySQL.query(qry2, uuid.toString())) {
            if (rs2.next()) {
                date.setMonth(rs2.getInt("datem") - 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (ResultSet rs1 = mySQL.query(qry1, uuid.toString())) {
            if (rs1.next()) {
                date.setDate(rs1.getInt("dated"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(0);
        return date;

    }

    @Override
    public Integer getStreak(Player player) {
        UUID uuid = player.getUniqueId();
        String qry = "SELECT streak FROM quests WHERE uuid=?";
        try (ResultSet rs = mySQL.query(qry, uuid.toString())) {
            if (rs.next()) {
                return rs.getInt("streak");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void initPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        mySQL.update("INSERT INTO quests (uuid, streak, quest, dated, datem, datey, questcomplete, number, hasnumber) VALUES (?,?,?,?,?,?,?,?,?)", uuid.toString(), 0, -1 ,0 ,0 ,0 , 0, 0, 0);
    }

    @Override
    public boolean doesUserExist(Player player) {
        UUID uuid = player.getUniqueId();
        String qry = "SELECT count(*) AS count FROM quests WHERE uuid=?";
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
        mySQL.update("CREATE TABLE IF NOT EXISTS quests (uuid VARCHAR(36), streak INT(6), quest INT(3), dated INT(2), datem INT(2), datey INT(4), questcomplete INT(6), number INT(5), hasnumber INT(5),FOREIGN KEY (uuid) REFERENCES players(uuid))");
    }
}
