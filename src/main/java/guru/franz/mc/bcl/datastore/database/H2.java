package guru.franz.mc.bcl.datastore.database;

import guru.franz.mc.bcl.config.ConfigLoader;
import guru.franz.mc.bcl.datastore.exceptions.MySQLConnectionException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

public class H2 extends MySQL {

    /**
     * Get a dataSource to execute Queries against
     *
     * @return the DataStore
     * @throws SQLException             if there is any errors with the MySQL
     * @throws MySQLConnectionException If we can't connect to the database.
     */
    protected DataSource getDataSource() throws SQLException, MySQLConnectionException {

        Path datastoreDir = Paths.get(ConfigLoader.getInstance().getDirectory() + "/datastore");
        if (!Files.exists(datastoreDir)) {
            try {
                Files.createDirectories(datastoreDir);
            } catch (IOException e) {
                e.printStackTrace();
                //TODO better message.
            }
        }

        String ConnectionString = "jdbc:h2:"+ datastoreDir + "/h2;mode=MySQL";
        return Sponge.getServiceManager().provide(SqlService.class).orElseThrow(SQLException::new)
                .getDataSource(ConnectionString);

    }
}
