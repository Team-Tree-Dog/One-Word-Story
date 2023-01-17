package net.onewordstory.core.entities.suggested_title_checkers;

/**
 * Abstract interface to check whether a suggested title is a valid title for a story.
 */
public interface SuggestedTitleChecker {

    /**
     * This abstract method checks whether the given title is a valid story title. The specific criteria for
     * determining whether a title is valid depends on the implementation of the SuggestedTitleChecker interface.
     * PRECONDITION: title must have its leading and trailing whitespaces trimmed, and all consecutive interior
     * whitespaces must be reduced to one whitespace.
     * @param title the title which we want to check for validity
     * @return      true if and only if title is a valid story title.
     */
    boolean checkValid(String title);
}
