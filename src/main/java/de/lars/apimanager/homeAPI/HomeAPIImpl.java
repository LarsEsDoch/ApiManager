package de.lars.apiManager.homeAPI;

import de.lars.apiManager.Main;
import de.lars.apiManager.database.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class HomeAPIImpl implements IHomeAPI {
    private MySQL mySQL;

    @Override
    public void createHome(Player player, String name, Location location, boolean isPublic) {
        UUID ownerUUID = player.getUniqueId();
        String locationString = Objects.requireNonNull(location.getWorld()).getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ();

        mySQL.updateAsync("INSERT INTO homes (uuid, name, location, is_public) VALUES (?, ?, ?, ?)", ownerUUID.toString(), name, locationString, isPublic);
    }

    @Override
    public void deleteHome(Player player, int homeID) {
        UUID uuid = player.getUniqueId();
        mySQL.updateAsync("DELETE FROM homes WHERE id = ? AND uuid = ?", homeID, uuid.toString());
    }

    @Override
    public List<String> getHomes(Player player) {
        List<String> homes = new ArrayList<>();
        UUID playerUUID = player.getUniqueId();

        ResultSet resultSet = mySQL.query("SELECT name FROM homes WHERE uuid = ? OR is_public = ?", playerUUID.toString(), 1);
        try {
            while (resultSet.next()) {
                String locationString = resultSet.getString("name");;
                homes.add(locationString);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return homes;
    }

    @Override
    public List<String> getOwnHomes(Player player) {
        List<String> homes = new ArrayList<>();
        UUID playerUUID = player.getUniqueId();

        ResultSet resultSet = mySQL.query("SELECT name FROM homes WHERE uuid = ?", playerUUID.toString());
        try {
            while (resultSet.next()) {
                String locationString = resultSet.getString("name");;
                homes.add(locationString);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return homes;
    }

    @Override
    public void setHomePublic(Player player, int homeID, boolean isPublic) {
        UUID uuid = player.getUniqueId();
        mySQL.updateAsync("UPDATE homes SET is_public = ? WHERE id = ? AND uuid = ?", isPublic, homeID, uuid.toString());
    }

    @Override
    public Location getHomeLocation(int homeID) {
        ResultSet resultSet = mySQL.query("SELECT location FROM homes WHERE id = ?", homeID);
        try {
            if (resultSet.next()) {
                String locationString = resultSet.getString("location");
                return parseLocation(locationString);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean doesHomeExist(Player player, String name) {
        UUID uuid = player.getUniqueId();
        ResultSet resultSet = mySQL.query("SELECT COUNT(*) FROM homes WHERE uuid = ? AND name = ?", uuid.toString(), name);
        ResultSet resultSet2 = mySQL.query("SELECT COUNT(*) FROM homes WHERE is_public = ? AND name = ?", true, name);
        try {
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count == 0) {
                    try {
                        if (resultSet2.next()) {
                            int count2 = resultSet2.getInt(1);
                            return count2 > 0;
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean doesOwnHomeExist(Player player, String name) {
        UUID uuid = player.getUniqueId();
        ResultSet resultSet = mySQL.query("SELECT COUNT(*) FROM homes WHERE uuid = ? AND name = ?", uuid.toString(), name);
        try {
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int getHomeId(Player player, String name) {
        UUID uuid = player.getUniqueId();
        ResultSet resultSet = mySQL.query("SELECT id FROM homes WHERE uuid = ? AND name = ?", uuid.toString(), name);
        ResultSet resultSet2 = mySQL.query("SELECT id FROM homes WHERE is_public = ? AND name = ?", true, name);
        try {
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (resultSet2.next()) {
                return resultSet2.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void createTables() {
        this.mySQL = Main.getInstance().getMySQL();
        mySQL.update("CREATE TABLE IF NOT EXISTS homes (id INT AUTO_INCREMENT," +
                "uuid VARCHAR(36)," +
                "name VARCHAR(255)," +
                "location VARCHAR(255)," +
                "is_public TINYINT(1)," +
                "PRIMARY KEY (id)," +
                "FOREIGN KEY (uuid) REFERENCES players(uuid))");
    }

    private Location parseLocation(String locationString) {
        String[] parts = locationString.split(",");
        if (parts.length == 4) {
            String worldName = parts[0];
            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);

            return new Location(Bukkit.getWorld(worldName), x, y, z);
        }
        return null;
    }
}
