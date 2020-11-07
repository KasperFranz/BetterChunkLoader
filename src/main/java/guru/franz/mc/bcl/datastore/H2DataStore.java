package guru.franz.mc.bcl.datastore;

import guru.franz.mc.bcl.datastore.database.H2;
import guru.franz.mc.bcl.datastore.exceptions.MySQLConnectionException;

public class H2DataStore extends DatabaseDataStore {

    @Override
    public String getName() {
        return "H2";
    }

    @Override
    public void load() throws MySQLConnectionException {

        this.database = new H2();
        super.load();
    }
}
