package usecases.disconnecting;

import entities.*;
import entities.display_name_checkers.DisplayNameChecker;
import entities.games.Game;
import entities.games.GameFactory;
import entities.statistics.PerPlayerIntStatistic;
import entities.validity_checkers.ValidityCheckerFacade;
import exceptions.GameDoesntExistException;
import exceptions.GameRunningException;
import exceptions.IdInUseException;
import exceptions.InvalidDisplayNameException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import usecases.Response;
import usecases.ThreadRegister;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing Disconnecting Use Case
 */
public class DcInteractorTests {

    /**
     * Test Validity Checker Facade which always validates and does not modify input
     */
    static class TestValidityCheckerFacadeTrue extends ValidityCheckerFacade {

        public TestValidityCheckerFacadeTrue() {
            super((p) -> p, (w) -> w);
        }

        @Override
        public String[] isValid(String word) {
            return new String[]{word};
        }
    }

    private DcInteractor dcInteractor;

    private static final ThreadRegister register = new ThreadRegister();

    private static final List<Player> players = new ArrayList<>();
    private static final DisplayNameChecker displayNameChecker = displayName -> true;
    private static final PlayerFactory playerFactory = new PlayerFactory(displayNameChecker);



    /**
     * Testing disconnecting player who are in the game
     */
    @Test
    @Timeout(1)
    public void testDisconnectPlayerFromGame() throws
            IdInUseException, GameRunningException,
            InvalidDisplayNameException, GameDoesntExistException {
        Player player1 = playerFactory.createPlayer("John", "1");
        Player player2 = playerFactory.createPlayer("Kate", "2");
        players.add(player1);
        players.add(player2);

        TestGame testGame = new TestGame(players);
        TestLobbyManager lm = new TestLobbyManager(testGame);

        assertTrue(lm.getGameReadOnly().getPlayers().contains(player1));
        assertTrue(lm.getGameReadOnly().getPlayers().contains(player2));

        AtomicReference<Boolean> hasFinished = new AtomicReference<>(false);

        DcOutputBoundary dcOutputBoundary = new DcOutputBoundary() {
            @Override
            public void hasDisconnected(DcOutputData data) {
                hasFinished.set(true);
            }

            @Override
            public void outputShutdownServer() {
                throw new RuntimeException("This method is not implemented and should not be called");
            }
        };
        dcInteractor = new DcInteractor(lm, register);

        DcInputData data = new DcInputData(player2.getPlayerId());
        dcInteractor.disconnect(data, dcOutputBoundary);

        while(!hasFinished.get()) {
            Thread.onSpinWait();
        }

        assertFalse(lm.getGameReadOnly().getPlayers().contains(player2));
        assertTrue(lm.getGameReadOnly().getPlayers().contains(player1));
    }

    /**
     * Testing disconnecting player who are in the pool
     */
    @Test
    @Timeout(1)
    public void testDisconnectPlayerFromPool() throws
            IdInUseException, GameRunningException,
            InvalidDisplayNameException {
        Player player3 = playerFactory.createPlayer("Nick", "3");
        Player player4 = playerFactory.createPlayer("Ann", "4");

        TestGame testGame = new TestGame(players);
        TestLobbyManager lm = new TestLobbyManager(testGame);

        lm.addPlayerToPool(player3, new BlankPoolListener());
        lm.addPlayerToPool(player4, new BlankPoolListener());

        assertTrue(lm.getPlayersFromPool().contains(player3));
        assertTrue(lm.getPlayersFromPool().contains(player4));

        AtomicReference<Boolean> hasFinished = new AtomicReference<>(false);

        DcOutputBoundary dcOutputBoundary = new DcOutputBoundary() {
            @Override
            public void hasDisconnected(DcOutputData data) {
                hasFinished.set(true);
            }

            @Override
            public void outputShutdownServer() {
                throw new RuntimeException("This method is not implemented and should not be called");
            }
        };
        dcInteractor = new DcInteractor(lm, register);

        DcInputData data = new DcInputData(player4.getPlayerId());
        dcInteractor.disconnect(data, dcOutputBoundary);

        while (!hasFinished.get()) {
            Thread.onSpinWait();
        }

        assertFalse(lm.getPlayersFromPool().contains(player4));
        assertTrue(lm.getPlayersFromPool().contains(player3));
    }

    /**
     * Test a malicious or bugged call to disconnect where a player
     * is NOT in the pool, and game is null. A fail code should be returned
     */
    @Test
    @Timeout(1)
    public void testGameNullPlayerNotInPool () throws
            IdInUseException, InvalidDisplayNameException, GameRunningException {
        Player player5 = playerFactory.createPlayer("Alby", "5");

        TestLobbyManager lm = new TestLobbyManager(null);

        AtomicBoolean hasResponded = new AtomicBoolean(false);
        AtomicReference<Response.ResCode> code = new AtomicReference<>();
        new DcOutputBoundary() {
            @Override
            public void hasDisconnected(DcOutputData data) {
                code.set(data.getResponse().getCode());
                hasResponded.set(true);
            }

            @Override
            public void outputShutdownServer() {
                throw new RuntimeException("This method is not implemented and should not be called");
            }
        };
        DcOutputBoundary dcOutputBoundary = new DcOutputBoundary() {
            @Override
            public void hasDisconnected(DcOutputData data) {
                code.set(data.getResponse().getCode());
                hasResponded.set(true);
            }

            @Override
            public void outputShutdownServer() {
                throw new RuntimeException("This method is not implemented and should not be called");
            }
        };
        dcInteractor = new DcInteractor(lm, register);

        DcInputData data = new DcInputData(player5.getPlayerId());
        dcInteractor.disconnect(data, dcOutputBoundary);

        while (!hasResponded.get()) {
            Thread.onSpinWait();
        }

        assertSame(Response.ResCode.GAME_DOESNT_EXIST, code.get());
    }

    /**
     * Test a call to disconnect a player who is neither in the game nor in the
     * pool. PLAYER_NOT_FOUND code should be returned.
     */
    @Test
    @Timeout(1)
    public void testPlayerNowhere () throws
            GameRunningException, IdInUseException, InvalidDisplayNameException {
        Player player6 = playerFactory.createPlayer("Sam", "6");

        TestGame g = new TestGame(players);
        TestLobbyManager lm = new TestLobbyManager(g);

        AtomicBoolean hasResponded = new AtomicBoolean(false);
        AtomicReference<Response.ResCode> code = new AtomicReference<>();
        DcOutputBoundary dcOutputBoundary = new DcOutputBoundary() {
            @Override
            public void hasDisconnected(DcOutputData data) {
                code.set(data.getResponse().getCode());
                hasResponded.set(true);
            }

            @Override
            public void outputShutdownServer() {
                throw new RuntimeException("This method is not implemented and should not be called");
            }
        };
        dcInteractor = new DcInteractor(lm, register);

        DcInputData data = new DcInputData(player6.getPlayerId());
        dcInteractor.disconnect(data, dcOutputBoundary);

        while (!hasResponded.get()) {
            Thread.onSpinWait();
        }

        assertSame(Response.ResCode.PLAYER_NOT_FOUND, code.get());
    }

    /**
     * Using descendant of LobbyManager to set the testGame
     */
    private static class TestLobbyManager extends LobbyManager {

        Game game;

        public TestLobbyManager(Game game) throws GameRunningException {
            super(playerFactory, new GameFactory(new PerPlayerIntStatistic[0]) {
                @Override
                public Game createGame(Map<String, Integer> settings, Collection<Player> initialPlayers) {
                    return null;
                }
            });
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

        private final Queue<Player> players;

        public TestGame(List<Player> players) {
            super(10, new TestValidityCheckerFacadeTrue());
            this.players = new LinkedList<>(players);
        }

        @Override
        public @NotNull Collection<Player> getPlayers() { return players; }

        @Override
        public boolean isGameOver() { return false; }

        @Override
        public void onTimerUpdateLogic() {}

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
        public boolean switchTurnLogic() {
            setSecondsLeftInCurrentTurn(getSecondsPerTurn());
            return players.add(players.remove());
        }

        @Override
        public @NotNull Player getCurrentTurnPlayer() { return players.peek(); }

    }

    private static class BlankPoolListener implements PlayerPoolListener {

        private final Lock lock = new ReentrantLock();
        @Override
        public void onJoinGamePlayer(Game game) {}

        @Override
        public void onCancelPlayer() {}

        @Override
        public Lock getLock() {
            return lock;
        }

    }

}
