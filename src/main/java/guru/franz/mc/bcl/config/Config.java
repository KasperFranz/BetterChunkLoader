package guru.franz.mc.bcl.config;

import guru.franz.mc.bcl.BetterChunkLoader;
import guru.franz.mc.bcl.config.node.ChunksAmountNode;
import guru.franz.mc.bcl.config.node.ItemsNode;
import guru.franz.mc.bcl.config.node.MaxChunksAmountNode;
import guru.franz.mc.bcl.config.node.MySQLNode;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@ConfigSerializable
public class Config {

    private static Config instance;
    private static final ObjectMapper<Config> MAPPER;

    private Path configDir;
    private ConfigurationNode node;
    @Setting(value = "DataStore", comment = "Available data storage types include: MySQL, H2.")
    private String dataStore = "H2";
    @Setting("Items")
    private ItemsNode itemsNode = new ItemsNode();
    @Setting("DefaultChunksAmount")
    private ChunksAmountNode defaultChunksNode = new ChunksAmountNode();
    @Setting("MaxChunksAmount")
    private MaxChunksAmountNode maxChunksNode = new MaxChunksAmountNode();
    @Setting(value = "MaxHoursOffline", comment = "Time in hours before a player's chunkloaders become inactive.")
    private int maxHoursOffline = 72;
    @Setting("MySQL")
    private final MySQLNode mySQL = new MySQLNode();
    @Setting(value = "ServerName", comment = "Unique name of the server.")
    private String serverName = "aServer";

    static {
        try {
            MAPPER = ObjectMapper.forClass(Config.class);
        } catch (ObjectMappingException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public Config(Path configDir, ConfigurationNode node) {
        this.configDir = configDir;
        this.node = node;
    }

    public static Config getInstance() {
        return instance;
    }

    /**
     * @return The {@link ConfigurationNode} that was used to create this {@link Config}.
     */
    public ConfigurationNode getNode() {
        return node;
    }

    public Path getDirectory() {
        return configDir;
    }

    public void setDirectory(Path configDir) {
        this.configDir = configDir;
    }

    /**
     * Loads a {@link Config} instance from the provided {@link ConfigurationNode}.
     * The newly created instance is then used when returning {@link #getInstance()}.
     *
     * @param configDir The {@link Path} where the config resides, used to return {@link #getDirectory()}.
     * @param node The {@link ConfigurationNode} to load from.
     * @return The newly created instance.
     * @throws ObjectMappingException If an error occurs when populating the config.
     */
    public static Config loadFrom(Path configDir, ConfigurationNode node) throws ObjectMappingException {
        // Apply any version transformations before generating the config object
        Transformations.versionedTransformation().apply(node);
        instance = new Config(configDir, node);
        // Populate fields
        MAPPER.bind(instance).populate(node);
        return instance;
    }

    public static void moveOldConfig(Path configDir, Path newConfigPath) {
        Path oldConfigPath = configDir.resolve("config.conf");
        if (Files.exists(oldConfigPath) && !Files.exists(newConfigPath)) {
            try {
                Files.move(oldConfigPath, newConfigPath);
            } catch (IOException e) {
                BetterChunkLoader.instance().getLogger().error("Could not move old config file.", e);
            }
        }
    }

    public void saveToFile(ConfigurationLoader<CommentedConfigurationNode> configLoader) throws ObjectMappingException, IOException {
        this.saveToNode(node);
        configLoader.save(node);
    }

    public void saveToNode(ConfigurationNode node) throws ObjectMappingException {
        MAPPER.bind(this).serialize(node);
    }

    public ItemsNode getItems() {
        return itemsNode;
    }

    public String getDataStore() {
        return dataStore;
    }

    public void setDataStore(String dataStore) {
        this.dataStore = dataStore;
    }

    public ChunksAmountNode getDefaultChunksAmount() {
        return defaultChunksNode;
    }

    public void setDefaultChunksAmount(ChunksAmountNode defaultChunksNode) {
        this.defaultChunksNode = defaultChunksNode;
    }

    public MaxChunksAmountNode getMaxChunksAmount() {
        return maxChunksNode;
    }

    public void setMaxChunksAmount(MaxChunksAmountNode maxChunksNode) {
        this.maxChunksNode = maxChunksNode;
    }

    public int getMaxHoursOffline() {
        return maxHoursOffline;
    }

    public void setMaxHoursOffline(int maxHoursOffline) {
        this.maxHoursOffline = maxHoursOffline;
    }

    public MySQLNode getMySQL() {
        return mySQL;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}
