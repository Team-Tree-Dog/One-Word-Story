package entities.validity_checkers;

import org.jetbrains.annotations.Nullable;

import java.util.Map;

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
     * @param wordExpression the word we need to check along with punctuation
     * @return null if the expression is invalid. Otherwise, an array containing first
     * the word and then the punctuation. If the punctuation doesn't exist, return an array
     * with just the word
     */
    @Nullable
    public String[] isValid(String wordExpression) {
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
            if (!punc.equals("")) {
                return new String[]{word, punc};
            } return new String[]{word};
        }
        return null;
    }
}
