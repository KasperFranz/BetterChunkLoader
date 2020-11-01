package guru.franz.mc.bcl.datastore;

import guru.franz.mc.bcl.config.Config;
import guru.franz.mc.bcl.datastore.database.MySQL;
import guru.franz.mc.bcl.exception.NegativeValueException;
import guru.franz.mc.bcl.exception.UserNotFound;
import guru.franz.mc.bcl.datastore.exceptions.MySQLConnectionException;
import guru.franz.mc.bcl.model.PlayerData;
import guru.franz.mc.bcl.BetterChunkLoader;
import guru.franz.mc.bcl.model.CChunkLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MySqlDataStore extends AHashMapDataStore {
    private MySQL mysql;

    @Override
    public String getName() {
        return "MySQL";
    }

    @Override

    public void load() throws MySQLConnectionException {
        mysql = new MySQL();
        String serverName = Config.getInstance().getServerName();
        try {
            mysql.setupTable();
        } catch (SQLException e) {
            BetterChunkLoader.instance().getLogger().info("Unable to connect to database. Check your config file settings.");
            throw new RuntimeException(e);
        }

        try {
            chunkLoaders = new HashMap<>();
            for (World world : Sponge.getServer().getWorlds()) {
                chunkLoaders.put(world.getName(), mysql.getChunkloadersByServerAndWorld(serverName, world.getName()));
            }

        } catch (SQLException e) {
            BetterChunkLoader.instance().getLogger().info("unable to read chunk loaders data from the MySQL server.");
            throw new RuntimeException(e);
        }

        try {
            this.playersData = mysql.getPlayers();
        } catch (SQLException e) {
            this.playersData = new HashMap<>();
            BetterChunkLoader.instance().getLogger().error("unable to read player data from the MySQL server.");
            throw new MySQLConnectionException(e.getMessage());
        }
    }

    /**
     * Load the chunk loaders based on a specific world.
     *
     * @param world The world we want to load
     */
    public void loadWorld(String world) {
        try {
            List<CChunkLoader> clList = mysql.getChunkloadersByServerAndWorld(Config.getInstance().getServerName(), world);
            chunkLoaders.remove(world);
            chunkLoaders.put(world, clList);
        } catch (SQLException e) {
            BetterChunkLoader.instance().getLogger().info("unable to  load the chunk loaders data from MySQL server. for " + world);
            throw new RuntimeException(e);
        }
    }

    @Override
    public CChunkLoader getChunkLoaderAt(Location<World> blockLocation) {
        //TODO we should be able to do this better!
        for (Map.Entry<String, List<CChunkLoader>> entry : this.chunkLoaders.entrySet()) {
            for (CChunkLoader cChunkLoader : entry.getValue()) {
                if (cChunkLoader.getServerName().equalsIgnoreCase(Config.getInstance().getServerName())) {
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
            PlayerData playerData = mysql.getPlayerDataByUUID(uuid);
            this.playersData.replace(uuid, playerData);
        } catch (SQLException | UserNotFound e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addChunkLoader(CChunkLoader chunkLoader) {
        try {
            mysql.insertOrUpdateChunkLoader(chunkLoader);
            super.addChunkLoader(chunkLoader);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeChunkLoader(CChunkLoader chunkLoader) {
        try {
            mysql.deleteChunkLoader(chunkLoader);
            super.removeChunkLoader(chunkLoader);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int removeChunkLoaders(UUID ownerId) {
        super.removeChunkLoaders(ownerId);
        try {
            return mysql.deleteChunkLoadersByOwner(ownerId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void changeChunkLoaderRange(CChunkLoader chunkLoader, byte range) {
        super.changeChunkLoaderRange(chunkLoader, range);
        try {
            chunkLoader.setRange(range);
            mysql.insertOrUpdateChunkLoader(chunkLoader);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setAlwaysOnChunksLimit(UUID playerId, int amount) throws NegativeValueException {
        super.setAlwaysOnChunksLimit(playerId, amount);
        try {
            PlayerData playerData = this.getPlayerData(playerId);
            mysql.insertOrUpdatePlayerData(playerId, playerData.getAlwaysOnChunksAmount(), playerData.getOnlineOnlyChunksAmount());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setOnlineOnlyChunksLimit(UUID playerId, int amount) throws NegativeValueException {
        super.setOnlineOnlyChunksLimit(playerId, amount);
        try {

            PlayerData playerData = this.getPlayerData(playerId);
            mysql.insertOrUpdatePlayerData(playerId, playerData.getAlwaysOnChunksAmount(), playerData.getOnlineOnlyChunksAmount());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addAlwaysOnChunksLimit(UUID playerId, int amount) {
        super.addAlwaysOnChunksLimit(playerId, amount);
        try {
            PlayerData playerData = this.getPlayerData(playerId);
            mysql.insertOrUpdatePlayerData(playerId, playerData.getAlwaysOnChunksAmount(), playerData.getOnlineOnlyChunksAmount());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addOnlineOnlyChunksLimit(UUID playerId, int amount) {
        super.addOnlineOnlyChunksLimit(playerId, amount);
        try {
            PlayerData playerData = this.getPlayerData(playerId);
            mysql.insertOrUpdatePlayerData(playerId, playerData.getAlwaysOnChunksAmount(), playerData.getOnlineOnlyChunksAmount());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
