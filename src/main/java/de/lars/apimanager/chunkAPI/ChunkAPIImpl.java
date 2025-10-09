package de.lars.apiManager.chunkAPI;

import de.lars.apiManager.Main;
import de.lars.apiManager.database.MySQL;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ChunkAPIImpl implements IChunkAPI {
    private MySQL mySQL;

    @Override
    public void claimChunk(Player player, String playerName, String chunk, String friends) {
        UUID uuid = player.getUniqueId();
        mySQL.updateAsync("INSERT INTO chunks (uuid, location, friends, spawning) VALUES (?, ?, ?, ?)", uuid.toString(), chunk, friends, true);
        mySQL.updateAsync("UPDATE players SET free_chunks = ? WHERE uuid = ?", getFreeChunks(player) - 1, uuid.toString());
    }

    @Override
    public void addFree(Player player, Integer add) {
        UUID uuid = player.getUniqueId();
        mySQL.updateAsync("UPDATE players SET free_chunks = ? WHERE uuid = ?", getFreeChunks(player) + add, uuid.toString());
    }

    @Override
    public void removeFree(Player player, Integer remove) {
        UUID uuid =player.getUniqueId();
        mySQL.updateAsync("UPDATE players SET free_chunks = ? WHERE uuid = ?", getFreeChunks(player) - remove, uuid.toString());
    }

    @Override
    public void setEntitySpawning(Player player, String chunk, Boolean spawning) {
        UUID uuid = player.getUniqueId();
        mySQL.updateAsync("UPDATE chunks SET spawning = ? WHERE uuid = ? AND location=?", spawning, uuid.toString(), chunk);
    }

    @Override
    public void addFriend(Player player, String chunk, String friend) {
        UUID playerUUID = player.getUniqueId();
        List<String> currentFriends = new ArrayList<>(getFriends(chunk));
        currentFriends.add(friend);

        String friendsString = String.join(",", currentFriends);

        String qry = "UPDATE chunks SET friends=? WHERE uuid=? AND location=?";
        mySQL.updateAsync(qry, friendsString, playerUUID.toString(), chunk);
    }

    @Override
    public void removeFriend(Player player, String chunk, String friend) {
        UUID playerUUID = player.getUniqueId();
        List<String> currentFriends = new ArrayList<>(getFriends(chunk));
        currentFriends.remove(friend);

        String friendsString = String.join(",", currentFriends);

        String qry = "UPDATE chunks SET friends=? WHERE uuid=? AND location=?";
        mySQL.updateAsync(qry, friendsString, playerUUID.toString(), chunk);
    }

    @Override
    public Boolean getEntitySpawning(String chunk) {
        String qry = "SELECT spawning FROM chunks WHERE location=?";
        try (ResultSet rs = mySQL.query(qry, chunk)) {
            if (rs.next()) {
                Boolean spawning = rs.getBoolean("spawning");
                return spawning;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public UUID getChunkOwner(String chunk) {
        String qry = "SELECT uuid FROM chunks WHERE location=?";
        try (ResultSet rs = mySQL.query(qry, chunk)) {
            if (rs.next()) {
                String playerUUIDString = rs.getString("uuid");
                return UUID.fromString(playerUUIDString);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<String> getFriends(String chunk) {
        List<String> friends = new ArrayList<>();
        String qry = "SELECT friends FROM chunks WHERE location=?";
        try (ResultSet rs = mySQL.query(qry, chunk)) {
            if (rs.next()) {
                String friendsString = rs.getString("friends");
                if (friendsString != null && !friendsString.isEmpty()) {
                    friends = Arrays.asList(friendsString.split(","));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return friends;
    }

    @Override
    public void sellChunk(Player player, String chunk) {
        UUID uuid = player.getUniqueId();
        int chunkId = getChunkId(player, chunk);
        if (chunkId != -1) {
            mySQL.updateAsync("DELETE FROM chunks WHERE id=?", chunkId);
            mySQL.updateAsync("UPDATE players SET free_chunks = ? WHERE uuid = ?", getFreeChunks(player) + 1, uuid.toString());
        }
    }

    private int getChunkId(Player player, String chunk) {
        UUID uuid = player.getUniqueId();
        String qry = "SELECT id FROM chunks WHERE uuid=? AND location=?";
        try (ResultSet rs = mySQL.query(qry, uuid.toString(), chunk)) {
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int getFreeChunks(Player player) {
        UUID uuid = player.getUniqueId();
        String qry = "SELECT free_chunks FROM players WHERE uuid=?";
        try (ResultSet rs = mySQL.query(qry, uuid.toString())) {
            if (rs.next()) {
                return rs.getInt("free_chunks");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public ArrayList<String> getChunks(Player player) {
        UUID playerUUID = player.getUniqueId();
        ArrayList<String> chunks = new ArrayList<>();

        try (ResultSet rs = mySQL.query("SELECT * FROM chunks WHERE uuid=?", playerUUID.toString())) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String Chunk = rs.getString("location");
                chunks.add(Chunk);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return chunks;
    }

    @Override
    public boolean doesChunkHaveOwner(String chunk) {
        String qry = "SELECT count(*) AS count FROM chunks WHERE location=?";
        try (ResultSet rs = mySQL.query(qry, chunk)) {
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void createTables() {
        this.mySQL = Main.getInstance().getMySQL();
        mySQL.update("CREATE TABLE IF NOT EXISTS chunks (id INT NOT NULL AUTO_INCREMENT,uuid VARCHAR(36) NOT NULL,location VARCHAR(255) NOT NULL,PRIMARY KEY (id),FOREIGN KEY (uuid) REFERENCES players(uuid), friends VARCHAR(255), spawning TINYINT(1))");
    }
}
