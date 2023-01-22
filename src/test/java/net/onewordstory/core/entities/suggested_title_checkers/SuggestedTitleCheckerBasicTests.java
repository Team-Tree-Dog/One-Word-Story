package net.onewordstory.core.entities.suggested_title_checkers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SuggestedTitleCheckerBasicTests {

    SuggestedTitleChecker checker = new SuggestedTitleCheckerBasic();

    @BeforeEach
    public void setup () {}

    @AfterEach
    public void teardown () {}

    /**
     * Runs through a large list of valid suggested titles and asserts
     * they are validated by the checker
     */
    @Test
    @Timeout(1000)
    public void testValidMany () {
        String[] validNames = {"Huge Spelling Porbemls: LOL11", "Cannot believe I read this",
                "Don't write more stories, please"};

        for (String s : validNames) {
            assertTrue(checker.checkValid(s), "Incorrect prediction: " + s);
        }
    }

    /**
     * Runs through a large list of invalid display names and asserts
     * they are rejected by the checker
     */
    @Test
    @Timeout(1000)
    public void testInvalidMany () {
        String[] validNames = {"23534xxxxx", "G$#%^*", "LL", "", "23456", "frlng zaz aza",
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"};

        for (String s : validNames) {
            assertFalse(checker.checkValid(s), "Incorrect prediction: " + s);
        }
    }
}
