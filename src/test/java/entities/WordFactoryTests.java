package entities;

import entities.validity_checkers.ValidityCheckerFacade;
import exceptions.InvalidWordException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.*;

public class WordFactoryTests {

    /**
     * Test Validity Checker Facade which always validates and does not modify input
     */
    static class TestValidityCheckerTrue extends ValidityCheckerFacade {

        public TestValidityCheckerTrue() {
            super((p) -> p, (w) -> w);
        }

        @Override
        public String[] isValid(String word) {
            return new String[]{word};
        }
    }

    /**
     * Test Validity Checker Facade which always rejects
     */
    static class TestValidityCheckerFalse extends ValidityCheckerFacade {

        public TestValidityCheckerFalse() {
            super((p) -> p, (w) -> w);
        }

        @Override
        public String[] isValid(String word) {
            return null;
        }
    }

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

        WordFactory wordfac = new WordFactory(new TestValidityCheckerTrue());
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

        WordFactory wordfac = new WordFactory(new TestValidityCheckerFalse());
        assertThrows(InvalidWordException.class, () -> wordfac.create(newword, player1));
    }
}
