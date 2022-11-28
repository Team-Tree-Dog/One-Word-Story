package entities;

import exceptions.IdInUseException;
import exceptions.InvalidDisplayNameException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerFactoryTests {
    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test a player is added successfully
     */
    @Test
    @Timeout(1000)
    public void testPlayerValid() throws IdInUseException, InvalidDisplayNameException {
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        Player player1 = playerfac.createPlayer("player1", "1");
        assertEquals("player1", player1.getDisplayName(), "Player 1 should have Display Name player1");
        assertEquals("1", player1.getPlayerId(), "Player 1 should have ID 1");
    }

    /**
     * Test a player with a used ID is not added, with IdInUseException thrown.
     */
    @Test
    @Timeout(1000)
    public void testUsedId() throws IdInUseException, InvalidDisplayNameException {
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        Player player1 = playerfac.createPlayer("player1", "1");
        assertEquals("player1", player1.getDisplayName(), "Player 1 should have Display Name player1");
        assertEquals("1", player1.getPlayerId(), "Player 1 should have ID 1");

        assertThrows(IdInUseException.class, () -> playerfac.createPlayer("player2", "1"));
    }

    /**
     * Test a player with an invalid name is not added, with InvalidDisplayNameException thrown.
     */
    @Test
    @Timeout(1000)
    public void testInvalidName() {
        PlayerFactory playerfac = new PlayerFactory(displayName -> false);
        assertThrows(InvalidDisplayNameException.class, () -> playerfac.createPlayer("player1", "1"));
    }
}
