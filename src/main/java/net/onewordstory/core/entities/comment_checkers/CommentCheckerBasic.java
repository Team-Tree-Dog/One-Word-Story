package net.onewordstory.core.entities.comment_checkers;

/**
 * A basic implementation of a comment checker
 */
public class CommentCheckerBasic implements CommentChecker {

    public static final int MAX_LENGTH = 300;

    /**
     * Validates comment using basic criteria, checking if:
     * The comment contains valid characters (defined in code below)
     * The comment length is between 1 and 300, inclusive
     * The comment is not entirely spaces
     * @param comment the comment to be validated
     * @return if the comment is valid
     */
    @Override
    public boolean checkValid(String comment) {
        int commentLength = comment.length();
        return comment.matches("^[a-zA-Z0-9.,?/'\";:\\[\\]}=+{@!#$&()\\- ]*$") &&
                !comment.matches("^[ ]*$") &&
                commentLength > 0 &&
                commentLength <= MAX_LENGTH;
    }
}
