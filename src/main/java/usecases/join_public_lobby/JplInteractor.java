package usecases.join_public_lobby;

import entities.LobbyManager;
import entities.Player;
import entities.PlayerPoolListener;
import entities.games.Game;
import exceptions.EntityException;
import usecases.GameDTO;
import usecases.Response;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Core class of the Join Public Lobby use case
 * Given a player who would like to join a public lobby, adds this player
 * to the matchmaking waiting pool in the entities. The thread blocks until
 * the entity responds that the player was sorted into a game or that another
 * use case was triggered to cancel this player's waiting. In both cases, the thread
 * finishes execution and notifies the view model of the corresponding development
 */
public class JplInteractor implements JplInputBoundary {

    private final LobbyManager lobbyManager;
    private final JplOutputBoundary presenter;

    /**
     * Thread which executes the core logic of this use case
     */
    public class JplThread implements Runnable, PlayerPoolListener {

        private Game game;
        private boolean hasCancelled;
        private final JplInputData data;

        private final Lock lock;

        private final Condition conditionVariable;

        /**
         * @param data Data passed into this use case
         */
        public JplThread (JplInputData data) {
            this.data = data;
            hasCancelled = false;
            lock = new ReentrantLock();
            conditionVariable = lock.newCondition();
        }

        /**
         * Called from another thread, sets game attribute. The run method
         * reaches a point where is waits for either hasCancelled or game to be set
         * @param game The game that the player has been sorted into
         */
        @Override
        public void onJoinGamePlayer(Game game) {
            this.game = game;
            // There is always only one thread waiting for this signal
            conditionVariable.signal();
        }

        /**
         * Called from another thread, sets hasCancelled to true. The run method
         * reaches a point where is waits for either hasCancelled or game to be set
         */
        @Override
        public void onCancelPlayer() {
            hasCancelled = true;
            // There is always only one thread waiting for this signal
            conditionVariable.signal();
        }

        @Override
        public Lock getLock() {
            return lock;
        }

        /**
         * Core logic of the use case
         */
        @Override
        public void run() {
            try {
                // It is better to always lock the whole critical section (a useful rule of thumb)
                lock.lock();

                // Throws IdInUseException, code after runs if this line succeeded
                Player player = lobbyManager.createNewPlayer(data.getDisplayName(), data.getId());

                // Add player to matchmaking pool and subscribe to hear updates
                lobbyManager.addPlayerToPool(player, this);

                // Notifies presenter that player was successfully added to pool
                presenter.inPool(new JplOutputDataResponse(
                        Response.getSuccessful("Player with ID " + player.getPlayerId() + " added to pool."),
                        player.getPlayerId()
                ));

                // Block thread until an update is heard on the player in the pool
                while (game == null && !hasCancelled) {
                    // Makes this waiting loop less CPU expensive
                    conditionVariable.await();
                }

                if (game != null) {
                    presenter.inGame(new JplOutputDataJoinedGame(
                            Response.getSuccessful("Player successfully joined a game"),
                            player.getPlayerId(), GameDTO.fromGame(game)));

                }

                // Player has cancelled waiting
                else {
                    presenter.cancelled(new JplOutputDataResponse(
                            Response.getSuccessful("Player successfully cancelled their pool waiting"),
                            player.getPlayerId()));
                }

            } catch (EntityException | InterruptedException e) {
                // Notifies that adding player with given ID to pool has failed
                presenter.inPool(
                        new JplOutputDataResponse(
                                // Response object with IdInUseException response code
                                Response.fromException(e, e.getMessage()),
                                data.getId()));
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * @param lobbyManager Shared object representing game state
     * @param presenter Object to call for output
     */
    public JplInteractor (LobbyManager lobbyManager, JplOutputBoundary presenter) {
        this.lobbyManager = lobbyManager;
        this.presenter = presenter;
    }

    /**
     * Thin wrapper to start a thread which performs this use case
     * @param data Describes player who wishes to join a public lobby
     */
    @Override
    public void joinPublicLobby(JplInputData data) {
        (new Thread(new JplThread(data))).start();
    }
}
