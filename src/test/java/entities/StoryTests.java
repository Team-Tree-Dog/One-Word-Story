package entities;

import exceptions.InvalidWordException;
import org.junit.After;
import org.junit.Before;
import org.junit.*;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class StoryTests {
    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Tests that a word is valid and is added successfully
     */
    @Test(timeout = 1000)
    public void testAddWordSuccess() throws InvalidWordException {
        Player player1 = new Player("player1", "1");
        String newword = "bloop";

        ValidityChecker validitySuccess = word -> true;
        WordFactory wordfac = new WordFactory(validitySuccess);
        Story story = new Story(wordfac);
        story.addWord(newword, player1);

        assertEquals("The word should be created, with string bloop", "bloop ", story.toString());
    }

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();
    // This says it is deprecated, but we are using JUnit 4, and AssertThrows was introduced in JUnit 5

    /**
     * Tests that a word is invalid and is refused
     */
    @Test(timeout = 1000)
    public void testAddWordFail() throws InvalidWordException {
        Player player1 = new Player("player1", "1");
        String newword = "bloop";

        ValidityChecker validityFailure = word -> false;
        WordFactory wordfac = new WordFactory(validityFailure);
        Story story = new Story(wordfac);
        exceptionRule.expect(InvalidWordException.class);
        story.addWord(newword, player1);
    }

    /**
     * Tests the toString method for a blank story.
     */
    @Test(timeout = 1000)
    public void testBlankStory() {
        ValidityChecker validitySuccess = word -> true;
        WordFactory wordfac = new WordFactory(validitySuccess);
        Story story = new Story(wordfac);
        assertEquals("The Story.toString() should just be a blank string.", "", story.toString());
    }

    /**
     * Tests the toString method for a story with multiple words.
     */
    @Test(timeout = 1000)
    public void testStorytoString() throws InvalidWordException {
        Player player1 = new Player("player1", "1");
        ValidityChecker validitySuccess = word -> true;
        WordFactory wordfac = new WordFactory(validitySuccess);
        Story story = new Story(wordfac);

        story.addWord("Once", player1);
        story.addWord("upon", player1);
        story.addWord("a", player1);
        story.addWord("time", player1);

        assertEquals("The Story.toString() should just be Once upon a time, with a space at the end.",
                "Once upon a time ", story.toString());
    }
}
