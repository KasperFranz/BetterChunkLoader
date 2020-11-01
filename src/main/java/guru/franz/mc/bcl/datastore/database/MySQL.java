package guru.franz.mc.bcl.datastore.database;

import guru.franz.mc.bcl.config.Config;
import guru.franz.mc.bcl.datastore.exceptions.MySQLConnectionException;
import guru.franz.mc.bcl.exception.UserNotFound;
import net.kaikk.mc.bcl.BetterChunkLoader;
import guru.franz.mc.bcl.model.CChunkLoader;
import guru.franz.mc.bcl.model.PlayerData;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
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

            conn.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS bcl_playersdata ("
                    + "pid varchar(36) NOT NULL, "
                    + "alwayson smallint(6) unsigned NOT NULL, "
                    + "onlineonly smallint(6) unsigned NOT NULL, "
                    + "UNIQUE KEY pid (pid));"); //TODO should this not include servername as well?

        }
    }

    /**
     * Get the chunk Loaders by server and world
     *
     * @param serverName the servers name
     * @param world      The world
     * @return a list of chunkloaders
     * @throws SQLException             if there is any errors with the MySQL
     * @throws MySQLConnectionException If we can't connect to the database.
     */
    public List<CChunkLoader> getChunkloadersByServerAndWorld(String serverName, String world) throws SQLException, MySQLConnectionException {
        List<CChunkLoader> chunkLoaders = new ArrayList<>();
        try (Connection conn = getDataStore().getConnection()) {
            String query = "SELECT * FROM bcl_chunkloaders WHERE serverName = ? and loc LIKE ?";

            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, serverName);
            statement.setString(2, world + ":%");
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                try {

                    CChunkLoader chunkLoader =
                            new CChunkLoader(
                                    rs.getString(1),
                                    rs.getByte(2),
                                    UUID.fromString(rs.getString(3)),
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
     *
     * @return all the player data from the database
     * @throws SQLException             if there is any errors with the MySQL
     * @throws MySQLConnectionException If we can't connect to the database.
     */
    public Map<UUID, PlayerData> getPlayers() throws SQLException, MySQLConnectionException {
        Map<UUID, PlayerData> players = new HashMap<>();
        try (Connection conn = getDataStore().getConnection()) {
            String query = "SELECT * from bcl_playersdata";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                try {
                    PlayerData pd = new PlayerData(UUID.fromString(rs.getString(1)), rs.getInt(2), rs.getInt(3));
                    players.put(pd.getPlayerId(), pd);
                } catch (IllegalArgumentException e) {
                    BetterChunkLoader.instance().getLogger().info("We had a problem while loading the player: " + rs.getString(1));

                }
            }
        }
        return players;
    }

    /**
     * @param playerId        The player UUID
     * @param worldLoaders    the amount of world loaders
     * @param personalLoaders The amount of personal Loaders
     * @throws SQLException             if there is any errors with the MySQL
     * @throws MySQLConnectionException If we can't connect to the database.
     */
    public void insertOrUpdatePlayerData(UUID playerId, int worldLoaders, int personalLoaders) throws SQLException, MySQLConnectionException {
        try (Connection conn = getDataStore().getConnection()) {
            String query = "INSERT INTO bcl_playersdata (pid, alwayson, onlineonly) VALUES" +
                    "(?, ?, ?) ON DUPLICATE KEY UPDATE " +
                    "alwayson = ?, onlineonly= ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, playerId.toString());
            statement.setInt(2, worldLoaders);
            statement.setInt(3, personalLoaders);
            statement.setInt(4, worldLoaders);
            statement.setInt(5, personalLoaders);
            statement.executeUpdate();
        }
    }

    /**
     * Get the player data by UUID
     *
     * @param uuid the UUID of the player
     * @return PlayerData
     * @throws SQLException             if there is any errors with the MySQL
     * @throws MySQLConnectionException If we can't connect to the database.
     * @throws UserNotFound             If we can't load a Player by the UUID
     */
    public PlayerData getPlayerDataByUUID(UUID uuid) throws SQLException, MySQLConnectionException, UserNotFound {
        try (Connection conn = getDataStore().getConnection()) {
            String query = "SELECT * FROM bcl_playersdata WHERE pid = ? LIMIT 1";

            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, uuid.toString());
            ResultSet rs = statement.executeQuery();
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
     *
     * @param chunkLoader The entity we want to update or insert
     * @throws SQLException             if there is any errors with the MySQL
     * @throws MySQLConnectionException If we can't connect to the database.
     */
    public void insertOrUpdateChunkLoader(CChunkLoader chunkLoader) throws SQLException, MySQLConnectionException {
        try (Connection conn = getDataStore().getConnection()) {
            String query = "INSERT INTO bcl_chunkloaders (loc, r, owner, date, aon, serverName) VALUES " +
                    "(?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE " +
                    "owner = ?, date= ?, aon = ?, serverName = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, chunkLoader.getLocationString());
            statement.setInt(2, chunkLoader.getRange());
            statement.setString(3, chunkLoader.getOwner().toString());
            statement.setLong(4, chunkLoader.getCreationDate().getTime());
            statement.setInt(5, chunkLoader.isAlwaysOn() ? 1 : 0);
            statement.setString(6, Config.getInstance().getServerName());
            statement.setString(7, chunkLoader.getOwner().toString());
            statement.setLong(8, chunkLoader.getCreationDate().getTime());
            statement.setInt(9, chunkLoader.isAlwaysOn() ? 1 : 0);
            statement.setString(10, Config.getInstance().getServerName());
            statement.executeUpdate();
        }
    }

    /**
     * Delete a chunkloader from the database.
     *
     * @param chunkLoader The chunkloader to delete
     * @throws SQLException             if there is any errors with the MySQL
     * @throws MySQLConnectionException If we can't connect to the database.
     */
    public void deleteChunkLoader(CChunkLoader chunkLoader) throws SQLException, MySQLConnectionException {
        try (Connection conn = getDataStore().getConnection()) {
            String query = "DELETE FROM bcl_chunkloaders WHERE loc = ? LIMIT 1";
            //TODO should use servername as well.
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, chunkLoader.getLocationString());
            statement.executeUpdate();
        }
    }

    /**
     * Delete all chunk loaders by the owner
     *
     * @param ownerId the UUID of the player
     * @return how many was deleted
     * @throws SQLException             if there is any errors with the MySQL
     * @throws MySQLConnectionException If we can't connect to the database.
     */
    public int deleteChunkLoadersByOwner(UUID ownerId) throws SQLException, MySQLConnectionException {
        try (Connection conn = getDataStore().getConnection()) {
            String query = "DELETE FROM bcl_chunkloaders WHERE owner = ? LIMIT 1";
            //TODO should be with servername as well
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, ownerId.toString());
            return statement.executeUpdate();
        }
    }

    /**
     * Get a dataStore to execute Queries against
     *
     * @return the DataStore
     * @throws SQLException             if there is any errors with the MySQL
     * @throws MySQLConnectionException If we can't connect to the database.
     */
    private DataSource getDataStore() throws SQLException, MySQLConnectionException {

        return Sponge.getServiceManager().provide(SqlService.class).orElseThrow(SQLException::new)
                .getDataSource(Config.getInstance().getMySQL().getConnectionString());

    }
}
