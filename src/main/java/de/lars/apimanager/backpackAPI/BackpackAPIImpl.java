package de.lars.apiManager.backpackAPI;

import de.lars.apiManager.Main;
import de.lars.apiManager.database.MySQL;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class BackpackAPIImpl implements IBackpackAPI{
    private MySQL mySQL;

    @Override
    public void setSlots(Player player, int slots) {
        UUID playerUUID = player.getUniqueId();
        mySQL.updateAsync("UPDATE backpacks SET slots  = ? WHERE uuid = ?", slots, playerUUID.toString());
    }

    @Override
    public void setBackpack(OfflinePlayer player, String data) {
        byte[] compressedData;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             DeflaterOutputStream dos = new DeflaterOutputStream(bos)) {
            dos.write(data.getBytes());
            dos.finish();
            compressedData = bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String base64EncodedData = Base64.getEncoder().encodeToString(compressedData);
        UUID playerUUID = player.getUniqueId();
        mySQL.updateAsync("UPDATE backpacks SET data = ? WHERE uuid = ?", base64EncodedData, playerUUID.toString());
    }

    @Override
    public int getSlots(OfflinePlayer offlinePlayer) {
        UUID playerUUID = offlinePlayer.getUniqueId();
        ResultSet resultSet = mySQL.query("SELECT slots FROM backpacks WHERE uuid = ?", playerUUID.toString());
        try {
            if (resultSet.next()) {
                return resultSet.getInt("slots");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public String getBackpack(OfflinePlayer player) {
        UUID playerUUID = player.getUniqueId();
        ResultSet resultSet = mySQL.query("SELECT data FROM backpacks WHERE uuid = ?", playerUUID.toString());
        try {
            if (resultSet.next()) {
                byte[] decodedData = Base64.getDecoder().decode(resultSet.getString("data"));

                String decompressedData;
                try (ByteArrayInputStream bis = new ByteArrayInputStream(decodedData);
                     InflaterInputStream iis = new InflaterInputStream(bis);
                     ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = iis.read(buffer)) != -1) {
                        bos.write(buffer, 0, length);
                    }
                    decompressedData = bos.toString();
                } catch (IOException e) {
                    return "";
                }
                return decompressedData;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<String> getUUIDs() {
        List<String> uuidList = new ArrayList<>();
        ResultSet resultSet = mySQL.query("SELECT uuid FROM backpacks");
        try {
            while (resultSet.next()) {
                String uuid = resultSet.getString("uuid");
                uuidList.add(uuid);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return uuidList;
    }

    @Override
    public void initPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        mySQL.updateAsync("INSERT INTO backpacks (uuid, slots, data) VALUES (?,?,?)", uuid.toString(), 9, "");
    }

    @Override
    public boolean doesUserExist(Player player) {
        UUID uuid = player.getUniqueId();
        String qry = "SELECT count(*) AS count FROM backpacks WHERE uuid=?";
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
    public boolean doesUserExist(OfflinePlayer player) {
        UUID uuid = player.getUniqueId();
        String qry = "SELECT count(*) AS count FROM backpacks WHERE uuid=?";
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
        mySQL.update("CREATE TABLE IF NOT EXISTS backpacks (" +
                "uuid VARCHAR(36) NOT NULL," +
                "slots INT(2) NOT NULL," +
                "data BLOB NOT NULL," +
                "FOREIGN KEY (uuid) REFERENCES players(uuid)" +
                ")");
    }
}
