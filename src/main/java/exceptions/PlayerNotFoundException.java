package exceptions;

/**
 * Exception for a case when player can't be found
 */
public class PlayerNotFoundException extends Exception {

    /**
     * Constructor for PlayerNotFoundException
     * @param str message which will be displayed when exception is called
     */
    public PlayerNotFoundException(String str) {
        super(str);
    }

}
