package entities.validity_checkers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class WordValidityCheckerRegularTests {

    /**
     * We use an implementation of a validity checker where the punctuation value is irrelevant
     * This allows us to specifically test WordValidityCheckerRegular
     */
    public final WordValidityChecker wordValidityChecker =
            new WordValidityCheckerRegular();
    public final ValidityCheckerFacade v = new ValidityCheckerFacade(
            puncValidityChecker -> "",
            wordValidityChecker
    );

    @Before
    public void setUp() {}

    @After
    public void tearDown() {}

    /**
     * Tests scenario where a word with no word value is checked
     */
    @Test(timeout = 1000)
    public void testNoWord() {
        String word = "";
        String validifiedWord = v.isValid(word);
        assertEquals("", validifiedWord);
    }

    /**
     * Tests scenario where a word with a letter in the alphabet is checked
     */
    @Test(timeout = 1000)
    public void testLetter() {
        String word = "thisDoesNotMatter H";
        String validifiedWord = v.isValid(word);
        assertEquals("H", validifiedWord);
    }

    /**
     * Tests scenario where a word with multiple letters in the alphabet (under the
     * character cap) is checked
     */
    @Test(timeout = 1000)
    public void testMultipleLettersUnderCap() {
        String word = "... ungabUnga";
        String validifiedWord = v.isValid(word);
        assertEquals("ungabUnga", validifiedWord);
    }

    /**
     * Tests scenario where a word with multiple letters in the alphabet (over the
     * character cap) is checked
     */
    @Test(timeout = 1000)
    public void testMultipleLettersOverCap() {
        String word = "qwertyuiopasdfghjklzxcvbnm";
        String validifiedWord = v.isValid(word);
        assertEquals("qwertyuiopasdfghjklzxc", validifiedWord);
    }

    /**
     * Tests scenario where a word including non-letters is checked
     */
    @Test(timeout = 1000)
    public void testIncludesNonLetters() {
        String word = "hell0";
        String validifiedWord = v.isValid(word);
        assertNull(validifiedWord);
    }
}
