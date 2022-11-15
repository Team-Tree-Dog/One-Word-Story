package entities;

import exceptions.InvalidWordException;
import org.junit.*;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class WordFactoryTests {
    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test that a valid word is added successfully with create().
     */
    @Test(timeout = 1000)
    public void testValidWord() throws InvalidWordException {
        Player player1 = new Player("player1", "1");
        String newword = "bloop";

        ValidityChecker validityFailure = word -> true;
        WordFactory wordfac = new WordFactory(validityFailure);
        Word bloop = wordfac.create(newword, player1);
        assertEquals("The word should be created, with string bloop", "bloop", bloop.getWord());
    }

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();
    // This says it is deprecated, but we are using JUnit 4, and AssertThrows was introduced in JUnit 5

    /**
     * Test that an invalid word is refused successfully with create(), with InvalidWordException thrown.
     */
    @Test(timeout = 1000)
    public void testInvalidWord() throws InvalidWordException {
        Player player1 = new Player("player1", "1");
        String newword = "bloop";

        ValidityChecker validityFailure = word -> false;
        WordFactory wordfac = new WordFactory(validityFailure);
        exceptionRule.expect(InvalidWordException.class);
        wordfac.create(newword, player1);

    }
}
