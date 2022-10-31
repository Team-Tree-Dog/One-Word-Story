package exceptions;

/**
 * Exception for a case when a player wants to write a word, but it is not his turn
 */
public class OutOfTurnException extends EntityException {

    /**
     * Constructor for OutOfTurnException
     * @param str message which will be displayed when exception is called
     */
    public OutOfTurnException(String str) {super(str);}

}
