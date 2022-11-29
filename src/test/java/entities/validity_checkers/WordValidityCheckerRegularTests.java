package entities.validity_checkers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class WordValidityCheckerRegularTests {

    public final WordValidityChecker wvc =
            new WordValidityCheckerRegular();

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
        String validifiedWord = wvc.isWordValid(word);
        assertNull(validifiedWord);
    }

    /**
     * Tests scenario where a word with a letter in the alphabet is checked
     */
    @Test(timeout = 1000)
    public void testLetter() {
        String word = "H";
        String validifiedWord = wvc.isWordValid(word);
        assertEquals("H", validifiedWord);
    }

    /**
     * Tests scenario where a word with multiple letters in the alphabet (under the
     * character cap) is checked
     */
    @Test(timeout = 1000)
    public void testMultipleLettersUnderCap() {
        String word = "ungabUnga";
        String validifiedWord = wvc.isWordValid(word);
        assertEquals("ungabUnga", validifiedWord);
    }

    /**
     * Tests scenario where a word with multiple letters in the alphabet (over the
     * character cap) is checked
     */
    @Test(timeout = 1000)
    public void testMultipleLettersOverCap() {
        String word = "qwertyuiopasdfghjklzxcvbnm";
        String validifiedWord = wvc.isWordValid(word);
        assertEquals("qwertyuiopasdfghjklzxc", validifiedWord);
    }

    /**
     * Tests scenario where a word including non-letters is checked
     */
    @Test(timeout = 1000)
    public void testIncludesNonLetters() {
        String word = "hell0";
        String validifiedWord = wvc.isWordValid(word);
        assertNull(validifiedWord);
    }
}
