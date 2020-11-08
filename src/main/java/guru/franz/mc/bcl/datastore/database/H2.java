package guru.franz.mc.bcl.datastore.database;

import guru.franz.mc.bcl.BetterChunkLoader;
import guru.franz.mc.bcl.config.Config;
import guru.franz.mc.bcl.datastore.exceptions.MySQLConnectionException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import javax.sql.DataSource;

public class H2 extends MySQL {

    /**
     * Get a dataSource to execute Queries against
     *
     * @return the DataStore
     * @throws SQLException             if there is any errors with the MySQL
     * @throws MySQLConnectionException If we can't connect to the database.
     */
    protected DataSource getDataSource() throws SQLException, MySQLConnectionException {
        Path datastoreDir = Paths.get(Config.getInstance().getDirectory() + "/datastore");
        if (!Files.exists(datastoreDir)) {
            try {
                Files.createDirectories(datastoreDir);
            } catch (IOException e) {
                BetterChunkLoader.instance().getLogger()
                        .error("Could not create datastore directory.", e);
            }
        }

        String jdbcConnection = String.format("jdbc:h2:%s/h2;mode=MySQL", datastoreDir);
        return Sponge.getServiceManager().provide(SqlService.class).orElseThrow(SQLException::new)
                .getDataSource(jdbcConnection);

    }
}
