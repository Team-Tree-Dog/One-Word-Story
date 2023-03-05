package net.onewordstory.core.exceptions;

/**
 * Entity Exception
 */
public class EntityException extends Exception {

    /**
     * Constructor for EntityException
     * @param str message which will be displayed when exception is called
     */
    public EntityException(String str) {
        super(str);
    }
}
