package entities;

import exceptions.InvalidWordException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.*;

public class WordFactoryTests {
    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test that a valid word is added successfully with create().
     */
    @Test
    @Timeout(1)
    public void testValidWord() throws InvalidWordException {
        Player player1 = new Player("player1", "1");
        String newword = "bloop";

        ValidityChecker validitySuccess = word -> true;
        WordFactory wordfac = new WordFactory(validitySuccess);
        Word bloop = wordfac.create(newword, player1);
        assertEquals("bloop", bloop.getWord(),
                "The word should be created, with string bloop");
    }

    /**
     * Test that an invalid word is refused successfully with create(), with InvalidWordException thrown.
     */
    @Test
    @Timeout(1)
    public void testInvalidWord() {
        Player player1 = new Player("player1", "1");
        String newword = "bloop";

        ValidityChecker validityFailure = word -> false;
        WordFactory wordfac = new WordFactory(validityFailure);
        assertThrows(InvalidWordException.class, () -> wordfac.create(newword, player1));
    }
}
