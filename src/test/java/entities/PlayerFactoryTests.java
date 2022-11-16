package entities;

import exceptions.IdInUseException;
import exceptions.InvalidDisplayNameException;
import org.junit.*;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class PlayerFactoryTests {
    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test a player is added successfully
     */
    @Test(timeout = 1000)
    public void testPlayerValid() throws IdInUseException, InvalidDisplayNameException {
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        Player player1 = playerfac.createPlayer("player1", "1");
        assertEquals("Player 1 should have Display Name player1", "player1", player1.getDisplayName());
        assertEquals("Player 1 should have ID 1", "1", player1.getPlayerId());
    }

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();
    // This says it is deprecated, but we are using JUnit 4, and AssertThrows was introduced in JUnit 5

    /**
     * Test a player with a used ID is not added, with IdInUseException thrown.
     */
    @Test(timeout = 1000)
    public void testUsedId() throws IdInUseException, InvalidDisplayNameException {
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        Player player1 = playerfac.createPlayer("player1", "1");
        assertEquals("Player 1 should have Display Name player1", "player1", player1.getDisplayName());
        assertEquals("Player 1 should have ID 1", "1", player1.getPlayerId());

        exceptionRule.expect(IdInUseException.class);
        playerfac.createPlayer("player2", "1");
    }

    /**
     * Test a player with an invalid name is not added, with InvalidDisplayNameException thrown.
     */
    @Test(timeout = 1000)
    public void testInvalidName() throws IdInUseException, InvalidDisplayNameException {
        PlayerFactory playerfac = new PlayerFactory(displayName -> false);
        exceptionRule.expect(InvalidDisplayNameException.class);
        playerfac.createPlayer("player1", "1");
    }
}
