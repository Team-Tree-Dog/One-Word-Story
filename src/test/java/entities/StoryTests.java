package entities;

import exceptions.InvalidWordException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.*;

public class StoryTests {
    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Tests that a word is valid and is added successfully
     */
    @Test
    @Timeout(1000)
    public void testAddWordSuccess() throws InvalidWordException {
        Player player1 = new Player("player1", "1");
        String newword = "bloop";

        ValidityChecker validitySuccess = word -> true;
        WordFactory wordfac = new WordFactory(validitySuccess);
        Story story = new Story(wordfac);
        story.addWord(newword, player1);

        assertEquals("bloop ", story.toString(), "The word should be created, with string bloop");
    }

    /**
     * Tests that a word is invalid and is refused
     */
    @Test
    @Timeout(1000)
    public void testAddWordFail() {
        Player player1 = new Player("player1", "1");
        String newword = "bloop";

        ValidityChecker validityFailure = word -> false;
        WordFactory wordfac = new WordFactory(validityFailure);
        Story story = new Story(wordfac);
        assertThrows(InvalidWordException.class, () -> story.addWord(newword, player1));
    }

    /**
     * Tests the toString method for a blank story.
     */
    @Test
    @Timeout(1000)
    public void testBlankStory() {
        ValidityChecker validitySuccess = word -> true;
        WordFactory wordfac = new WordFactory(validitySuccess);
        Story story = new Story(wordfac);
        assertEquals("", story.toString(), "The Story.toString() should just be a blank string.");
    }

    /**
     * Tests the toString method for a story with multiple words.
     */
    @Test
    @Timeout(1000)
    public void testStorytoString() throws InvalidWordException {
        Player player1 = new Player("player1", "1");
        ValidityChecker validitySuccess = word -> true;
        WordFactory wordfac = new WordFactory(validitySuccess);
        Story story = new Story(wordfac);

        story.addWord("Once", player1);
        story.addWord("upon", player1);
        story.addWord("a", player1);
        story.addWord("time", player1);

        assertEquals("Once upon a time ", story.toString(),
                "The Story.toString() should just be Once upon a time, with a space at the end.");
    }
}
