package guru.franz.mc.bcl.datastore;

import guru.franz.mc.bcl.datastore.exceptions.MySQLConnectionException;
import guru.franz.mc.bcl.exception.Exception;
import guru.franz.mc.bcl.exception.NegativeValueException;
import guru.franz.mc.bcl.model.CChunkLoader;
import guru.franz.mc.bcl.model.PlayerData;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.UUID;

/**
 * Interface for BetterChunkLoader's DataStore<br>
 * How to create custom DataStore:<br>
 * - Make a class that implements this interface<br>
 * - Call DataStoreManager.registerDataStore(name, class) during your plugin's onLoad()
 */
public interface IDataStore {

    /**
     * Returns the DataStore name
     */
    String getName();

    /**
     * Loads data from the datastore.
     * This is called while BCL is loading
     */
    void load() throws Exception, MySQLConnectionException;

    /**
     * Loads data from the datastore.
     * This is called when a world is loaded
     */
    void loadWorld(String world) throws RuntimeException;

    /**
     * Get chunk loaders
     */
    List<CChunkLoader> getChunkLoaders();

    /**
     * Get chunk loaders for dimension id
     */
    List<CChunkLoader> getChunkLoaders(String worldName);

    /**
     * Get chunk loaders owned by someone with the specified UUID
     */
    List<CChunkLoader> getChunkLoaders(UUID ownerId);

    /**
     * Get chunk loader at specified location
     */
    CChunkLoader getChunkLoaderAt(Location<World> blockLocation);

    /**
     * Add a new chunk loader
     */
    void addChunkLoader(CChunkLoader chunkLoader);

    /**
     * refresh balance on login
     */
    void refreshPlayer(UUID uuid);

    /**
     * Remove chunk loader
     */
    void removeChunkLoader(CChunkLoader chunkLoader);

    /**
     * Remove chunk loaders owned by someone with the specified UUID
     */
    int removeChunkLoaders(UUID ownerId);

    /**
     * Change chunk loader range
     */
    void changeChunkLoaderRange(CChunkLoader chunkLoader, byte range);

    /**
     * Get the amount of free always on chunks that this player can still load until he reaches his chunks limit
     */
    int getAlwaysOnFreeChunksAmount(UUID playerId);

    /**
     * Get the amount of free online only chunks that this player can still load until he reaches his chunks limit
     */
    int getOnlineOnlyFreeChunksAmount(UUID playerId);

    /**
     * Set the max amount of always on chunks that this player can load
     */
    void setAlwaysOnChunksLimit(UUID playerId, int amount) throws NegativeValueException;

    /**
     * Set the max amount of online only chunks that this player can load
     */
    void setOnlineOnlyChunksLimit(UUID playerId, int amount) throws NegativeValueException;

    /**
     * Add an amount of chunks to the max amount of always on chunks that this player can load
     */
    void addAlwaysOnChunksLimit(UUID playerId, int amount);

    /**
     * Add an amount of chunks to the max amount of online only chunks that this player can load
     */
    void addOnlineOnlyChunksLimit(UUID playerId, int amount);

    /**
     * Get the player data
     */
    PlayerData getPlayerData(UUID playerId);

    /**
     * Get players data
     */
    List<PlayerData> getPlayersData();
}
