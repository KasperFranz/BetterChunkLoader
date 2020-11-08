package guru.franz.mc.bcl.datastore.exceptions;

import java.sql.SQLException;

public class MySQLConnectionException extends SQLException {

    public MySQLConnectionException(String message) {
        super(message);
    }
}
