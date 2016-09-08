package net.kaikk.mc.bcl.datastore;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.kaikk.mc.bcl.BetterChunkLoader;
import net.kaikk.mc.bcl.CChunkLoader;


public class XmlDataStore extends AHashMapDataStore {
	@Override
	public String getName() {
		return "XML";
	}
	
	@Override
	public void load() {
		this.loadChunkLoaders();
		this.loadPlayersData();
	}
	
	public void loadChunkLoaders() {
		this.chunkLoaders=new HashMap<>();
		File file = getChunkLoadersDataFile();
		if (file.exists() && file.length()!=0) {
			try {
				JAXBContext jaxbContext = JAXBContext.newInstance(XmlChunkLoadersData.class);
				
				// read file
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				XmlChunkLoadersData data = (XmlChunkLoadersData) jaxbUnmarshaller.unmarshal(file);
				if (data.chunkLoaders!=null) {
					for (CChunkLoader cl : data.chunkLoaders) {
						List<CChunkLoader> clList = this.chunkLoaders.get(cl.getWorldName());
						if (clList==null) {
							clList = new ArrayList<CChunkLoader>();
							this.chunkLoaders.put(cl.getWorldName(), clList);
						}
						clList.add(cl);
					}
				}
			} catch (JAXBException e) {
				BetterChunkLoader.instance().getLogger().warning("ChunkLoaders XML file '"+file.getName()+"' is not valid!");
				e.printStackTrace();
			}
		}
	}

	public void saveChunkLoaders() {
		try {
			JAXBContext jaxbContext=JAXBContext.newInstance(XmlChunkLoadersData.class);
			Marshaller jaxbChunkLoadersMarshaller = jaxbContext.createMarshaller();
			jaxbChunkLoadersMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			
			XmlChunkLoadersData data = new XmlChunkLoadersData();
			data.chunkLoaders=this.getChunkLoaders();
			jaxbChunkLoadersMarshaller.marshal(data, getChunkLoadersDataFile());
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void loadPlayersData() {
		this.playersData=new HashMap<>();
		File file = getPlayersDataFile();
		
		if (file.exists() && file.length()!=0) {
			try {
				JAXBContext jaxbContext=JAXBContext.newInstance(XmlPlayersData.class);
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				try {
					XmlPlayersData data = (XmlPlayersData) jaxbUnmarshaller.unmarshal(file);
					if (data.playersData!=null) {
						for (PlayerData pd : data.playersData) {
							this.playersData.put(pd.getPlayerId(), pd);
						}
					}
				} catch (JAXBException e) {
					BetterChunkLoader.instance().getLogger().warning("PlayersData XML file '"+file.getName()+"' is not valid!");
					e.printStackTrace();
				}
			} catch (JAXBException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public void savePlayersData() {
		try {
			JAXBContext jaxbContext=JAXBContext.newInstance(XmlPlayersData.class);
			Marshaller jaxbPlayersDataMarshaller = jaxbContext.createMarshaller();
			jaxbPlayersDataMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			
			XmlPlayersData xpd = new XmlPlayersData();
			xpd.playersData=new ArrayList<PlayerData>(this.playersData.values());
			jaxbPlayersDataMarshaller.marshal(xpd, getPlayersDataFile());
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setAlwaysOnChunksLimit(UUID playerId, int amount) {
		super.setAlwaysOnChunksLimit(playerId, amount);
		this.savePlayersData();
	}

	@Override
	public void setOnlineOnlyChunksLimit(UUID playerId, int amount) {
		super.setOnlineOnlyChunksLimit(playerId, amount);
		this.savePlayersData();
	}

	@Override
	public void addAlwaysOnChunksLimit(UUID playerId, int amount) {
		super.addAlwaysOnChunksLimit(playerId, amount);
		this.savePlayersData();
	}

	@Override
	public void addOnlineOnlyChunksLimit(UUID playerId, int amount) {
		super.addOnlineOnlyChunksLimit(playerId, amount);
		this.savePlayersData();
	}
	

	public static File getChunkLoadersDataFile() {
		return new File(BetterChunkLoader.instance().getDataFolder(), "ChunkLoaders.xml");
	}
	
	public static File getPlayersDataFile() {
		return new File(BetterChunkLoader.instance().getDataFolder(), "PlayersData.xml");
	}
	
	@XmlRootElement(name="ChunkLoadersData")
	@XmlAccessorType(XmlAccessType.FIELD)
	private static class XmlChunkLoadersData {
		@XmlElement(name="cl")
		private List<CChunkLoader> chunkLoaders;
	}
	
	@XmlRootElement(name="PlayersData")
	@XmlAccessorType(value=XmlAccessType.FIELD)
	private static class XmlPlayersData {
		@XmlElement(name="pd")
		private List<PlayerData> playersData;
	}


}
