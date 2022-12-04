package exceptions;

public class InvalidCommentException extends EntityException {

    /**
     * Constructor for InvalidCommentException
     * @param str message which will be displayed when exception is called
     */
    public InvalidCommentException(String str) { super(str); }
}
