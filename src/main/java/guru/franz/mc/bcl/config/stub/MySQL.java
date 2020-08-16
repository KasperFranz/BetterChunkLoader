package guru.franz.mc.bcl.config.stub;

import java.util.Properties;

public class MySQL {
    private final String hostname, username, password, database;

    public MySQL(String hostname, String username, String password, String database) {
        this.hostname = hostname;
        this.username = username;
        this.password = password;
        this.database = database;
    }

    public String getHostname() {
        return hostname;
    }

    public String getDatabase() {
        return database;
    }
    public String getUsername(){
        return username;
    }

    public Properties getConnectionProperties(){
        final Properties connectionProps = new Properties();

        connectionProps.put("user", username);
        connectionProps.put("password", password);
        return connectionProps;
    }
}
