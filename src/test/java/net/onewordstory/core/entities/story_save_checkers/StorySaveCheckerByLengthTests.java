package net.onewordstory.core.entities.story_save_checkers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static net.onewordstory.core.entities.story_save_checkers.Action.REJECTED;
import static net.onewordstory.core.entities.story_save_checkers.Action.ACCEPTED;
import static org.junit.jupiter.api.Assertions.*;

public class StorySaveCheckerByLengthTests {

    private final StorySaveChecker checker = new StorySaveCheckerByLength();

    @BeforeEach
    public void setup () {}

    @AfterEach
    public void teardown () {}

    /**
     * Tests a string with no characters, should end up being rejected.
     */
    @Test
    @Timeout(1000)
    public void testZeroLength () {
        String zeroStoryString = "";
        FilterOutput filterOutput = checker.filterStory(zeroStoryString);
        assertEquals(filterOutput.getAction(), REJECTED, "Should be REJECTED");
    }

    /**
     * Tests a string with less than minimum characters, should end up being rejected.
     */
    @Test
    @Timeout(1000)
    public void testOneLength () {
        String oneStoryString = "a";
        FilterOutput filterOutput = checker.filterStory(oneStoryString);
        assertEquals(filterOutput.getAction(), REJECTED, "Should be REJECTED");
    }

    /**
     * Tests a string with bare minimum number of characters, should end up being rejected.
     */
    @Test
    @Timeout(1000)
    public void testMinLength () {
        String threeStoryString = "aaa";
        FilterOutput filterOutput = checker.filterStory(threeStoryString);
        assertEquals(filterOutput.getAction(), ACCEPTED, "Should be ACCEPTED");
    }

    /**
     * Tests a string with a number of characters above minimum, should end up being rejected.
     */
    @Test
    @Timeout(1000)
    public void testArbLength () {
        String arbStoryString = "kjdfkfdj";
        FilterOutput filterOutput = checker.filterStory(arbStoryString);
        assertEquals(filterOutput.getAction(), ACCEPTED, "Should be ACCEPTED");
    }
}
