package entities.validity_checkers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class WordValidityCheckerRegularTests {

    public final WordValidityChecker wvc =
            new WordValidityCheckerRegular();

    @BeforeEach
    public void setUp() {}

    @AfterEach
    public void tearDown() {}

    /**
     * Tests scenario where a word with no word value is checked
     */
    @Test
    @Timeout(1000)
    public void testNoWord() {
        String word = "";
        String validifiedWord = wvc.isWordValid(word);
        assertNull(validifiedWord);
    }

    /**
     * Tests scenario where a word with a letter in the alphabet is checked
     */
    @Test
    @Timeout(1000)
    public void testLetter() {
        String word = "H";
        String validifiedWord = wvc.isWordValid(word);
        assertEquals("H", validifiedWord);
    }

    /**
     * Tests scenario where a word with multiple letters in the alphabet (under the
     * character cap) is checked
     */
    @Test
    @Timeout(1000)
    public void testMultipleLettersUnderCap() {
        String word = "ungabUnga";
        String validifiedWord = wvc.isWordValid(word);
        assertEquals("ungabUnga", validifiedWord);
    }

    /**
     * Tests scenario where a word with multiple letters in the alphabet (over the
     * character cap) is checked
     */
    @Test
    @Timeout(1000)
    public void testMultipleLettersOverCap() {
        String word = "qwertyuiopasdfghjklzxcvbnm";
        String validifiedWord = wvc.isWordValid(word);
        assertEquals("qwertyuiopasdfghjklzxc", validifiedWord);
    }

    /**
     * Tests scenario where a word including non-letters is checked
     */
    @Test
    @Timeout(1000)
    public void testIncludesNonLetters() {
        String word = "hell0";
        String validifiedWord = wvc.isWordValid(word);
        assertNull(validifiedWord);
    }
}
