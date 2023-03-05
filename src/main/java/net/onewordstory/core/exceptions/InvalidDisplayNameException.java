package net.onewordstory.core.exceptions;

/**
 * Exception for a case when the user's name is invalid
 */
public class InvalidDisplayNameException extends EntityException {

    /**
     * Constructor for InvalidDisplayNameException
     * @param str message which will be displayed when exception is called
     */
    public InvalidDisplayNameException(String str) {
        super(str);
    }

}
