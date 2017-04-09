package net.kaikk.mc.bcl.datastore;

import net.kaikk.mc.bcl.BetterChunkLoader;
import net.kaikk.mc.bcl.CChunkLoader;
import net.kaikk.mc.bcl.config.Config;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public class MySqlDataStore extends AHashMapDataStore {

    private Connection dbConnection;

    private static UUID toUUID(String string) {
        return UUID.fromString(string);
    }

    private static String UUIDtoHexString(UUID uuid) {
        if (uuid == null) {
            return "";
        }
        return "\"" + uuid.toString() + "\"";
    }

    @Override
    public String getName() {
        return "MySQL";
    }

    @Override
    public void load() {
        try {
            // init connection
            this.refreshConnection();
        } catch (final Exception e) {
            BetterChunkLoader.instance().getLogger().error("Unable to connect to database. Check your config file settings.");
            throw new RuntimeException(e);
        }
        // create table, if not exists
        try {
            this.statement().executeUpdate("CREATE TABLE IF NOT EXISTS bcl_chunkloaders ("
                    + "loc varchar(50) NOT NULL, "
                    + "r tinyint(3) unsigned NOT NULL, "
                    + "owner varchar(36) NOT NULL, "
                    + "date bigint(20) NOT NULL, "
                    + "aon tinyint(1) NOT NULL, "
                    + "serverName varchar(50) NOT NULL, "
                    + "UNIQUE KEY loc (loc));");

            this.statement().executeUpdate("CREATE TABLE IF NOT EXISTS bcl_playersdata ("
                    + "pid varchar(36) NOT NULL, "
                    + "alwayson smallint(6) unsigned NOT NULL, "
                    + "onlineonly smallint(6) unsigned NOT NULL, "
                    + "UNIQUE KEY pid (pid));");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // load data
        this.chunkLoaders = new HashMap<>();
        try {
            ResultSet rs = this.statement().executeQuery(
                    "SELECT * FROM bcl_chunkloaders where serverName = '" + Config.getConfig().get().getNode("ServerName").getString() + "'");
            while (rs.next()) {
                try {
                    CChunkLoader chunkLoader =
                            new CChunkLoader(rs.getString(1), rs.getByte(2), toUUID(rs.getString(3)), new Date(rs.getLong(4)), rs.getBoolean(5),
                                    rs.getString(6));
                    List<CChunkLoader> clList = this.chunkLoaders.get(chunkLoader.getWorldName());
                    if (clList == null) {
                        clList = new ArrayList<>();
                        chunkLoaders.put(chunkLoader.getWorldName(), clList);
                    }
                    clList.add(chunkLoader);
                } catch (Exception ignored) {
                }
            }
        } catch (SQLException e) {
            BetterChunkLoader.instance().getLogger().info("Couldn't read chunk loaders data from MySQL server.");
            throw new RuntimeException(e);
        }
        this.playersData = new HashMap<>();
        try {
            ResultSet rs = this.statement().executeQuery("SELECT * FROM bcl_playersdata");
            while (rs.next()) {
                PlayerData pd = new PlayerData(toUUID(rs.getString(1)), rs.getInt(2), rs.getInt(3));
                this.playersData.put(pd.getPlayerId(), pd);
            }
        } catch (SQLException e) {
            BetterChunkLoader.instance().getLogger().info("Couldn't read players data from MySQL server.");
            throw new RuntimeException(e);
        }
    }

    @Override
    public CChunkLoader getChunkLoaderAt(Location<World> blockLocation) {
        for (Map.Entry<String, List<CChunkLoader>> entry : this.chunkLoaders.entrySet()) {
            for (CChunkLoader cChunkLoader : entry.getValue()) {
                if (cChunkLoader.getServerName().equalsIgnoreCase(Config.getConfig().get().getNode("ServerName").getString())) {
                    if (cChunkLoader.getLoc().equals(blockLocation)) {
                        return cChunkLoader;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void refreshPlayer(UUID uuid) {
        try {
            String statement = "SELECT * FROM bcl_playersdata WHERE pid=" + UUIDtoHexString(uuid) + " LIMIT 1";
            ResultSet rs = this.statement().executeQuery(statement);
            while (rs.next()) {
                PlayerData playerData = this.getPlayerData(uuid);
                playerData.setAlwaysOnChunksAmount(rs.getInt(2));
                playerData.setOnlineOnlyChunksAmount(rs.getInt(3));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addChunkLoader(CChunkLoader chunkLoader) {
        super.addChunkLoader(chunkLoader);
        try {
            String statement = "REPLACE INTO bcl_chunkloaders VALUES (\"" + chunkLoader.getLocationString() + "\", " + chunkLoader.getRange() + ", "
                    + UUIDtoHexString(chunkLoader.getOwner()) + ", " + chunkLoader.getCreationDate().getTime() + ", " + (chunkLoader.isAlwaysOn() ?
                    1 : 0) + ", \"" + Config.getConfig().get().getNode("ServerName").getString() + "\")";
            this.statement().executeUpdate(statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeChunkLoader(CChunkLoader chunkLoader) {
        super.removeChunkLoader(chunkLoader);
        try {
            this.statement().executeUpdate("DELETE FROM bcl_chunkloaders WHERE loc = \"" + chunkLoader.getLocationString() + "\" LIMIT 1");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeChunkLoaders(UUID ownerId) {
        super.removeChunkLoaders(ownerId);
        try {
            this.statement().executeUpdate("DELETE FROM bcl_chunkloaders WHERE owner = " + UUIDtoHexString(ownerId));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void changeChunkLoaderRange(CChunkLoader chunkLoader, byte range) {
        super.changeChunkLoaderRange(chunkLoader, range);
        try {
            this.statement()
                    .executeUpdate("UPDATE bcl_chunkloaders SET r = " + range + " WHERE loc = \"" + chunkLoader.getLocationString() + "\" LIMIT 1");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setAlwaysOnChunksLimit(UUID playerId, int amount) {
        super.setAlwaysOnChunksLimit(playerId, amount);
        try {
            this.statement().executeUpdate(
                    "INSERT INTO bcl_playersdata VALUES (" + UUIDtoHexString(playerId) + ", " + amount + ", " + Config.getConfig().get()
                            .getNode("DefaultChunksAmount").getNode("World").getInt() + ") ON DUPLICATE KEY UPDATE alwayson=" + amount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setOnlineOnlyChunksLimit(UUID playerId, int amount) {
        super.setOnlineOnlyChunksLimit(playerId, amount);
        try {
            this.statement().executeUpdate("INSERT INTO bcl_playersdata VALUES (" + UUIDtoHexString(playerId) + ", " + Config.getConfig().get()
                    .getNode("DefaultChunksAmount").getNode("Personal").getInt() + ", " + amount + ") ON DUPLICATE KEY UPDATE onlineonly=" + amount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addAlwaysOnChunksLimit(UUID playerId, int amount) {
        super.addAlwaysOnChunksLimit(playerId, amount);
        try {
            this.statement().executeUpdate(
                    "INSERT INTO bcl_playersdata VALUES (" + UUIDtoHexString(playerId) + ", " + amount + ", " + Config.getConfig().get()
                            .getNode("DefaultChunksAmount").getNode("World").getInt() + ") ON DUPLICATE KEY UPDATE alwayson=alwayson+" + amount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addOnlineOnlyChunksLimit(UUID playerId, int amount) {
        super.addOnlineOnlyChunksLimit(playerId, amount);
        try {
            this.statement().executeUpdate("INSERT INTO bcl_playersdata VALUES (" + UUIDtoHexString(playerId) + ", " + Config.getConfig().get()
                    .getNode("DefaultChunksAmount").getNode("Personal").getInt() + ", " + amount + ") ON DUPLICATE KEY UPDATE onlineonly=onlineonly+"
                    + amount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshConnection() throws SQLException {
        if (this.dbConnection == null || this.dbConnection.isClosed()) {
            // set username/pass properties
            final Properties connectionProps = new Properties();
            connectionProps.put("user", Config.getConfig().get().getNode("MySQL").getNode("Username").getString());
            connectionProps.put("password", Config.getConfig().get().getNode("MySQL").getNode("Password").getString());

            // establish connection
            this.dbConnection = DriverManager.getConnection(
                    "jdbc:mysql://" + Config.getConfig().get().getNode("MySQL").getNode("Hostname").getString() + "/" + Config.getConfig().get()
                            .getNode("MySQL").getNode("Database").getString() + "?autoReconnect=true", connectionProps);
        }
    }

    private Statement statement() throws SQLException {
        this.refreshConnection();
        return this.dbConnection.createStatement();
    }
}
