package guru.franz.mc.bcl.config;

import guru.franz.mc.bcl.config.stub.MySQL;
import org.spongepowered.api.item.ItemType;

public class Config {

    private static Config instance;
    private final ItemType itemType;
    private final String serverName, dataStore;
    private final int maxHoursOffline, defaultChunksAmountWorld, defaultChunksAmountPersonal, maxChunksAmountWorld, maxChunksAmountPersonal;
    private final MySQL mySQL;

    public Config(String serverName, int maxHoursOffline, String dataStore, int defaultChunksAmountWorld,
            int defaultChunksAmountPersonal, int maxChunksAmountWorld, int maxChunksAmountPersonal, ItemType itemType, MySQL mySQL) {
        this.itemType = itemType;
        this.serverName = serverName;
        this.maxHoursOffline = maxHoursOffline;
        this.dataStore = dataStore;
        this.defaultChunksAmountWorld = defaultChunksAmountWorld;
        this.defaultChunksAmountPersonal = defaultChunksAmountPersonal;
        this.maxChunksAmountWorld = maxChunksAmountWorld;
        this.maxChunksAmountPersonal = maxChunksAmountPersonal;
        this.mySQL = mySQL;
        instance = this;
    }

    public static Config getInstance() {
        return instance;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public String getItemName() {
        return itemType.getTranslation().get();
    }

    public String getServerName() {
        return serverName;
    }

    public int getMaxHoursOffline() {
        return maxHoursOffline;
    }

    public String getDataStore() {
        return dataStore;
    }

    public int getDefaultChunksAmountWorld() {
        return defaultChunksAmountWorld;
    }

    public int getDefaultChunksAmountPersonal() {
        return defaultChunksAmountPersonal;
    }

    public int getMaxChunksAmountWorld() {
        return maxChunksAmountWorld;
    }

    public int getMaxChunksAmountPersonal() {
        return maxChunksAmountPersonal;
    }

    public MySQL getMySQL() {
        return mySQL;
    }
}
