package guru.franz.mc.bcl.exception;

public class NegativeValueException extends Exception {

    public NegativeValueException()
    {
    }

    public NegativeValueException(String message)
    {
        super(message);
    }
}
