package guru.franz.mc.bcl.datastore;

import guru.franz.mc.bcl.config.Config;
import guru.franz.mc.bcl.datastore.database.DatabaseInterface;
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

public abstract class DatabaseDataStore extends AHashMapDataStore {
    protected DatabaseInterface database;

    public void load() throws MySQLConnectionException {
        String serverName = Config.getInstance().getServerName();
        try {
            database.setupTable();
        } catch (SQLException e) {
            BetterChunkLoader.instance().getLogger().info("Unable to connect to the database. Please verify the connection information.");
            throw new RuntimeException(e);
        }

        try {
            chunkLoaders = new HashMap<>();
            for (World world : Sponge.getServer().getWorlds()) {
                chunkLoaders.put(world.getName(), database.getChunkloadersByServerAndWorld(serverName, world.getName()));
            }

        } catch (SQLException e) {
            BetterChunkLoader.instance().getLogger().info("unable to read chunk loaders data from the database.");
            throw new RuntimeException(e);
        }

        try {
            this.playersData = database.getPlayers();
        } catch (SQLException e) {
            this.playersData = new HashMap<>();
            BetterChunkLoader.instance().getLogger().error("unable to read player data from the database.");
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
            List<CChunkLoader> clList = database.getChunkloadersByServerAndWorld(Config.getInstance().getServerName(), world);
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
            PlayerData playerData = database.getPlayerDataByUUID(uuid);
            this.playersData.replace(uuid, playerData);
        } catch (SQLException | UserNotFound e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addChunkLoader(CChunkLoader chunkLoader) {
        try {
            database.insertOrUpdateChunkLoader(chunkLoader);
            super.addChunkLoader(chunkLoader);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeChunkLoader(CChunkLoader chunkLoader) {
        try {
            database.deleteChunkLoader(chunkLoader);
            super.removeChunkLoader(chunkLoader);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int removeChunkLoaders(UUID ownerId) {
        super.removeChunkLoaders(ownerId);
        try {
            return database.deleteChunkLoadersByOwner(ownerId);
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
            database.insertOrUpdateChunkLoader(chunkLoader);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setAlwaysOnChunksLimit(UUID playerId, int amount) throws NegativeValueException {
        super.setAlwaysOnChunksLimit(playerId, amount);
        try {
            PlayerData playerData = this.getPlayerData(playerId);
            database.insertOrUpdatePlayerData(playerId, playerData.getAlwaysOnChunksAmount(), playerData.getOnlineOnlyChunksAmount());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setOnlineOnlyChunksLimit(UUID playerId, int amount) throws NegativeValueException {
        super.setOnlineOnlyChunksLimit(playerId, amount);
        try {

            PlayerData playerData = this.getPlayerData(playerId);
            database.insertOrUpdatePlayerData(playerId, playerData.getAlwaysOnChunksAmount(), playerData.getOnlineOnlyChunksAmount());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addAlwaysOnChunksLimit(UUID playerId, int amount) {
        super.addAlwaysOnChunksLimit(playerId, amount);
        try {
            PlayerData playerData = this.getPlayerData(playerId);
            database.insertOrUpdatePlayerData(playerId, playerData.getAlwaysOnChunksAmount(), playerData.getOnlineOnlyChunksAmount());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addOnlineOnlyChunksLimit(UUID playerId, int amount) {
        super.addOnlineOnlyChunksLimit(playerId, amount);
        try {
            PlayerData playerData = this.getPlayerData(playerId);
            database.insertOrUpdatePlayerData(playerId, playerData.getAlwaysOnChunksAmount(), playerData.getOnlineOnlyChunksAmount());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
