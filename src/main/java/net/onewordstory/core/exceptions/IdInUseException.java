package net.onewordstory.core.exceptions;

/**
 * Exception for a case when ID under which the user wants to register is already taken by another user
 */
public class IdInUseException extends EntityException {

    /**
     * Constructor for IdInUseException
     * @param str message which will be displayed when exception is called
     */
    public IdInUseException(String str) {
        super(str);
    }

}
