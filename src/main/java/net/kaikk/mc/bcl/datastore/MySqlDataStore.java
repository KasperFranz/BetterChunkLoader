package net.kaikk.mc.bcl.datastore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import net.kaikk.mc.bcl.BetterChunkLoader;
import net.kaikk.mc.bcl.CChunkLoader;

public class MySqlDataStore extends AHashMapDataStore {
	private Connection dbConnection;
	
	@Override
	public String getName() {
		return "MySQL";
	}

	@Override
	public void load() {
		try {
			// load the java driver for mySQL
			Class.forName("com.mysql.jdbc.Driver");
		} catch (final Exception e) {
			BetterChunkLoader.instance().getLogger().warning("Unable to load MySQL database driver. Make sure you've installed it properly.");
			throw new RuntimeException(e);
		}
		
		try {
			// init connection
			this.refreshConnection();
		} catch (final Exception e) {
			BetterChunkLoader.instance().getLogger().warning("Unable to connect to database. Check your config file settings.");
			throw new RuntimeException(e);
		}
		
		// create table, if not exists
		try {
			this.statement().executeUpdate("CREATE TABLE IF NOT EXISTS bcl_chunkloaders ("
					+ "loc varchar(50) NOT NULL, "
					+ "r tinyint(3) unsigned NOT NULL, "
					+ "owner binary(16) NOT NULL, "
					+ "date bigint(20) NOT NULL, "
					+ "aon tinyint(1) NOT NULL, "
					+ "UNIQUE KEY loc (loc));");
			
			this.statement().executeUpdate("CREATE TABLE IF NOT EXISTS bcl_playersdata ("
					+ "pid binary(16) NOT NULL, "
					+ "alwayson smallint(6) unsigned NOT NULL, "
					+ "onlineonly smallint(6) unsigned NOT NULL, "
					+ "UNIQUE KEY pid (pid));");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
		// load data
		this.chunkLoaders = new HashMap<String, List<CChunkLoader>>();
		try {
			ResultSet rs = this.statement().executeQuery("SELECT * FROM bcl_chunkloaders");
			while(rs.next()) {
				CChunkLoader chunkLoader = new CChunkLoader(rs.getString(1), rs.getByte(2), toUUID(rs.getBytes(3)), new Date(rs.getLong(4)), rs.getBoolean(5));
				List<CChunkLoader> clList = this.chunkLoaders.get(chunkLoader.getWorldName());
				if (clList==null) {
					clList=new ArrayList<CChunkLoader>();
					chunkLoaders.put(chunkLoader.getWorldName(), clList);
				}
				clList.add(chunkLoader);
			}
		} catch (SQLException e) {
			BetterChunkLoader.instance().getLogger().warning("Couldn't read chunk loaders data from MySQL server.");
			throw new RuntimeException(e);
		}
		
		this.playersData = new HashMap<UUID, PlayerData>();
		try {
			ResultSet rs = this.statement().executeQuery("SELECT * FROM bcl_playersdata");
			while(rs.next()) {
				PlayerData pd = new PlayerData(toUUID(rs.getBytes(1)), rs.getInt(2), rs.getInt(3));
				this.playersData.put(pd.getPlayerId(), pd);
			}
		} catch (SQLException e) {
			BetterChunkLoader.instance().getLogger().warning("Couldn't read players data from MySQL server.");
			throw new RuntimeException(e);
		}
	}

	@Override
	public void addChunkLoader(CChunkLoader chunkLoader) {
		super.addChunkLoader(chunkLoader);
		try {
			this.statement().executeUpdate("REPLACE INTO bcl_chunkloaders VALUES (\""+chunkLoader.getLocationString()+"\", "+chunkLoader.getRange()+", "+UUIDtoHexString(chunkLoader.getOwner())+", "+chunkLoader.getCreationDate().getTime()+", "+(chunkLoader.isAlwaysOn()?1:0)+")");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void removeChunkLoader(CChunkLoader chunkLoader) {
		super.removeChunkLoader(chunkLoader);
		try {
			this.statement().executeUpdate("DELETE FROM bcl_chunkloaders WHERE loc = \""+chunkLoader.getLocationString()+"\" LIMIT 1");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void removeChunkLoaders(UUID ownerId) {
		super.removeChunkLoaders(ownerId);
		try {
			this.statement().executeUpdate("DELETE FROM bcl_chunkloaders WHERE owner = "+UUIDtoHexString(ownerId));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void changeChunkLoaderRange(CChunkLoader chunkLoader, byte range) {
		super.changeChunkLoaderRange(chunkLoader, range);
		try {
			this.statement().executeUpdate("UPDATE bcl_chunkloaders SET r = "+range+" WHERE loc = \""+chunkLoader.getLocationString()+"\" LIMIT 1");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setAlwaysOnChunksLimit(UUID playerId, int amount) {
		super.setAlwaysOnChunksLimit(playerId, amount);
		try {
			this.statement().executeUpdate("INSERT INTO bcl_playersdata VALUES ("+UUIDtoHexString(playerId)+", "+amount+", "+BetterChunkLoader.instance().config().defaultChunksAmountOnlineOnly+") ON DUPLICATE KEY UPDATE alwayson="+amount);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setOnlineOnlyChunksLimit(UUID playerId, int amount) {
		super.setOnlineOnlyChunksLimit(playerId, amount);
		try {
			this.statement().executeUpdate("INSERT INTO bcl_playersdata VALUES ("+UUIDtoHexString(playerId)+", "+BetterChunkLoader.instance().config().defaultChunksAmountAlwaysOn+", "+amount+") ON DUPLICATE KEY UPDATE onlineonly="+amount);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addAlwaysOnChunksLimit(UUID playerId, int amount) {
		super.addAlwaysOnChunksLimit(playerId, amount);
		try {
			this.statement().executeUpdate("INSERT INTO bcl_playersdata VALUES ("+UUIDtoHexString(playerId)+", "+amount+", "+BetterChunkLoader.instance().config().defaultChunksAmountOnlineOnly+") ON DUPLICATE KEY UPDATE alwayson=alwayson+"+amount);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addOnlineOnlyChunksLimit(UUID playerId, int amount) {
		super.addOnlineOnlyChunksLimit(playerId, amount);
		try {
			this.statement().executeUpdate("INSERT INTO bcl_playersdata VALUES ("+UUIDtoHexString(playerId)+", "+BetterChunkLoader.instance().config().defaultChunksAmountAlwaysOn+", "+amount+") ON DUPLICATE KEY UPDATE onlineonly=onlineonly+"+amount);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void refreshConnection() throws SQLException {
		if (this.dbConnection == null || this.dbConnection.isClosed()) {
			// set username/pass properties
			final Properties connectionProps = new Properties();
			connectionProps.put("user", BetterChunkLoader.instance().config().mySqlUsername);
			connectionProps.put("password", BetterChunkLoader.instance().config().mySqlPassword);
			connectionProps.put("autoReconnect", "true");
			connectionProps.put("maxReconnects", "4");

			// establish connection
			this.dbConnection = DriverManager.getConnection("jdbc:mysql://"+BetterChunkLoader.instance().config().mySqlHostname+"/"+BetterChunkLoader.instance().config().mySqlDatabase, connectionProps);
		}
	}
	
	private Statement statement() throws SQLException {
		return this.dbConnection.createStatement();
	}
	
	/** Converts an array of 16 bytes to an UUID */
	public static UUID toUUID(byte[] bytes) {
		if (bytes.length != 16) {
			throw new IllegalArgumentException();
		}
		int i = 0;
		long msl = 0;
		for (; i < 8; i++) {
			msl = (msl << 8) | (bytes[i] & 0xFF);
		}
		long lsl = 0;
		for (; i < 16; i++) {
			lsl = (lsl << 8) | (bytes[i] & 0xFF);
		}
		return new UUID(msl, lsl);
	}

	/** Converts an UUID to an hex number using the 0x format */
	public static String UUIDtoHexString(UUID uuid) {
		if (uuid == null) {
			return "0";
		}
		return "0x" + org.apache.commons.lang.StringUtils.leftPad(Long.toHexString(uuid.getMostSignificantBits()), 16, "0") + org.apache.commons.lang.StringUtils.leftPad(Long.toHexString(uuid.getLeastSignificantBits()), 16, "0");
	}
}
