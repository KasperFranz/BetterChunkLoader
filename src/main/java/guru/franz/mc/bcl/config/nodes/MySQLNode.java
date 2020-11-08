package guru.franz.mc.bcl.config.nodes;

import guru.franz.mc.bcl.datastore.exceptions.MySQLConnectionException;
import guru.franz.mc.bcl.BetterChunkLoader;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.net.URLEncoder;

@ConfigSerializable
public class MySQLNode {

    @Setting("Hostname")
    private String hostname = "host";
    @Setting("Username")
    private String username = "user";
    @Setting("Password")
    private String password = "pass";
    @Setting("Database")
    private String database = "db";

    /**
     * Get a JDBC connection string.
     * @return the JDBC connection string
     * @throws MySQLConnectionException if there is any data issues
     */
    public String getConnectionString() throws MySQLConnectionException {
        if (username == null || username.isEmpty()) {
            throw new MySQLConnectionException("No user provided");
        }

        if (database == null || database.isEmpty()) {
            throw new MySQLConnectionException("No database selected");
        }

        if (hostname == null || hostname.isEmpty()) {
            throw new MySQLConnectionException("No hostname provided");
        }

        String safePassword;
        try {
            safePassword = URLEncoder.encode(password, "UTF-8");
        } catch (Exception e) {
            safePassword = password;
            BetterChunkLoader.instance().getLogger().error("We had a problem while encoding the database password, falling back to unsafe, this may end badly");
        }

        return String.format("jdbc:mysql://%s:%s@%s/%s", username, safePassword, hostname, database);
    }
}
