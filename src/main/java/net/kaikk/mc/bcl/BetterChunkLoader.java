package net.kaikk.mc.bcl;

import java.io.File;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoField;
import java.util.Optional;
import java.util.UUID;

import com.google.inject.Inject;
import net.kaikk.mc.bcl.datastore.DataStoreManager;
import net.kaikk.mc.bcl.datastore.MySqlDataStore;
import net.kaikk.mc.bcl.datastore.XmlDataStore;
import net.kaikk.mc.bcl.forgelib.BCLForgeLib;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;

@Plugin(id = "betterchunkloader", name = "BetterChunkLoader", version = "1.0")
public class BetterChunkLoader {
	private static BetterChunkLoader instance;
	private Config config;
	@Inject
	private Logger logger;

	public void onLoad() {
		// Register XML DataStore
		DataStoreManager.registerDataStore("XML", XmlDataStore.class);
		
		// Register MySQL DataStore
		DataStoreManager.registerDataStore("MySQL", MySqlDataStore.class);
	}

	@Listener
	public void onServerStart(GameStartedServerEvent event) {
		// check if forge is running
		try {
			Class.forName("net.minecraftforge.common.ForgeVersion");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("BCLForgeLib are needed to run this plugin!");
		}
		
		// check if BCLForgeLib is present
		try {
			Class.forName("net.kaikk.mc.bcl.forgelib.BCLForgeLib");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("BCLForgeLib is needed to run this plugin!");
		}
		
		instance=this;
		
		try {
			// load config
			logger.info("Loading config...");
			this.config = new Config(this);
			
			// instantiate data store, if needed
			if (DataStoreManager.getDataStore()==null) {
				DataStoreManager.setDataStoreInstance(config.dataStore);
			}
			
			// load datastore
			logger.info("Loading "+DataStoreManager.getDataStore().getName()+" Data Store...");
			DataStoreManager.getDataStore().load();
			
			logger.info("Loaded "+DataStoreManager.getDataStore().getChunkLoaders().size()+" chunk loaders data.");
			logger.info("Loaded "+DataStoreManager.getDataStore().getPlayersData().size()+" players data.");
			
			// load always on chunk loaders
			int count=0;
			for (CChunkLoader cl : DataStoreManager.getDataStore().getChunkLoaders()) {
				if (cl.getServerName().equalsIgnoreCase(BetterChunkLoader.instance().config().serverName)) {
					if (cl.isLoadable()) {
						BCLForgeLib.instance().addChunkLoader(cl);
						count++;
					}
				}
			}
			
			logger.info("Loaded "+count+" always-on chunk loaders.");
			
			logger.info("Loading Listeners...");
			initializeListeners();
			initializeCommands();
			
			logger.info("Load complete.");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Load failed!");
			//TODO: DISABLE PLUGIN HERE
		}
	}
	
	public void onDisable() {
		for (CChunkLoader cl : DataStoreManager.getDataStore().getChunkLoaders()) {
			if (cl.getServerName().equalsIgnoreCase(BetterChunkLoader.instance().config().serverName)) {
				BCLForgeLib.instance().removeChunkLoader(cl);
			}
		}
		instance=null;
	}

	private void initializeListeners() {
		Sponge.getEventManager().registerListeners(this, new EventListener());
	}

	private void initializeCommands() {
		CommandSpec bclSpec = CommandSpec.builder()
				.description(Text.of("BetterChunkloader Main Command"))
				.arguments(GenericArguments.onlyOne(GenericArguments.integer(Text.of("tagInt"))))
				.executor(new CommandExec(this))
				.build();

		Sponge.getCommandManager().register(this, bclSpec, "betterchunkloader", "bcl");
	}

	public static BetterChunkLoader instance() {
		return instance;
	}
	
	public static long getPlayerLastPlayed(UUID playerId) {
		Optional<Player> onlinePlayer = Sponge.getServer().getPlayer(playerId);
		if(onlinePlayer.isPresent()) {
			return 0L; //player is online
		} else {
			Optional<UserStorageService> userStorage = Sponge.getServiceManager().provide(UserStorageService.class);
			Optional<User> optUser = userStorage.get().get(playerId);
			if (optUser.isPresent()) {
				//TODO: Add more optional handling here.
				User user = optUser.get();
				Instant lastPlayed = user.getPlayer().get().lastPlayed().get();
				return lastPlayed.getLong(ChronoField.NANO_OF_SECOND);
			} else {
				return getPlayerDataLastModified(playerId);
			}
		}
		//return 0;
	}
	
	public static long getPlayerDataLastModified(UUID playerId) {
		String name = Sponge.getServer().getDefaultWorldName();
		Path path = Sponge.getGame().getGameDirectory().resolve("world/"+name);
		File playerData =new File(path.toString(), "playerdata"+File.separator+playerId.toString()+".dat");
		if (playerData.exists()) {
			return playerData.lastModified();
		}
		return 0;
	}
	
	public Config config() {
		return this.config;
	}
}
