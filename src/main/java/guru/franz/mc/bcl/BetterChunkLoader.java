package guru.franz.mc.bcl;

import com.google.inject.Inject;
import guru.franz.mc.bcl.command.BCL;
import guru.franz.mc.bcl.command.Balance;
import guru.franz.mc.bcl.command.Chunks;
import guru.franz.mc.bcl.command.Delete;
import guru.franz.mc.bcl.command.Info;
import guru.franz.mc.bcl.command.ListCommand;
import guru.franz.mc.bcl.command.Purge;
import guru.franz.mc.bcl.command.Reload;
import guru.franz.mc.bcl.command.elements.ChunksChangeOperatorElement;
import guru.franz.mc.bcl.command.elements.LoaderTypeElement;
import guru.franz.mc.bcl.config.Config;
import guru.franz.mc.bcl.datastore.DataStoreManager;
import guru.franz.mc.bcl.datastore.H2DataStore;
import guru.franz.mc.bcl.datastore.MySQLDataStore;
import guru.franz.mc.bcl.model.CChunkLoader;
import guru.franz.mc.bcl.utils.Messenger;
import guru.franz.mc.bcl.utils.Permission;
import net.kaikk.mc.bcl.forgelib.BCLForgeLib;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Plugin(id = BetterChunkLoaderPluginInfo.ID,
        name = BetterChunkLoaderPluginInfo.NAME,
        description = BetterChunkLoaderPluginInfo.DESCRIPTION,
        version = BetterChunkLoaderPluginInfo.VERSION
)
public class BetterChunkLoader {
    private static BetterChunkLoader instance;
    @Inject
    private Logger logger;
    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;
    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path configPath;
    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> configLoader;
    private Map<String, List<CChunkLoader>> activeChunkLoaders;
    public boolean enabled = false;

    @Inject
    public BetterChunkLoader() {
        if (instance != null) {
            throw new IllegalStateException("Plugin cannot be instantiated twice");
        }

        instance = this;
    }

    public static BetterChunkLoader instance() {
        return instance;
    }

    public static long getPlayerLastPlayed(UUID playerId) {
        //		Optional<Player> onlinePlayer = Sponge.getServer().getPlayer(playerId);
        //		if(onlinePlayer.isPresent()) {
        //			return 0L; //player is online
        //		} else {
        //			Optional<UserStorageService> userStorage = Sponge.getServiceManager().provide(UserStorageService.class);
        //			Optional<User> optUser = userStorage.get().get(playerId);
        //			if (optUser.isPresent()) {
        //				User user = optUser.get();
        //				if(user.getPlayer().isPresent()){
        //					//player is online
        //					Instant lastPlayed = user.getPlayer().get().lastPlayed().get();
        //					return lastPlayed.getLong(ChronoField.NANO_OF_SECOND);
        //				} else {
        //					//player is offline
        //					Instant instant = user.get(Keys.LAST_DATE_PLAYED).get();
        //					return instant.getLong(ChronoField.NANO_OF_SECOND);
        //				}
        //			} else {
        //				return getPlayerDataLastModified(playerId);
        //			}
        //		}
        //TODO: maybe make a call to nucleus and check when the user was last online (if they have nucleus enabled)
        //TODO: Reimplement the above. Ref: https://forums.spongepowered.org/t/get-last-played-instance-of-offline-player/16829/11
        return getPlayerDataLastModified(playerId);

    }

    public static long getPlayerDataLastModified(UUID playerId) {
        String name = Sponge.getServer().getDefaultWorldName();
        Path path = Sponge.getGame().getGameDirectory().resolve(name);
        File playerData = new File(path.toString(), "playerdata" + File.separator + playerId.toString() + ".dat");
        if (playerData.exists()) {
            return playerData.lastModified();
        }
        return 0;
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

        try {
            setupPlugin();
        } catch (Exception e) {
            Messenger.logException(e);
        }

        initializeCommands();
    }

    public void setupPlugin() throws Exception {
        // Load config
        logger.debug("Loading configuration");
        // Have to move config file since Configurate did not properly implemented private root conventions
        Config.moveOldConfig(configDir, configPath);
        Config config = Config.loadFrom(configDir, configLoader.load());
        config.saveToFile(configLoader);

        this.activeChunkLoaders = new HashMap<>();
        // Register DataStores
        DataStoreManager.registerDataStore("MySQL", MySQLDataStore.class);
        DataStoreManager.registerDataStore("H2", H2DataStore.class);

        // instantiate DataStore, if needed
        if (DataStoreManager.getDataStore() == null) {
            String dataStore = config.getDataStore();
            DataStoreManager.setDataStoreInstance(dataStore);
        }

        // load datastore
        logger.info("Loading {} DataStore...", DataStoreManager.getDataStore().getName());
        DataStoreManager.getDataStore().load();

        logger.info("Loaded {} chunk loaders data.", DataStoreManager.getDataStore().getChunkLoaders().size());
        logger.info("Loaded {} players data.", DataStoreManager.getDataStore().getPlayersData().size());

        // load always on chunk loaders
        int count = 0;
        for (CChunkLoader cl : DataStoreManager.getDataStore().getChunkLoaders()) {
            if (cl.getServerName().equalsIgnoreCase(config.getServerName()) && cl.isLoadable()) {
                this.loadChunks(cl);
                count++;
            }
        }

        logger.info("Loaded {} always-on chunk loaders.", count);

        logger.debug("Loading Listeners...");
        initializeListeners();

        logger.debug("Load complete.");
        enabled = true;
    }

    @Listener
    public void onDisable(GameStoppingServerEvent event) {
        for (CChunkLoader cl : this.getActiveChunkloaders()) {
            this.unloadChunks(cl);
        }

        instance = null;
    }

    private void initializeListeners() {
        Sponge.getEventManager().registerListeners(this, new Events());
    }

    private void initializeCommands() {


        CommandSpec cmdBalance = CommandSpec.builder()
                .arguments(GenericArguments.optional(GenericArguments.requiringPermission(
                        GenericArguments.user(Text.of("user")), Permission.COMMAND_BALANCE_OTHERS)))
                .permission(Permission.COMMAND_BALANCE)
                .executor(new Balance())
                .description(Text.of("Get the balance of your different types of chunk loaders."))
                .build();

        CommandSpec cmdInfo = CommandSpec.builder()
                .arguments(GenericArguments.none())
                .permission(Permission.COMMAND_INFO)
                .description(Text.of("Get general information about the usage on the server."))
                .executor(new Info())
                .build();


        CommandSpec cmdList = CommandSpec.builder()
                .arguments(
                        // TODO: figure out a good way to do this.
                        //                        GenericArguments.optional(GenericArguments.requiringPermission(new AllElement(Text.of("all")),
                        // Permissions
                        //                .COMMAND_LIST_ALL)),
                        GenericArguments.optional(
                                GenericArguments.requiringPermission(GenericArguments.user(Text.of("user")), Permission.COMMAND_LIST_OTHERS))
                )
                .permission(Permission.COMMAND_LIST_SELF)
                .executor(new ListCommand())
                .description(Text.of("Get the list of your chunk loaders."))
                .build();


        CommandSpec cmdChunks = CommandSpec.builder()
                .arguments(GenericArguments.seq(new ChunksChangeOperatorElement(Text.of("change")),
                        GenericArguments.user(Text.of("user")),
                        new LoaderTypeElement(Text.of("type")),
                        GenericArguments.integer(Text.of("value"))
                ))
                .executor(new Chunks())
                .permission(Permission.COMMAND_CHUNKS)
                .description(Text.of("add, set remove a players type of chunkloaders."))
                .build();

        CommandSpec delete = CommandSpec.builder()
                .arguments(
                        GenericArguments.optional(
                                GenericArguments.requiringPermission(GenericArguments.user(Text.of("user")), Permission.COMMAND_DELETE_OTHERS))
                )
                .executor(new Delete())
                .permission(Permission.COMMAND_DELETE_OWN)
                .description(Text.of("Delete the specified players chunkloaders"))
                .build();

        CommandSpec cmdPurge = CommandSpec.builder()
                .executor(new Purge())
                .permission(Permission.COMMAND_PURGE)
                .description(Text.of("remove all chunks there is in not existing dimensions."))
                .build();

        CommandSpec cmdReload = CommandSpec.builder()
                .permission(Permission.COMMAND_RELOAD)
                .executor(new Reload())
                .description(Text.of("Reloads the plugin configuration."))
                .build();

        CommandSpec bclCmdSpec = CommandSpec.builder()
                .child(cmdBalance, "balance", "bal")
                .child(cmdInfo, "info")
                .child(cmdList, "list", "ls")
                .child(cmdChunks, "chunks")
                .child(delete, "delete", "del")
                .child(cmdPurge, "purge")
                .child(cmdReload, "reload")
                .executor(new BCL())
                .build();

        Sponge.getCommandManager().register(this, bclCmdSpec, "betterchunkloader", "bcl");
    }


    public Logger getLogger() {
        return logger;
    }

    public void loadChunks(CChunkLoader chunkloader) {
        if (chunkloader.getServerName().equalsIgnoreCase(Config.getInstance().getServerName())) {
            BCLForgeLib.instance().addChunkLoader(chunkloader);
            List<CChunkLoader> clList = activeChunkLoaders.computeIfAbsent(chunkloader.getWorldName(), k -> new ArrayList<>());
            clList.add(chunkloader);
        }
    }


    public void unloadChunks(CChunkLoader chunkloader) {
        if (chunkloader.getServerName().equalsIgnoreCase(Config.getInstance().getServerName())) {
            BCLForgeLib.instance().removeChunkLoader(chunkloader);
            List<CChunkLoader> clList = activeChunkLoaders.get(chunkloader.getWorldName());
            if (clList == null) {
                return;
            }
            clList.remove(chunkloader);
        }
    }

    public List<CChunkLoader> getActiveChunkloaders() {
        List<CChunkLoader> chunkLoaders = new ArrayList<>();
        for (List<CChunkLoader> clList : activeChunkLoaders.values()) {
            chunkLoaders.addAll(clList);
        }
        return chunkLoaders;
    }
}
