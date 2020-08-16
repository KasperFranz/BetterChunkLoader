package guru.franz.mc.bcl.exception.mysql;

import guru.franz.mc.bcl.exception.Exception;

public class MySQLConnectionException extends Exception {

    public MySQLConnectionException(String message)
    {
        super(message);
    }
}
