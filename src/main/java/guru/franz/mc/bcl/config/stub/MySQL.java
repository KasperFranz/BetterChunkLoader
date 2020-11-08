package guru.franz.mc.bcl.config.stub;

import guru.franz.mc.bcl.BetterChunkLoader;
import guru.franz.mc.bcl.datastore.exceptions.MySQLConnectionException;

import java.net.URLEncoder;

public class MySQL {

    private final String hostname, username, password, database;

    public MySQL(String hostname, String username, String password, String database) {
        this.hostname = hostname;
        this.username = username;
        this.password = password;
        this.database = database;
    }

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
            BetterChunkLoader.instance().getLogger()
                    .error("We had a problem while encoding the database password, falling back to unsafe, this may end badly");
        }

        return "jdbc:mysql://"
                + username
                + ":"
                + safePassword
                + "@"
                + hostname
                + "/"
                + database;
    }

}
