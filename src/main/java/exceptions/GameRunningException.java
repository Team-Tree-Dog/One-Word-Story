package exceptions;

/**
 * Exception for a cases when you try to set the game as null or set the game another instance of already running game
 */
public class GameRunningException extends EntityException {

    /**
     * Constructor for GameRunningException
     * @param str message which will be displayed when exception is called
     */
    public GameRunningException(String str) {
        super(str);
    }

}
