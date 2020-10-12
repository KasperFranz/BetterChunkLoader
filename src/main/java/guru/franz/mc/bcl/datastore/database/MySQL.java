package guru.franz.mc.bcl.datastore.database;

import guru.franz.mc.bcl.config.Config;
import guru.franz.mc.bcl.exception.UserNotFound;
import guru.franz.mc.bcl.datastore.exceptions.MySQLConnectionException;
import net.kaikk.mc.bcl.BetterChunkLoader;
import net.kaikk.mc.bcl.CChunkLoader;
import net.kaikk.mc.bcl.datastore.PlayerData;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MySQL implements DatabaseInterface {

    /**
     * Set up the tables we need for BCL
     *
     * @throws SQLException             if there is an error with the SQL
     * @throws MySQLConnectionException if there is an error when connecting to the SQL Server
     */
    public void setupTable() throws SQLException, MySQLConnectionException {
        //TODO we should look at adding created and changed at timestamps
        try (Connection conn = getDataStore().getConnection()) {
            // create table, if not exists
            conn.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS bcl_chunkloaders ("
                    + "loc varchar(50) NOT NULL, "
                    + "r tinyint(3) unsigned NOT NULL, "
                    + "owner varchar(36) NOT NULL, "
                    + "date bigint(20) NOT NULL, "
                    + "aon tinyint(1) NOT NULL, "
                    + "serverName varchar(50) NOT NULL, "
                    + "UNIQUE KEY loc (loc));"); //TODO should be servername and loc

            //TODO: we should use prepare statement
            conn.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS bcl_playersdata ("
                    + "pid varchar(36) NOT NULL, "
                    + "alwayson smallint(6) unsigned NOT NULL, "
                    + "onlineonly smallint(6) unsigned NOT NULL, "
                    + "UNIQUE KEY pid (pid));"); //TODO should this not include servername as well?

        }
    }

    /**
     * Get the chunk Loaders by server and world
     * @param serverName the servers name
     * @param world The world
     * @return a list of chunkloaders
     * @throws SQLException if there is any errors with the MySQL
     * @throws MySQLConnectionException If we can't connect to the database.
     */
    public List<CChunkLoader> getChunkloadersByServerAndWorld(String serverName, String world) throws SQLException, MySQLConnectionException {
        List<CChunkLoader> chunkLoaders = new ArrayList<>();
        try (Connection conn = getDataStore().getConnection()) {

            //TODO: we should use prepare statement
            ResultSet rs = conn.createStatement().executeQuery(
                    "SELECT * FROM bcl_chunkloaders where serverName = '" + serverName + "' and loc like '" + world + ":%'");
            while (rs.next()) {
                try {

                    CChunkLoader chunkLoader =
                            new CChunkLoader(
                                    rs.getString(1),
                                    rs.getByte(2),
                                    toUUID(rs.getString(3)),
                                    new Date(rs.getLong(4)),
                                    rs.getBoolean(5),
                                    rs.getString(6)
                            );
                    chunkLoaders.add(chunkLoader);
                } catch (Exception ignored) {
                }
            }
        }
        return chunkLoaders;
    }

    /**
     * Get all players from the database
     * @return all the player data from the database
     * @throws SQLException if there is any errors with the MySQL
     * @throws MySQLConnectionException If we can't connect to the database.
     */
    public Map<UUID, PlayerData> getPlayers() throws SQLException, MySQLConnectionException {
        Map<UUID, PlayerData> players = new HashMap<>();
        try (Connection conn = getDataStore().getConnection()) {

            //TODO: we should use prepare statement
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM bcl_playersdata");
            while (rs.next()) {
                try {
                    PlayerData pd = new PlayerData(toUUID(rs.getString(1)), rs.getInt(2), rs.getInt(3));
                    players.put(pd.getPlayerId(), pd);
                } catch (IllegalArgumentException e) {
                    BetterChunkLoader.instance().getLogger().info("We had a problem while loading the player: " + rs.getString(1));

                }
            }
        }
        return players;
    }

    /**
     *
     * @param playerId The player UUID
     * @param worldLoaders the amount of world loaders
     * @param personalLoaders The amount of personal Loaders
     * @throws SQLException if there is any errors with the MySQL
     * @throws MySQLConnectionException If we can't connect to the database.
     */
    public void insertOrUpdatePlayerData(UUID playerId, int worldLoaders, int personalLoaders) throws SQLException, MySQLConnectionException {
        try (Connection conn = getDataStore().getConnection()) {
            //TODO: we should use prepare statement
            conn.createStatement().executeUpdate(
                    "INSERT INTO bcl_playersdata (pid,alwayson,onlineonly)  VALUES (" + UUIDtoString(playerId) + ", " + worldLoaders + "," + personalLoaders + ") ON DUPLICATE KEY "
                            + "UPDATE "
                            + "alwayson=" + worldLoaders + ", onlineonly=" + personalLoaders);
        }
    }

    /**
     * Get the player data by UUID
     * @param uuid the UUID of the player
     * @return PlayerData
     * @throws SQLException if there is any errors with the MySQL
     * @throws MySQLConnectionException If we can't connect to the database.
     * @throws UserNotFound If we can't load a Player by the UUID
     */
    public PlayerData getPlayerDataByUUID(UUID uuid) throws SQLException, MySQLConnectionException, UserNotFound {
        try (Connection conn = getDataStore().getConnection()) {
            //TODO prepared statement!
            String statement = "SELECT * FROM bcl_playersdata WHERE pid=" + UUIDtoString(uuid) + " LIMIT 1";
            ResultSet rs = conn.createStatement().executeQuery(statement);
            PlayerData playerData = new PlayerData(uuid);
            while (rs.next()) {
                playerData.setAlwaysOnChunksAmount(rs.getInt(2));
                playerData.setOnlineOnlyChunksAmount(rs.getInt(3));
            }
            return playerData;
        }
    }

    /**
     * Update/Insert a ChunkLoader to the database
     * @param chunkLoader The entity we want to update or insert
     * @throws SQLException if there is any errors with the MySQL
     * @throws MySQLConnectionException If we can't connect to the database.
     */
    public void insertOrUpdateChunkLoader(CChunkLoader chunkLoader) throws SQLException, MySQLConnectionException {
        try (Connection conn = getDataStore().getConnection()) {
            String statement = "REPLACE INTO bcl_chunkloaders VALUES (\"" + chunkLoader.getLocationString() + "\", " + chunkLoader.getRange() + ", "
                    + UUIDtoString(chunkLoader.getOwner()) + ", " + chunkLoader.getCreationDate().getTime() + ", " + (chunkLoader.isAlwaysOn() ?
                    1 : 0) + ", \"" + Config.getInstance().getServerName() + "\")";

            //TODO: we should use prepare statement
            conn.createStatement().executeUpdate(statement);
        }
    }

    /**
     * Delete a chunkloader from the database.
     * @param chunkLoader The chunkloader to delete
     * @throws SQLException if there is any errors with the MySQL
     * @throws MySQLConnectionException If we can't connect to the database.
     */
    public void deleteChunkLoader(CChunkLoader chunkLoader) throws SQLException, MySQLConnectionException {
        try (Connection conn = getDataStore().getConnection()) {
            //TODO: we should use prepare statement
            conn.createStatement().executeUpdate("DELETE FROM bcl_chunkloaders WHERE loc = \"" + chunkLoader.getLocationString() + "\" LIMIT 1");
        }
    }

    /**
     * Delete all chunk loaders by the owner
     * @param ownerId the UUID of the player
     * @return how many was deleted
     * @throws SQLException if there is any errors with the MySQL
     * @throws MySQLConnectionException If we can't connect to the database.
     */
    public int deleteChunkLoadersByOwner(UUID ownerId) throws SQLException, MySQLConnectionException {
        try (Connection conn = getDataStore().getConnection()) {
            //TODO: we should use prepare statement
            return conn.createStatement().executeUpdate("DELETE FROM bcl_chunkloaders WHERE owner = " + UUIDtoString(ownerId));
        }
    }

    /**
     * Get a dataStore to execute Queries against
     *
     * @return the DataStore
     * @throws SQLException if there is any errors with the MySQL
     * @throws MySQLConnectionException If we can't connect to the database.
     */
    private DataSource getDataStore() throws SQLException, MySQLConnectionException {

        return Sponge.getServiceManager().provide(SqlService.class).orElseThrow(SQLException::new)
                .getDataSource(Config.getInstance().getMySQL().getConnectionString());

    }

    /**
     * transform a string to a UUID
     * TODO: we should find a better way!
     *
     * @param string the UUID string
     * @return a UUID
     */
    private static UUID toUUID(String string) {
        return UUID.fromString(string);
    }

    /**
     * Transform a UUID to a hex string
     * //TODO there must be a better way!
     *
     * @param uuid the UUID you want to transform
     * @return the encoded string
     */
    private static String UUIDtoString(UUID uuid) {
        if (uuid == null) {
            return "";
        }
        return "\"" + uuid.toString() + "\"";
    }
}
