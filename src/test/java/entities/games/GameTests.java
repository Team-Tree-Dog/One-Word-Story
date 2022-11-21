package entities.games;

import entities.Player;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import static org.junit.Assert.*;

public class GameTests {

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    private static class CustomizableTestGame extends Game {

        private final Queue<Player> players;

        /**
         * Constructor for CustomizableTestGame
         * @param initialPlayers The initial players in this CustomizableTestGame
         */

        public CustomizableTestGame(Queue<Player> initialPlayers) {
            super(15, word -> true);
            players = new LinkedList<>(initialPlayers);
        }

        @Override
        public Collection<Player> getPlayers() {
            return players;
        }

        @Override
        public boolean isGameOver() {
            return players.size() < 2;
        }

        @Override
        public void onTimerUpdate() {

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
        public boolean switchTurn() {
            setSecondsLeftInCurrentTurn(getSecondsPerTurn());
            return players.add(players.remove());
        }

        @Override
        public Player getCurrentTurnPlayer() {
            return this.players.peek();
        }
    }

    @Test(timeout = 1000)
    public void onTimerUpdate() {
    }

    @Test(timeout = 1000)
    public void getPlayerById() {
        Game testGame = new CustomizableTestGame(new LinkedList<>());
        Player player1 = new Player("player1", "1");
        testGame.addPlayer(player1);

        // Sanity Check
        assertTrue("Player 1 should be in the game, but it is not.", testGame.getPlayers().contains(player1));
        // Main Assertion
        assertEquals("We should obtain player 1, but we don't.", player1, testGame.getPlayerById("1"));
    }

    @Test(timeout = 1000)
    public void removePlayer() {
        Game testGame = new CustomizableTestGame(new LinkedList<>());
        Player player1 = new Player("player1", "1");
        testGame.addPlayer(player1);

        // Sanity Check
        assertTrue("Player 1 should be in the game, but it is not.", testGame.getPlayers().contains(player1));
        // What we will test:
        testGame.removePlayer(player1);
        // Main Assertion:
        assertFalse("Player 1 shouldn't be in the game anymore, but it still is.",
                testGame.getPlayers().contains(player1));
    }

    @Test(timeout = 1000)
    public void addPlayer() {
        Game testGame = new CustomizableTestGame(new LinkedList<>());
        Player player1 = new Player("player1", "1");
        testGame.addPlayer(player1);

        // Our Assertion
        assertTrue("Player 1 should be in the game, but it is not.", testGame.getPlayers().contains(player1));
    }

    @Test(timeout = 1000)
    public void switchTurn() {
        Game testGame = new CustomizableTestGame(new LinkedList<>());
        Player player1 = new Player("player1", "1");
        Player player2 = new Player("player2", "2");

        testGame.addPlayer(player1);
        testGame.addPlayer(player2);

        // Sanity Check
        assertEquals("It should be Player 1's turn, but it isn't.", player1, testGame.getCurrentTurnPlayer());

        // Here comes the bit we want to test:
        testGame.switchTurn();
        assertEquals("It should be Player 2's turn, but it isn't.", player2, testGame.getCurrentTurnPlayer());

    }

    @Test(timeout = 1000)
    public void getCurrentTurnPlayer() {
        Game testGame = new CustomizableTestGame(new LinkedList<>());
        Player player1 = new Player("player1", "1");
        Player player2 = new Player("player2", "2");

        testGame.addPlayer(player1);
        testGame.addPlayer(player2);

        // What we want to check:
        assertEquals("It should be Player 1's turn, but it isn't.", player1, testGame.getCurrentTurnPlayer());
        assertNotSame("It shouldn't be Player 2's turn, but it is.", player2, testGame.getCurrentTurnPlayer());
    }
}
