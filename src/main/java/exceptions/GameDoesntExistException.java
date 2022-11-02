package exceptions;

/**
 * Exception for a case when game doesn't exist
 */
public class GameDoesntExistException extends EntityException {

    /**
     * Constructor for GameDoesntExistException
     * @param str message which will be displayed when exception is called
     */
    public GameDoesntExistException(String str) {
        super(str);
    }
}
