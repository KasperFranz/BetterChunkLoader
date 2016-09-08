package net.kaikk.mc.bcl.datastore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.kaikk.mc.bcl.CChunkLoader;

/** An implementation of IDataStore that stores data into HashMaps
 * It's abstract because it doesn't write any data on disk: all data will be lost at server shutdown
 * Classes that extend this class should store the data somewhere. */
public abstract class AHashMapDataStore implements IDataStore {
	protected Map<String, List<CChunkLoader>> chunkLoaders;
	protected Map<UUID, PlayerData> playersData;
	
	@Override
	public List<CChunkLoader> getChunkLoaders() {
		List<CChunkLoader> chunkLoaders = new ArrayList<CChunkLoader>();
		for (List<CChunkLoader> clList : this.chunkLoaders.values()) {
			chunkLoaders.addAll(clList);
		}
		return chunkLoaders;
	}

	@Override
	public List<CChunkLoader> getChunkLoaders(String worldName) {
		List<CChunkLoader> list = this.chunkLoaders.get(worldName);
		if (list==null) {
			return Collections.emptyList();
		}
		return list;
	}

	@Override
	public List<CChunkLoader> getChunkLoaders(UUID ownerId) {
		List<CChunkLoader> chunkLoaders = new ArrayList<CChunkLoader>();
		for (CChunkLoader cl : this.getChunkLoaders()) {
			if (cl.getOwner().equals(ownerId)) {
				chunkLoaders.add(cl);
			}
		}
		return chunkLoaders;
	}


	@Override
	public int getAlwaysOnFreeChunksAmount(UUID playerId) {
		int clAmount=this.getPlayerData(playerId).getAlwaysOnChunksAmount();
		for (CChunkLoader cl : this.getChunkLoaders(playerId)) {
			if (cl.isAlwaysOn()) {
				clAmount-=cl.size();
			}
		}
		
		return clAmount;
	}

	@Override
	public int getOnlineOnlyFreeChunksAmount(UUID playerId) {
		int clAmount=this.getPlayerData(playerId).getOnlineOnlyChunksAmount();
		for (CChunkLoader cl : this.getChunkLoaders(playerId)) {
			if (!cl.isAlwaysOn()) {
				clAmount-=cl.size();
			}
		}
		
		return clAmount;
	}

	@Override
	public void setAlwaysOnChunksLimit(UUID playerId, int amount) {
		PlayerData playerData = this.getPlayerData(playerId);
		playerData.setAlwaysOnChunksAmount(amount);
	}

	@Override
	public void setOnlineOnlyChunksLimit(UUID playerId, int amount) {
		PlayerData playerData = this.getPlayerData(playerId);
		playerData.setOnlineOnlyChunksAmount(amount);
	}

	@Override
	public void addAlwaysOnChunksLimit(UUID playerId, int amount) {
		PlayerData playerData = this.getPlayerData(playerId);
		playerData.setAlwaysOnChunksAmount(playerData.getAlwaysOnChunksAmount()+amount);
	}

	@Override
	public void addOnlineOnlyChunksLimit(UUID playerId, int amount) {
		PlayerData playerData = this.getPlayerData(playerId);
		playerData.setOnlineOnlyChunksAmount(playerData.getOnlineOnlyChunksAmount()+amount);
	}
	
	@Override
	public PlayerData getPlayerData(UUID playerId) {
		PlayerData playerData = this.playersData.get(playerId);
		if (playerData==null) {
			playerData = new PlayerData(playerId);
			this.playersData.put(playerId, playerData);
		}
		return playerData;
	}
	
	@Override
	public List<PlayerData> getPlayersData() {
		return new ArrayList<PlayerData>(this.playersData.values());
	}
}
