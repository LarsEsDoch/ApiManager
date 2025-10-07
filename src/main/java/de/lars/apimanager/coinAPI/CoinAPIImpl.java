package de.lars.apimanager.coinAPI;

import de.lars.apimanager.Main;
import de.lars.apimanager.database.MySQL;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CoinAPIImpl implements ICoinAPI {
    private MySQL mySQL;

    @Override
    public Integer getCoins(Player player) {
        UUID uuid = player.getUniqueId();
        String qry = "SELECT coins FROM coinlist WHERE uuid=?";
        try (ResultSet rs = mySQL.query(qry, uuid.toString())) {
            if (rs.next()) {
                return rs.getInt("coins");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void addCoins(Player player, int amount) {
        UUID uuid = player.getUniqueId();
        mySQL.update("UPDATE coinlist SET coins=? WHERE uuid=?", getCoins(player) + amount, uuid.toString());
    }

    @Override
    public void removeCoins(Player player, int amount) {
        UUID uuid = player.getUniqueId();
        int currentCoins = getCoins(player);

        if (currentCoins >= amount) {
            mySQL.update("UPDATE coinlist SET coins=? WHERE uuid=?", currentCoins - amount, uuid.toString());
        }
    }

    @Override
    public void setCoins(Player player, int amount) {
        UUID uuid = player.getUniqueId();
        mySQL.update("UPDATE coinlist SET coins=? WHERE uuid=?", amount, uuid.toString());
    }

    @Override
    public void addGift(Player player, String gift) {
        UUID playerUUID = player.getUniqueId();
        List<String> currentGifts = new ArrayList<>(getGifts(player));
        currentGifts.add(gift);

        String giftString = String.join(",", currentGifts);

        String qry = "UPDATE coinlist SET gifts=? WHERE uuid=?";
        mySQL.update(qry, giftString, playerUUID.toString());
    }

    @Override
    public void removeGift(Player player, String gift) {
        UUID playerUUID = player.getUniqueId();
        List<String> currentGifts = new ArrayList<>(getGifts(player));
        currentGifts.remove(gift);

        String giftString = String.join(",", currentGifts);

        String qry = "UPDATE coinlist SET gifts=? WHERE uuid=?";
        mySQL.update(qry, giftString, playerUUID.toString());
    }

    @Override
    public List<String> getGifts(Player player) {
        List<String> friends = new ArrayList<>();
        String qry = "SELECT gifts FROM coinlist WHERE uuid=?";
        try (ResultSet rs = mySQL.query(qry, player.getUniqueId().toString())) {
            if (rs.next()) {
                String friendsString = rs.getString("gifts");
                if (friendsString != null && !friendsString.isEmpty()) {
                    friends = Arrays.asList(friendsString.split(","));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return friends;
    }

    public void initPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        mySQL.update("INSERT INTO coinlist (uuid, coins, gifts) VALUES (?,?, ?)", uuid.toString(), 0, "");
    }

  @Override
    public boolean doesUserExist(Player player) {
        UUID uuid = player.getUniqueId();
        String qry = "SELECT count(*) AS count FROM coinlist WHERE uuid=?";
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
        mySQL.update("CREATE TABLE IF NOT EXISTS coinlist (uuid VARCHAR(36),coins INT(35),gifts VARCHAR(255),FOREIGN KEY (uuid) REFERENCES players(uuid))");
    }
}
