package usecases.disconnecting;

import entities.*;
import entities.games.Game;

import exceptions.GameRunningException;
import exceptions.IdInUseException;

import exceptions.InvalidDisplayNameException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing Disconnecting Use Case
 */
public class DcInteractorTests {

    private DcInteractor dcInteractor;
    private static final List<Player> players = new ArrayList<>();
    private static final DisplayNameChecker displayNameChecker = displayName -> true;
    private static final PlayerFactory playerFactory = new PlayerFactory(displayNameChecker);

    @Before
    public void setUp() {}

    @After
    public void teardown() {}

    /**
     * Testing disconnecting player who are in the game
     *
     * @throws IdInUseException
     * @throws GameRunningException
     */
    @Test(timeout = 1000)
    public void testDisconnectPlayerFromGame() throws
            IdInUseException, GameRunningException,
            InterruptedException, InvalidDisplayNameException {
        Player player1 = playerFactory.createPlayer("John", "1");
        Player player2 = playerFactory.createPlayer("Kate", "2");
        players.add(player1);
        players.add(player2);

        TestGame testGame = new TestGame(players);
        TestLobbyManager lm = new TestLobbyManager(testGame);

        assertTrue(lm.getPlayersFromGame().contains(player1));
        assertTrue(lm.getPlayersFromGame().contains(player2));

        DcOutputBoundary dcOutputBoundary = data -> {};
        dcInteractor = new DcInteractor(lm, dcOutputBoundary);

        DcInputData data = new DcInputData(players.get(1).getPlayerId());
        dcInteractor.disconnect(data);

        while(lm.getPlayersFromGame().contains(player2)) {
            Thread.onSpinWait();
        }

        assertFalse(lm.getPlayersFromGame().contains(player2));
        assertTrue(lm.getPlayersFromGame().contains(player1));
    }

    /**
     * Testing disconnecting player who are in the pool
     *
     * @throws IdInUseException
     * @throws GameRunningException
     */
    @Test(timeout = 1000)
    public void testDisconnectPlayerFromPool() throws
            IdInUseException, GameRunningException,
            InterruptedException, InvalidDisplayNameException {
        Player player3 = playerFactory.createPlayer("Nick", "3");
        Player player4 = playerFactory.createPlayer("Ann", "4");

        TestGame testGame = new TestGame(players);
        TestLobbyManager lm = new TestLobbyManager(testGame);

        lm.addPlayerToPool(player3, new PlayerPoolListener() {
            @Override
            public void onJoinGamePlayer(Game game) {}

            @Override
            public void onCancelPlayer() {}
        });
        lm.addPlayerToPool(player4, new PlayerPoolListener() {
            @Override
            public void onJoinGamePlayer(Game game) {}

            @Override
            public void onCancelPlayer() {}
        });

        assertTrue(lm.getPlayersFromPool().contains(player3));
        assertTrue(lm.getPlayersFromPool().contains(player4));

        DcOutputBoundary dcOutputBoundary = data -> {};
        dcInteractor = new DcInteractor(lm, dcOutputBoundary);

        DcInputData data = new DcInputData(player4.getPlayerId());
        dcInteractor.disconnect(data);

        try {
            while (lm.getPlayersFromPool().contains(player4)) {
                Thread.onSpinWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertFalse(lm.getPlayersFromPool().contains(player4));
        assertTrue(lm.getPlayersFromPool().contains(player3));
    }

    /**
     * Test a malicious or bugged call to disconnect where a player
     * is NOT in the pool, and game is null. A fail code should be returned
     */
    @Test(timeout = 1000)
    public void testGameNullPlayerNotInPool () {

    }

    /**
     * Test a call to disconnect a player who is neither in the game nor in the
     * pool. Fail code should be returned
     */
    @Test(timeout = 1000)
    public void testPlayerNowhere () {

    }

    /**
     * Using descendant of LobbyManager to set the testGame
     */
    private static class TestLobbyManager extends LobbyManager {

        Game game;

        public TestLobbyManager(Game game) throws GameRunningException {
            super(playerFactory, (settings, initialPlayers) -> game);
            this.game = game;
            this.setGame(game);
        }

        @Override
        public void setGame(Game game) throws GameRunningException { super.setGame(game); }
    }

    /**
     * Using descendant of Game with implemented methods
     */
    private static class TestGame extends Game {

        private final List<Player> players;

        public TestGame(List<Player> players) {
            super(10, word -> true);
            this.players = players;
        }

        @Override
        public Collection<Player> getPlayers() { return players; }

        @Override
        public boolean isGameOver() { return false; }

        @Override
        public void onTimerUpdate() {}

        @Override
        public Player getPlayerById(String playerId) {
            for (Player player : players)
                if (player.getPlayerId().equals(playerId))
                    return player;
            return null;
        }

        @Override
        public boolean removePlayer(Player playerToRemove) { return players.remove(playerToRemove); }

        @Override
        public boolean addPlayer(Player playerToAdd) { return players.add(playerToAdd); }

        @Override
        public boolean switchTurn() { return false; }

        @Override
        public Player getCurrentTurnPlayer() { return null; }
    }

}