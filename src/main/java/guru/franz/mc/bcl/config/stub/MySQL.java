package guru.franz.mc.bcl.config.stub;

import guru.franz.mc.bcl.datastore.exceptions.MySQLConnectionException;

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

        return "jdbc:mysql://"
                + username
                + ":"
                + password
                + "@"
                + hostname
                + "/"
                + database;
    }
}
