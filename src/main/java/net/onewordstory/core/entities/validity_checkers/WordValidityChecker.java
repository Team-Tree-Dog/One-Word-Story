package net.onewordstory.core.entities.validity_checkers;

/**
 * Interface for word validity checker.
 */
public interface WordValidityChecker {

    /**
     * Verifies word is valid (dependent on implementing class)
     * Makes changes to the word if forgivable
     * @param word the word to validate (trimmed)
     * @return valid word, or if not valid, null
     */
    String isWordValid(String word);
}
