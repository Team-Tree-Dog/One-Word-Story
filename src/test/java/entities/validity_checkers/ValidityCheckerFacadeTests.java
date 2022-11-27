package entities.validity_checkers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ValidityCheckerFacadeTests {

    /**
     * We create a custom implementation of ValidityCheckerFacade to have isValid return
     * the punctuation and word with a space in between, so we can isolate them while testing
     */
    public static class TestValidityCheckerFacade extends ValidityCheckerFacade {

        /**
         * Constructor for ValidityCheckerFacade
         *
         * @param pv A punctuation validity checker
         * @param wv A word validity checker
         */
        public TestValidityCheckerFacade(PunctuationValidityChecker pv, WordValidityChecker wv) {
            super(pv, wv);
        }

        @Override
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
                word = wordValidity.isWordValid(wordExpression.trim());
            }

            if (punc != null && word != null) {
                return punc + " " + word;
            }
            return null;
        }
    }

    /**
     * This punctuation validity checker does nothing to the word when checked
     */
    public static class TestPunctuationValidityChecker implements PunctuationValidityChecker {
        @Override
        public String isPunctuationValid(String punctuation) {
            return punctuation;
        }
    }

    /**
     * This word validity checker does nothing to the word when checked
     */
    public static class TestWordValidityChecker implements WordValidityChecker {
        @Override
        public String isWordValid(String word) {
            return word;
        }
    }

    /**
     * We use an implementation of TestValidityCheckerFacade where the punctuation and word
     * validity checkers do nothing, meaning the only changes are made by TestValidityCheckerFacade
     * logic
     */
    TestPunctuationValidityChecker puncValidityChecker =
            new TestPunctuationValidityChecker();
    TestWordValidityChecker wordValidityChecker =
            new TestWordValidityChecker();
    public final TestValidityCheckerFacade v = new TestValidityCheckerFacade(
            puncValidityChecker, wordValidityChecker
    );

    @Before
    public void setUp() {}

    @After
    public void tearDown() {}

    /**
     * Tests scenario where the word expression has no punctuation and no word
     */
    @Test(timeout = 1000)
    public void testNoPuncNoWord() {
        String inputWord = "";
        String validifiedWord = v.isValid(inputWord);
        String[] puncAndWord = validifiedWord.split(" ", 2);
        String punc = puncAndWord[0];
        String word = puncAndWord[1];
        assertEquals("", punc);
        assertEquals("", word);
    }

    /**
     * Tests scenario where the word expression has no punctuation
     */
    @Test(timeout = 1000)
    public void testNoPuncWord() {
        String inputWord = "word";
        String validifiedWord = v.isValid(inputWord);
        String[] puncAndWord = validifiedWord.split(" ", 2);
        String punc = puncAndWord[0];
        String word = puncAndWord[1];
        assertEquals("", punc);
        assertEquals("word", word);
    }

    /**
     * Tests scenario where the word expression has no punctuation and word has typo spaces
     */
    @Test(timeout = 1000)
    public void testNoPuncWordWithSpaces() {
        String inputWord = "  word   ";
        String validifiedWord = v.isValid(inputWord);
        String[] puncAndWord = validifiedWord.split(" ", 2);
        String punc = puncAndWord[0];
        String word = puncAndWord[1];
        assertEquals("", punc);
        assertEquals("word", word);
    }

    /**
     * Tests scenario where the word expression has punctuation and word
     */
    @Test(timeout = 1000)
    public void testPuncWord() {
        String inputWord = "! word";
        String validifiedWord = v.isValid(inputWord);
        String[] puncAndWord = validifiedWord.split(" ", 2);
        String punc = puncAndWord[0];
        String word = puncAndWord[1];
        assertEquals("!", punc);
        assertEquals("word", word);
    }

    /**
     * Tests scenario where the word expression has punctuation and word, with typo spaces
     */
    @Test(timeout = 1000)
    public void testPuncWordSpaces() {
        String inputWord = "    !   word  ";
        String validifiedWord = v.isValid(inputWord);
        String[] puncAndWord = validifiedWord.split(" ", 2);
        String punc = puncAndWord[0];
        String word = puncAndWord[1];
        assertEquals("!", punc);
        assertEquals("word", word);
    }
}
