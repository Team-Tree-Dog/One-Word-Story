package entities.validity_checkers;

/**
 * Punctuation validity checker for the regular game mode
 */
public class PunctuationValidityCheckerRegular implements PunctuationValidityChecker {

    private static final int PUNC_LENGTH = 3;

    /**
     * Verifies punctuation is valid based on game mode regular criteria
     * Makes changes to the punctuation if error is forgivable
     * @param punctuation the punctuation to validate (trimmed)
     * @return valid punctuation, or if not valid, null
     */
    @Override
    public String isPunctuationValid(String punctuation) {
        int puncLength = punctuation.length();
        if (puncLength > PUNC_LENGTH) {
            punctuation = punctuation.substring(0, PUNC_LENGTH);
        }
        if (punctuation.substring(0, 1).matches("[,;:-]?")) {
            if (puncLength <= 1) {
                return punctuation;
            }
            return null;
        } else if (punctuation.matches("\\.*") || punctuation.matches("[!?]*")) {
            return punctuation;
        }
        return null;
    }
}
