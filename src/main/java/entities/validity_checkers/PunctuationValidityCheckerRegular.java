package entities.validity_checkers;

/**
 * Punctuation validity checker for the regular game mode
 */
public class PunctuationValidityCheckerRegular implements PunctuationValidityChecker {

    private static final int PUNC_LENGTH = 3;

    /**
     * Verifies punctuation is valid by basic criteria. Accepts punctuation combinations
     * which consist of a single character from {, ; : -}. Accepts punctuation which
     * consists of any number of periods, or !? combinations. Any valid punctuation longer than 3
     * characters gets trimmed to 3 characters by cutting off the rest
     * @param punctuation the punctuation to validate (trimmed)
     * @return valid punctuation, or if not valid, null
     */
    @Override
    public String isPunctuationValid(String punctuation) {
        int puncLength = punctuation.length();
        if (puncLength == 0) {
            return punctuation;
        }
        if (puncLength > PUNC_LENGTH) {
            punctuation = punctuation.substring(0, PUNC_LENGTH);
        }
        if (punctuation.substring(0, 1).matches("[,;:\-]?")) {
            if (puncLength == 1) {
                return punctuation;
            }
            return null;
        } 
        if (punctuation.matches("\\.*") || punctuation.matches("[!?]*")) {
            return punctuation;
        }
        return null;
    }
}
