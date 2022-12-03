package entities.validity_checkers;

/**
 * Interface for punctuation validity checker.
 */
public interface PunctuationValidityChecker {

    /**
     * Verifies punctuation is valid (dependent on implementing class)
     * Makes changes to the punctuation if error is forgivable
     * @param punctuation the punctuation to validate (trimmed)
     * @return valid punctuation, or if not valid, null
     */
    String isPunctuationValid(String punctuation);
}
