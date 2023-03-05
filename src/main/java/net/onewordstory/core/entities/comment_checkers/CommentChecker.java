package net.onewordstory.core.entities.comment_checkers;

/**
 * Validates comments made by guests
 */
public interface CommentChecker {

    /**
     * Checks if the comment is valid
     * @param comment the comment to be validated
     * @return if the comment is valid
     */
    boolean checkValid(String comment);
}
