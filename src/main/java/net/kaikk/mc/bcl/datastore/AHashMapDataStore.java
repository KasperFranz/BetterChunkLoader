package net.kaikk.mc.bcl.datastore;

import guru.franz.mc.bcl.config.Config;
import guru.franz.mc.bcl.exception.NegativeValueException;
import guru.franz.mc.bcl.exception.UserNotFound;
import guru.franz.mc.bcl.model.PlayerData;
import net.kaikk.mc.bcl.BetterChunkLoader;
import guru.franz.mc.bcl.model.CChunkLoader;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;

/**
 * An implementation of IDataStore that stores data into HashMaps
 * It's abstract because it doesn't write any data on disk: all data will be lost at server shutdown
 * Classes that extend this class should store the data somewhere.
 */
public abstract class AHashMapDataStore implements IDataStore {

    protected Map<String, List<CChunkLoader>> chunkLoaders;
    protected Map<UUID, PlayerData> playersData;

    @Override
    public List<CChunkLoader> getChunkLoaders() {
        List<CChunkLoader> chunkLoaders = new ArrayList<>();
        for (List<CChunkLoader> clList : this.chunkLoaders.values()) {
            chunkLoaders.addAll(clList);
        }
        return chunkLoaders;
    }

    @Override
    public List<CChunkLoader> getChunkLoaders(String worldName) {
        List<CChunkLoader> list = this.chunkLoaders.get(worldName);
        if (list == null) {
            return Collections.emptyList();
        }
        return list;
    }

    @Override
    public List<CChunkLoader> getChunkLoaders(UUID ownerId) {
        List<CChunkLoader> chunkLoaders = new ArrayList<>();
        for (CChunkLoader cl : this.getChunkLoaders()) {
            if (cl.getOwner().equals(ownerId)) {
                chunkLoaders.add(cl);
            }
        }
        return chunkLoaders;
    }

    @Override
    public CChunkLoader getChunkLoaderAt(Location<World> blockLocation) {
        for (CChunkLoader cl : this.getChunkLoaders(blockLocation.getExtent().getName())) {
            if (cl.getLoc().getX() == blockLocation.getX() && cl.getLoc().getZ() == blockLocation.getZ() && cl.getLoc().getY() == blockLocation
                    .getY()) {
                return cl;
            }
        }
        return null;
    }

    @Override
    public void addChunkLoader(CChunkLoader chunkLoader) {
        List<CChunkLoader> clList = this.chunkLoaders.computeIfAbsent(chunkLoader.getWorldName(), k -> new ArrayList<>());

        clList.add(chunkLoader);
        if (chunkLoader.getServerName().equalsIgnoreCase(Config.getInstance().getServerName())) {
            chunkLoader.getPlayer().spawnParticles(ParticleEffect.builder().type(ParticleTypes.MOBSPAWNER_FLAMES).quantity(10).build(),
                    chunkLoader.getLoc().getPosition());
            if (chunkLoader.isLoadable()) {
                BetterChunkLoader.instance().loadChunks(chunkLoader);
            }
        }
    }

    @Override
    public void removeChunkLoader(CChunkLoader chunkLoader) {
        List<CChunkLoader> clList = this.chunkLoaders.get(chunkLoader.getWorldName());
        if (clList != null) {
            if (chunkLoader.blockCheck()) {
                chunkLoader.getPlayer().spawnParticles(ParticleEffect.builder().type(ParticleTypes.SPLASH_POTION).quantity(10).build(),
                        chunkLoader.getLoc().getPosition());
            }
            clList.remove(chunkLoader);
            if (chunkLoader.getServerName().equalsIgnoreCase(Config.getInstance().getServerName())) {
                BetterChunkLoader.instance().unloadChunks(chunkLoader);
            }
        }
    }

    @Override
    public int removeChunkLoaders(UUID ownerId) {
        List<CChunkLoader> clList = this.getChunkLoaders(ownerId);
        for (CChunkLoader cl : clList) {
            this.getChunkLoaders(cl.getWorldName()).remove(cl);
        }
        return clList.size();
    }

    @Override
    public void changeChunkLoaderRange(CChunkLoader chunkLoader, byte range) {
        if (chunkLoader.isLoadable()) {
            BetterChunkLoader.instance().unloadChunks(chunkLoader);
        }

        chunkLoader.setRange(range);

        if (chunkLoader.isLoadable()) {
            BetterChunkLoader.instance().loadChunks(chunkLoader);
        }
    }

    @Override
    public int getAlwaysOnFreeChunksAmount(UUID playerId) {
        int clAmount = this.getPlayerData(playerId).getAlwaysOnChunksAmount();
        for (CChunkLoader cl : this.getChunkLoaders(playerId)) {
            if (cl.isAlwaysOn()) {
                clAmount -= cl.getSize();
            }
        }

        return clAmount;
    }

    @Override
    public int getOnlineOnlyFreeChunksAmount(UUID playerId) {
        int clAmount = this.getPlayerData(playerId).getOnlineOnlyChunksAmount();
        for (CChunkLoader cl : this.getChunkLoaders(playerId)) {
            if (!cl.isAlwaysOn()) {
                clAmount -= cl.getSize();
            }
        }

        return clAmount;
    }

    @Override
    public void setAlwaysOnChunksLimit(UUID playerId, int amount) throws NegativeValueException {
        if (amount < 0) {
            throw new NegativeValueException();
        }
        PlayerData playerData = this.getPlayerData(playerId);
        playerData.setAlwaysOnChunksAmount(amount);
    }

    @Override
    public void setOnlineOnlyChunksLimit(UUID playerId, int amount) throws NegativeValueException {
        if (amount < 0) {
            throw new NegativeValueException();
        }
        PlayerData playerData = this.getPlayerData(playerId);
        playerData.setOnlineOnlyChunksAmount(amount);
    }

    @Override
    public void addAlwaysOnChunksLimit(UUID playerId, int amount) {
        PlayerData playerData = this.getPlayerData(playerId);
        playerData.setAlwaysOnChunksAmount(playerData.getAlwaysOnChunksAmount() + amount);
    }

    @Override
    public void addOnlineOnlyChunksLimit(UUID playerId, int amount) {
        PlayerData playerData = this.getPlayerData(playerId);
        playerData.setOnlineOnlyChunksAmount(playerData.getOnlineOnlyChunksAmount() + amount);
    }

    @Override
    public PlayerData getPlayerData(UUID playerId) {
        PlayerData playerData = this.playersData.get(playerId);
        if (playerData == null) {
            try {
                playerData = new PlayerData(playerId);
            } catch (UserNotFound e) {
                BetterChunkLoader.instance().getLogger().info("No user found");
            }
            this.playersData.put(playerId, playerData);
        }
        return playerData;
    }

    @Override
    public List<PlayerData> getPlayersData() {
        return new ArrayList<>(this.playersData.values());
    }
}
