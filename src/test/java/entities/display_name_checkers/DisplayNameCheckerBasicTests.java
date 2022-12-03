package entities.display_name_checkers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DisplayNameCheckerBasicTests {

    DisplayNameCheckerBasic checker = new DisplayNameCheckerBasic();

    @BeforeEach
    public void setup () {}

    @AfterEach
    public void teardown () {}

    /**
     * Runs through a large list of valid display names and asserts
     * they are validated by the checker
     */
    @Test
    @Timeout(1000)
    public void testValidMany () {
        String[] validNames = {"April", "Alex123", "Patri3k", "cotapaxi64", "a435780"};

        for (String s : validNames) {
            assertTrue(checker.checkValid(s));
        }
    }

    /**
     * Runs through a large list of invalid display names and asserts
     * they are rejected by the checker
     */
    @Test
    @Timeout(1000)
    public void testInvalidMany () {
        String[] validNames = {"23534, 4AHfdhjks", "Hadoo%", "G$#%^*", "aaaaaaaaaaaaaaaaaaaaa"};

        for (String s : validNames) {
            assertFalse(checker.checkValid(s));
        }
    }
}
