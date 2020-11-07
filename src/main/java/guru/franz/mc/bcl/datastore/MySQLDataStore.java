package guru.franz.mc.bcl.datastore;

import guru.franz.mc.bcl.datastore.database.MySQL;
import guru.franz.mc.bcl.datastore.exceptions.MySQLConnectionException;

public class MySQLDataStore extends DatabaseDataStore {

    @Override
    public String getName() {
        return "MySQL";
    }

    @Override
    public void load() throws MySQLConnectionException {
        this.database = new MySQL();
        super.load();
    }
}
