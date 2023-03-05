package net.onewordstory.core.entities.validity_checkers;

/**
 * Word validity checker for the regular game mode
 */
public class WordValidityCheckerRegular implements WordValidityChecker{

    private static final int WORD_LENGTH = 22;

    /**
     * Verifies word is valid based on game mode regular criteria
     * Makes changes to the word if error is forgivable
     * @param word the word to validate (trimmed)
     * @return valid word, or if not valid, null
     */
    @Override
    public String isWordValid(String word) {
        int wordLength = word.length();
        if (wordLength > WORD_LENGTH) {
            word = word.substring(0, WORD_LENGTH);
        }
        if (word.matches("[A-Za-z]*") && wordLength > 0) {
            return word;
        }
        return null;
    }
}
