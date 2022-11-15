package entities;

import org.junit.*;

import static org.junit.Assert.*;

public class PlayerTests {
    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test that two players are equal for equal player IDs
     */
    @Test(timeout = 1000)
    public void testEquals() {
        Player player1 = new Player("player1", "1");
        Player player2 = new Player("player2", "1");
        // Notice how the display names are not the same.
        assertEquals("The players should be the same because the IDs are the same.", player1, player2);
    }

    /**
     * Test that two players are not equal for unequal player IDs
     */
    @Test(timeout = 1000)
    public void testUnequals() {
        Player player1 = new Player("player1", "1");
        Player player2 = new Player("player2", "2");
        assertNotEquals("The players should not be the same because the IDs are not the same.", player1, player2);
    }
}
