package guru.franz.mc.bcl.exceptions.mysql;

import net.kaikk.mc.bcl.exceptions.MyException;

public class MySQLConnectionException extends MyException {

    public MySQLConnectionException(String message)
    {
        super(message);
    }
}
