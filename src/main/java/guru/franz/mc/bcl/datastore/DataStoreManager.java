package guru.franz.mc.bcl.datastore;

import java.util.HashMap;
import java.util.Map;

/**
 * DataStore manager<br>
 * See IDataStore interface if you want to implement your custom DataStore
 */
final public class DataStoreManager {

    private static final Map<String, Class<? extends IDataStore>> dataStores = new HashMap<>();
    private static IDataStore dataStore;

    /**
     * Register a new DataStore. This should be run at onLoad()<br>
     *
     * @param dataStoreId    ID that identifies this DataStore <br>
     * @param dataStoreClass a class that implements IDataStore
     */
    public static void registerDataStore(String dataStoreId, Class<? extends IDataStore> dataStoreClass) {
        dataStores.put(dataStoreId, dataStoreClass);
    }

    /**
     * Sets and instantiate the DataStore
     */
    public static void setDataStoreInstance(String dataStoreId) {
        try {
            dataStore = dataStores.get(dataStoreId).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't instantiate DataStore " + dataStoreId);
        }
    }

    /**
     * Gets current DataStore. Returns null if there isn't an instantiated DataStore
     */
    public static IDataStore getDataStore() {
        return dataStore;
    }
}
