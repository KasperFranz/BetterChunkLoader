package net.kaikk.mc.bcl.datastore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Data store manager<br>
 * See IDataStore interface if you want to implement your custom data store */
final public class DataStoreManager {

    private static Map<String, Class<? extends IDataStore>> dataStores = new HashMap<>();
    private static IDataStore dataStore;

    /** Register a new Data Store. This should be run at onLoad()<br>
     * @param dataStoreId ID that identifies this data store <br>
     * @param dataStoreClass a class that implements IDataStore */
    public static void registerDataStore(String dataStoreId, Class<? extends IDataStore> dataStoreClass) {
        dataStores.put(dataStoreId, dataStoreClass);
    }

    /** Unregisters the data store with the provided id*/
    public static void unregisterDataStore(String dataStoreId) {
        dataStores.remove(dataStoreId);
    }

    /** List of registered data stores id */
    public static List<String> getAvailableDataStores() {
        List<String> list = new ArrayList<String>();
        list.addAll(dataStores.keySet());
        return Collections.unmodifiableList(list);
    }

    /** Sets and instantiate the data store */
    public static void setDataStoreInstance(String dataStoreId) {
        try {
            dataStore = dataStores.get(dataStoreId).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't instantiate data store " + dataStoreId);
        }
    }

    /** Gets current data store. Returns null if there isn't an instantiated data store */
    public static IDataStore getDataStore() {
        return dataStore;
    }
}
