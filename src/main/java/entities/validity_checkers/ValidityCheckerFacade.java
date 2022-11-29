package entities.validity_checkers;

/**
 * Validity Checker of words
 */
public class ValidityCheckerFacade {

    WordValidityChecker wordValidity;
    PunctuationValidityChecker puncValidity;

    /**
     * Constructor for ValidityCheckerFacade
     * @param pv A punctuation validity checker
     * @param wv A word validity checker
     */
    public ValidityCheckerFacade(PunctuationValidityChecker pv, WordValidityChecker wv) {
        this.puncValidity = pv;
        this.wordValidity = wv;
    }

    /**
     * Checks whether the word is valid, making modifications if possible
     * @param wordExpression the word we need to check
     * @return the valid word if valid, null otherwise
     */
    public String isValid(String wordExpression) {
        String punc;
        String word;

        wordExpression = wordExpression.trim();
        if (wordExpression.contains(" ")) {
            String[] puncAndWord = wordExpression.split(" ", 2);
            punc = puncValidity.isPunctuationValid(puncAndWord[0].trim());
            word = wordValidity.isWordValid(puncAndWord[1].trim());
        } else {
            punc = "";
            word = wordValidity.isWordValid(wordExpression);
        }

        if (punc != null && word != null) {
            return punc + word;
        }
        return null;
    }
}
