package entities.games;

import entities.Player;
import entities.validity_checkers.ValidityCheckerFacade;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;


public class GameTests {

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

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    private static class CustomizableTestGame extends Game {

        private final Queue<Player> players;

        /**
         * Constructor for CustomizableTestGame
         * @param initialPlayers The initial players in this CustomizableTestGame
         */

        public CustomizableTestGame(Queue<Player> initialPlayers) {
            super(15, new TestValidityCheckerTrue());
            players = new LinkedList<>(initialPlayers);
        }

        @Override
        public @NotNull Collection<Player> getPlayers() {
            return players;
        }

        @Override
        public boolean isGameOver() {
            return players.size() < 2;
        }

        @Override
        public void onTimerUpdateLogic() {

        }

        @Override
        public Player getPlayerById(String playerId) {
            return players.stream().filter(p -> p.getPlayerId().equals(playerId)).findAny().orElse(null);
        }

        @Override
        public boolean removePlayer(Player playerToRemove) {
            return players.remove(playerToRemove);
        }

        @Override
        public boolean addPlayer(Player playerToAdd) {
            return players.add(playerToAdd);
        }

        @Override
        public boolean switchTurnLogic() {
            setSecondsLeftInCurrentTurn(getSecondsPerTurn());
            return players.add(players.remove());
        }

        @Override
        public @NotNull Player getCurrentTurnPlayer() {
            return this.players.peek();
        }
    }

    @Test
    @Timeout(1)
    public void onTimerUpdate() {
    }

    @Test
    @Timeout(1)
    public void getPlayerById() {
        Game testGame = new CustomizableTestGame(new LinkedList<>());
        Player player1 = new Player("player1", "1");
        testGame.addPlayer(player1);

        // Sanity Check
        assertTrue(testGame.getPlayers().contains(player1), "Player 1 should be in the game, but it is not.");
        // Main Assertion
        assertEquals(player1, testGame.getPlayerById("1"), "We should obtain player 1, but we don't.");
    }

    @Test
    @Timeout(1)
    public void removePlayer() {
        Game testGame = new CustomizableTestGame(new LinkedList<>());
        Player player1 = new Player("player1", "1");
        testGame.addPlayer(player1);

        // Sanity Check
        assertTrue(testGame.getPlayers().contains(player1), "Player 1 should be in the game, but it is not.");
        // What we will test:
        testGame.removePlayer(player1);
        // Main Assertion:
        assertFalse(testGame.getPlayers().contains(player1),
                "Player 1 shouldn't be in the game anymore, but it still is.");
    }

    @Test
    @Timeout(1)
    public void addPlayer() {
        Game testGame = new CustomizableTestGame(new LinkedList<>());
        Player player1 = new Player("player1", "1");
        testGame.addPlayer(player1);

        // Our Assertion
        assertTrue(testGame.getPlayers().contains(player1), "Player 1 should be in the game, but it is not.");
    }

    @Test
    @Timeout(1)
    public void switchTurn() {
        Game testGame = new CustomizableTestGame(new LinkedList<>());
        Player player1 = new Player("player1", "1");
        Player player2 = new Player("player2", "2");

        testGame.addPlayer(player1);
        testGame.addPlayer(player2);

        // Sanity Check
        assertEquals(player1, testGame.getCurrentTurnPlayer(), "It should be Player 1's turn, but it isn't.");

        // Here comes the bit we want to test:
        testGame.switchTurn();
        assertEquals(player2, testGame.getCurrentTurnPlayer(), "It should be Player 2's turn, but it isn't.");

    }

    @Test
    @Timeout(1)
    public void getCurrentTurnPlayer() {
        Game testGame = new CustomizableTestGame(new LinkedList<>());
        Player player1 = new Player("player1", "1");
        Player player2 = new Player("player2", "2");

        testGame.addPlayer(player1);
        testGame.addPlayer(player2);

        // What we want to check:
        assertEquals(player1, testGame.getCurrentTurnPlayer(), "It should be Player 1's turn, but it isn't.");
        assertNotSame(player2, testGame.getCurrentTurnPlayer(), "It shouldn't be Player 2's turn, but it is.");
    }
}
