package entities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.*;


public class PlayerTests {
    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test that two players are equal for equal player IDs
     */
    @Test
    @Timeout(1)
    public void testEquals() {
        Player player1 = new Player("player1", "1");
        Player player2 = new Player("player2", "1");
        // Notice how the display names are not the same.
        assertEquals(player1, player2,"The players should be the same because the IDs are the same.");
    }

    /**
     * Test that two players are not equal for unequal player IDs
     */
    @Test
    @Timeout(1)
    public void testUnequals() {
        Player player1 = new Player("player1", "1");
        Player player2 = new Player("player2", "2");
        assertNotEquals(player1, player2, "The players should not be the same because the IDs are not the same.");
    }
}
