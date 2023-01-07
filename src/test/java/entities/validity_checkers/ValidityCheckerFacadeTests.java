package entities.validity_checkers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.*;

public class ValidityCheckerFacadeTests {

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
     * We use an implementation of ValidityCheckerFacade where the punctuation and word
     * validity checkers do nothing, meaning the only changes are made by ValidityCheckerFacade
     * logic
     */
    TestPunctuationValidityChecker puncValidityChecker =
            new TestPunctuationValidityChecker();
    TestWordValidityChecker wordValidityChecker =
            new TestWordValidityChecker();
    public final ValidityCheckerFacade v = new ValidityCheckerFacade(
            puncValidityChecker, wordValidityChecker
    );

    @BeforeEach
    public void setUp() {}

    @AfterEach
    public void tearDown() {}

    /**
     * Tests scenario where the word expression has no punctuation and no word
     */
    @Test
    @Timeout(1000)
    public void testNoPuncNoWord() {
        String inputWord = "";
        String[] validifiedWord = v.isValid(inputWord);
        assertEquals("", validifiedWord[0]);
        assertEquals(validifiedWord.length, 1);
    }

    /**
     * Tests scenario where the word expression has blank punctuation and blank word
     */
    @Test
    @Timeout(1000)
    public void testNoPuncNoWordSpace() {
        String inputWord = " ";
        String[] validifiedWord = v.isValid(inputWord);
        assertEquals("", validifiedWord[0]);
        assertEquals(validifiedWord.length, 1);
    }

    /**
     * Tests scenario where the word expression has no punctuation
     */
    @Test
    @Timeout(1000)
    public void testNoPuncWord() {
        String inputWord = "word";
        String[] validifiedWord = v.isValid(inputWord);
        assertEquals("word", validifiedWord[0]);
        assertEquals(validifiedWord.length, 1);
    }

    /**
     * Tests scenario where the word expression has no punctuation and word has typo spaces
     */
    @Test
    @Timeout(1000)
    public void testNoPuncWordWithSpaces() {
        String inputWord = "  word   ";
        String[] validifiedWord = v.isValid(inputWord);
        assertEquals("word", validifiedWord[0]);
        assertEquals(validifiedWord.length, 1);
    }

    /**
     * Tests scenario where the word expression has punctuation and word
     */
    @Test
    @Timeout(1000)
    public void testPuncWord() {
        String inputWord = "! word";
        String[] validifiedWord = v.isValid(inputWord);
        assertEquals("word", validifiedWord[0]);
        assertEquals("!", validifiedWord[1]);
    }

    /**
     * Tests scenario where the word expression has punctuation and word, with typo spaces
     */
    @Test
    @Timeout(1000)
    public void testPuncWordSpaces() {
        String inputWord = "    !   word  ";
        String[] validifiedWord = v.isValid(inputWord);
        assertEquals("word", validifiedWord[0]);
        assertEquals("!", validifiedWord[1]);
    }
}
