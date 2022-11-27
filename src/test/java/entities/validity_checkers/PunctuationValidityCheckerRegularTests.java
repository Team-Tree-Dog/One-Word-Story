package entities.validity_checkers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PunctuationValidityCheckerRegularTests {

    /**
     * We use an implementation of a validity checker where the word value is irrelevant
     * This allows us to specifically test PunctuationValidityCheckerRegular
     */
    public final PunctuationValidityChecker puncValidityChecker =
            new PunctuationValidityCheckerRegular();
    public final ValidityCheckerFacade v = new ValidityCheckerFacade(
            puncValidityChecker,
            wordValidityChecker -> ""
    );

    @Before
    public void setUp() {}

    @After
    public void tearDown() {}

    /**
     * Tests scenario where a word with no punctuation is checked
     */
    @Test(timeout = 1000)
    public void testNoPunctuation() {
        String word = "noPunc";
        String validifiedWord = v.isValid(word);
        assertEquals("", validifiedWord);
    }

    /**
     * Tests scenario where a word with invalid punctuation is checked
     */
    @Test(timeout = 1000)
    public void testInvalidPunctuation() {
        String word = "not_right noPunc";
        String validifiedWord = v.isValid(word);
        assertNull(validifiedWord);
    }

    /**
     * Tests scenario where a word with a single, non-duplicable punctuation character is checked
     */
    @Test(timeout = 1000)
    public void testNonDuplicableSingle() {
        String word = "; hotdog";
        String validifiedWord = v.isValid(word);
        assertEquals(";", validifiedWord);
    }

    /**
     * Tests scenario where a word with a single, duplicable period for punctuation is checked
     */
    @Test(timeout = 1000)
    public void testDuplicablePeriodSingle() {
        String word = ". sausage";
        String validifiedWord = v.isValid(word);
        assertEquals(".", validifiedWord);
    }

    /**
     * Tests scenario where a word with a single, duplicable mark for punctuation is checked
     */
    @Test(timeout = 1000)
    public void testDuplicableMarkSingle() {
        String word = "? beans";
        String validifiedWord = v.isValid(word);
        assertEquals("?", validifiedWord);
    }

    /**
     * Tests scenario where a word with multiple, non-duplicable punctuation characters is checked
     */
    @Test(timeout = 1000)
    public void testNonDuplicableMultiple() {
        String word = ";,: equinox";
        String validifiedWord = v.isValid(word);
        assertNull(validifiedWord);
    }

    /**
     * Tests scenario where a word with multiple, duplicable periods for punctuation is checked
     */
    @Test(timeout = 1000)
    public void testDuplicablePeriodMultiple() {
        String word = "... Stewart";
        String validifiedWord = v.isValid(word);
        assertEquals("...", validifiedWord);
    }

    /**
     * Tests scenario where a word with multiple, duplicable marks for punctuation is checked
     */
    @Test(timeout = 1000)
    public void testDuplicableMarkMultiple() {
        String word = "?!! huhhhhh";
        String validifiedWord = v.isValid(word);
        assertEquals("?!!", validifiedWord);
    }

    /**
     * Tests scenario where a word with non-duplicable characters combined with duplicable ones
     * for punctuation is checked
     */
    @Test(timeout = 1000)
    public void testSingleNonDuplicableAndDuplicable() {
        String word = ",.. Ford-F150";
        String validifiedWord = v.isValid(word);
        assertNull(validifiedWord);
    }

    /**
     * Tests scenario where a word with more than three valid punctuation characters is checked
     */
    @Test(timeout = 1000)
    public void testMoreThanThreeValidCharacters() {
        String word = "?????????????? confusion";
        String validifiedWord = v.isValid(word);
        assertEquals("???", validifiedWord);
    }

    /**
     * Tests scenario where a word with more than three punctuation characters
     * (the first three only are valid) is checked
     */
    @Test(timeout = 1000)
    public void testFirstThreeValidCharacters() {
        String word = "...kkkkkk confusion";
        String validifiedWord = v.isValid(word);
        assertEquals("...", validifiedWord);
    }

    /**
     * Tests scenario where a word with more than three invalid punctuation characters is checked
     */
    @Test(timeout = 1000)
    public void testMoreThanThreeInvalidCharacters() {
        String word = "AAAAAAAAA screams";
        String validifiedWord = v.isValid(word);
        assertNull(validifiedWord);
    }
}
