package net.kaikk.mc.bcl;

import com.google.inject.Inject;
import net.kaikk.mc.bcl.commands.CmdBCL;
import net.kaikk.mc.bcl.commands.CmdBalance;
import net.kaikk.mc.bcl.commands.CmdChunks;
import net.kaikk.mc.bcl.commands.CmdDelete;
import net.kaikk.mc.bcl.commands.CmdInfo;
import net.kaikk.mc.bcl.commands.CmdList;
import net.kaikk.mc.bcl.commands.CmdPurge;
import net.kaikk.mc.bcl.commands.elements.ChunksChangeOperatorElement;
import net.kaikk.mc.bcl.commands.elements.LoaderTypeElement;
import net.kaikk.mc.bcl.config.Config;
import net.kaikk.mc.bcl.datastore.DataStoreManager;
import net.kaikk.mc.bcl.datastore.MySqlDataStore;
import net.kaikk.mc.bcl.forgelib.BCLForgeLib;
import net.kaikk.mc.bcl.utils.BCLPermission;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Plugin(id = "betterchunkloader",
        name = "@name@",
        description = "@description@",
        version = "@version@",
        authors = "@authors@"
)
public class BetterChunkLoader {

    private static final Text prefix = Text.builder("[BetterChunkLoader] ").color(TextColors.GOLD).build();
    private static BetterChunkLoader instance;
    @Inject
    private Logger logger;
    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;

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

    public static Text getPrefix() {
        return prefix;
    }

    public void onLoad() {
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

        instance = this;

        // Config File
        if (!Files.exists(configDir)) {
            try {
                Files.createDirectories(configDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Config.getConfig().setup();

        try {
            // load config
            logger.info("Loading config...");
            onLoad();
            // instantiate data store, if needed
            if (DataStoreManager.getDataStore() == null) {
                DataStoreManager.setDataStoreInstance(Config.getConfig().get().getNode("DataStore").getString());
            }

            // load datastore
            logger.info("Loading " + DataStoreManager.getDataStore().getName() + " Data Store...");
            DataStoreManager.getDataStore().load();

            logger.info("Loaded " + DataStoreManager.getDataStore().getChunkLoaders().size() + " chunk loaders data.");
            logger.info("Loaded " + DataStoreManager.getDataStore().getPlayersData().size() + " players data.");

            // load always on chunk loaders
            int count = 0;
            for (CChunkLoader cl : DataStoreManager.getDataStore().getChunkLoaders()) {
                if (cl.getServerName().equalsIgnoreCase(Config.getConfig().get().getNode("ServerName").getString())) {
                    if (cl.isLoadable()) {
                        BCLForgeLib.instance().addChunkLoader(cl);
                        count++;
                    }
                }
            }

            logger.info("Loaded " + count + " always-on chunk loaders.");

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

    @Listener
    public void onDisable(GameStoppedServerEvent event) {
        for (CChunkLoader cl : DataStoreManager.getDataStore().getChunkLoaders()) {
            if (cl.getServerName().equalsIgnoreCase(Config.getConfig().get().getNode("ServerName").getString())) {
                BCLForgeLib.instance().removeChunkLoader(cl);
            }
        }
        instance = null;
    }

    private void initializeListeners() {
        Sponge.getEventManager().registerListeners(this, new Events());
    }

    private void initializeCommands() {


        CommandSpec cmdInfo = CommandSpec.builder()
                .arguments(GenericArguments.none())
                .permission(BCLPermission.COMMAND_INFO)
                .description(Text.of("Get general information about the usage on the server."))
                .executor(new CmdInfo())
                .build();


        //private CommandSpec cmdList = CommandSpec.builder().arguments(GenericArguments.none()).build();


        CommandSpec cmdBalance = CommandSpec.builder()
                .arguments(GenericArguments.optional(GenericArguments.requiringPermission(
                        GenericArguments.user(Text.of("user")), BCLPermission.COMMAND_BALANCE_OTHERS)))
                .permission(BCLPermission.COMMAND_BALANCE)
                .executor(new CmdBalance())
                .description(Text.of("Get the balance of your different types of chunk loaders."))
                .build();

        CommandSpec cmdList = CommandSpec.builder()
                .arguments(
                        // TODO: figure out a good way to do this.
                        //                        GenericArguments.optional(GenericArguments.requiringPermission(new AllElement(Text.of("all")),
                        // BCLPermission
                        //                .COMMAND_LIST_ALL)),
                        GenericArguments.optional(
                                GenericArguments.requiringPermission(GenericArguments.user(Text.of("user")), BCLPermission.COMMAND_LIST_OTHERS))
                )
                .permission(BCLPermission.COMMAND_LIST_SELF)
                .executor(new CmdList())
                .description(Text.of("Get the list of your chunk loaders."))
                .build();


        CommandSpec cmdChunks = CommandSpec.builder()
                .arguments(GenericArguments.seq(new ChunksChangeOperatorElement(Text.of("change")),
                        GenericArguments.user(Text.of("user")),
                        new LoaderTypeElement(Text.of("type")),
                        GenericArguments.integer(Text.of("value"))
                ))
                .executor(new CmdChunks())
                .permission(BCLPermission.COMMAND_CHUNKS)
                .description(Text.of("add, set remove a players type of chunkloaders."))
                .build();

        CommandSpec cmdDelete = CommandSpec.builder()
                .arguments(GenericArguments.onlyOne(GenericArguments.user(Text.of("user"))))
                .executor(new CmdDelete())
                .permission(BCLPermission.COMMAND_DELETE)
                .description(Text.of("Delete the specified players chunkloaders"))
                .build();

        CommandSpec cmdPurge = CommandSpec.builder()
                .executor(new CmdPurge())
                .permission(BCLPermission.COMMAND_PURGE)
                .description(Text.of("remove all chunks there is in not existing dimensions."))
                .build();

        CommandSpec bclCmdSpec = CommandSpec.builder()
                .child(cmdBalance, new String[]{"balance", "bal"})
                .child(cmdInfo, new String[]{"info"})
                .child(cmdList, new String[]{"list", "ls"})
                .child(cmdChunks, new String[]{"chunks"})
                .child(cmdDelete, new String[]{"delete", "del"})
                .child(cmdPurge, new String[]{"purge"})
                .executor(new CmdBCL())
                .build();

        Sponge.getCommandManager().register(this, bclCmdSpec, "betterchunkloader", "bcl");


    }

    public Logger getLogger() {
        return logger;
    }

    public Path getConfigDir() {
        return configDir;
    }
}
