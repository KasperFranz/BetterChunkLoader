package net.kaikk.mc.bcl.datastore;

import net.kaikk.mc.bcl.CChunkLoader;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.UUID;

/** Interface for BetterChunkLoader's data store<br>
 * How to create custom data store:<br>
 * - Make a class that implements this interface<br>
 * - Call DataStoreManager.registerDataStore(name, class) during your plugin's onLoad()
 *  */
public interface IDataStore {

    /** Returns the data store name */
    public abstract String getName();

    /** Loads data from the datastore.
     *  This is called while BCL is loading */
    public abstract void load();

    /** Get chunk loaders */
    public abstract List<CChunkLoader> getChunkLoaders();

    /** Get chunk loaders for dimension id */
    public abstract List<CChunkLoader> getChunkLoaders(String worldName);

    /** Get chunk loaders at specified chunk */
    public abstract List<CChunkLoader> getChunkLoadersAt(String worldName, int chunkX, int chunkZ);

    /** Get chunk loaders owned by someone with the specified UUID */
    public abstract List<CChunkLoader> getChunkLoaders(UUID ownerId);

    /** Get chunk loader at specified location */
    public abstract CChunkLoader getChunkLoaderAt(Location<World> blockLocation);

    /** Add a new chunk loader */
    public abstract void addChunkLoader(CChunkLoader chunkLoader);

    /** refresh balance on login */
    public abstract void refreshPlayer(UUID uuid);

    /** Remove chunk loader */
    public abstract void removeChunkLoader(CChunkLoader chunkLoader);

    /** Remove chunk loaders owned by someone with the specified UUID */
    public abstract void removeChunkLoaders(UUID ownerId);

    /** Change chunk loader range */
    public abstract void changeChunkLoaderRange(CChunkLoader chunkLoader, byte range);

    /** Get the amount of free always on chunks that this player can still load until he reaches his chunks limit */
    public abstract int getAlwaysOnFreeChunksAmount(UUID playerId);

    /** Get the amount of free online only chunks that this player can still load until he reaches his chunks limit */
    public abstract int getOnlineOnlyFreeChunksAmount(UUID playerId);

    /** Set the max amount of always on chunks that this player can load */
    public abstract void setAlwaysOnChunksLimit(UUID playerId, int amount);

    /** Set the max amount of online only chunks that this player can load */
    public abstract void setOnlineOnlyChunksLimit(UUID playerId, int amount);

    /** Add an amount of chunks to the max amount of always on chunks that this player can load */
    public abstract void addAlwaysOnChunksLimit(UUID playerId, int amount);

    /** Add an amount of chunks to the max amount of online only chunks that this player can load */
    public abstract void addOnlineOnlyChunksLimit(UUID playerId, int amount);

    /** Get the player data */
    public abstract PlayerData getPlayerData(UUID playerId);

    /** Get players data*/
    public abstract List<PlayerData> getPlayersData();
}
