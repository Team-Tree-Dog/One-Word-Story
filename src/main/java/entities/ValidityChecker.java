package entities;

/**
 * Validity Checker of words
 */
public interface ValidityChecker {

    /**
     * Checks whether the word is valid
     * @param word the word we need to check
     * @return true if the word is valid and false if it is not
     */
    boolean isValid(String word);
}
