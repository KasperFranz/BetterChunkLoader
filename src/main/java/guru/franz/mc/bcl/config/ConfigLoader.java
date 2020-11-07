package guru.franz.mc.bcl.config;

import com.google.common.reflect.TypeToken;
import guru.franz.mc.bcl.config.stub.MySQL;
import guru.franz.mc.bcl.exception.ConfigLoadException;
import guru.franz.mc.bcl.BetterChunkLoader;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigLoader {

    private static ConfigLoader instance;

    private final Path configDir;
    private final Path configFile;
    private final ConfigurationLoader<CommentedConfigurationNode> configLoader;
    private CommentedConfigurationNode configNode;

    public ConfigLoader(Path configDir) {
        this.configDir = configDir;
        configFile = Paths.get(configDir + "/config.conf");
        configLoader = HoconConfigurationLoader.builder().setPath(configFile).build();
        instance = this;
    }

    public static ConfigLoader getInstance(){
        return instance;
    }

    public void setup() throws ConfigLoadException {
        // Config File
        if (!Files.exists(configDir)) {
            try {
                Files.createDirectories(configDir);
            } catch (IOException e) {
                e.printStackTrace();
                //TODO better message.
            }
        }

        if (!Files.exists(this.configFile)) {
            try {
                Files.createFile(this.configFile);
                this.configNode = this.configLoader.load();
                populate();
                save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        load();
    }

    private void load() throws ConfigLoadException {
        try {
            this.configNode = (this.configLoader.load());
            ItemType itemType;
            String serverName, dataStore;
            int maxHoursOffline, defaultChunksAmountWorld, defaultChunksAmountPersonal, maxChunksAmountWorld, maxChunksAmountPersonal;
            try {
                itemType = get().getNode("item", "type").getValue(TypeToken.of(ItemType.class));
                if (itemType == null) {
                    itemType = ItemTypes.BLAZE_ROD;
                }
            } catch (ObjectMappingException e) {
                e.printStackTrace();
                itemType = ItemTypes.BLAZE_ROD;
            }
            serverName = get().getNode("ServerName").getString("aServer");
            maxHoursOffline = get().getNode("MaxHoursOffline").getInt(72);
            dataStore = get().getNode("DataStore").getString("MySQL");
            defaultChunksAmountWorld = get().getNode("DefaultChunksAmount", "World").getInt(0);
            defaultChunksAmountPersonal = get().getNode("DefaultChunksAmount", "Personal").getInt(0);
            maxChunksAmountWorld = get().getNode("DefaultChunksAmount", "World").getInt(250);
            maxChunksAmountPersonal = get().getNode("DefaultChunksAmount", "Personal").getInt(250);
            BetterChunkLoader.instance().getLogger().info("Loading information for " + serverName);

            MySQL mySQL = new MySQL(
            get().getNode("MySQL", "Hostname").getString("host"),
            get().getNode("MySQL", "Username").getString("user"),
            get().getNode("MySQL", "Password").getString("pass"),
            get().getNode("MySQL", "Database").getString("db")
            );

            new Config(serverName, maxHoursOffline, dataStore, defaultChunksAmountWorld, defaultChunksAmountPersonal,
                    maxChunksAmountWorld, maxChunksAmountPersonal, itemType,
                    mySQL);

        } catch (IOException e) {
            throw new ConfigLoadException(e.getMessage());
        }
    }

    private void save() {
        try {
            this.configLoader.save(this.configNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void populate() {
        get().getNode("ServerName").setValue("aServer").setComment("Unique name of server");
        get().getNode("MaxHoursOffline").setValue(72).setComment("Time in hours before player's chunkloaders become inactive.");
        get().getNode("DataStore").setValue("H2").setComment("You can use Either MySQL or H2!");
        get().getNode("DefaultChunksAmount", "World").setValue(0);
        get().getNode("DefaultChunksAmount", "Personal").setValue(0);
        get().getNode("MaxChunksAmount", "World").setValue(250);
        get().getNode("MaxChunksAmount", "Personal").setValue(250);
        get().getNode("item", "type").setValue("minecraft:blaze_rod");

        get().getNode("MySQL", "Hostname").setValue("host");
        get().getNode("MySQL", "Username").setValue("user");
        get().getNode("MySQL", "Password").setValue("pass");
        get().getNode("MySQL", "Database").setValue("db");
    }

    public Path getDirectory(){
        return configDir;
    }

    private CommentedConfigurationNode get() {
        return this.configNode;
    }
}
