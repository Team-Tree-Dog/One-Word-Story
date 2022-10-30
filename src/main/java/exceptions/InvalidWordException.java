package exceptions;

/**
 * Exception for a case when the word is invalid
 */
public class InvalidWordException extends Exception {

    /**
     * Constructor for InvalidWordException
     * @param str message which will be displayed when exception is called
     */
    public InvalidWordException(String str) {
        super(str);
    }

}
