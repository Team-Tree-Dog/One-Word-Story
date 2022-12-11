package usecases.join_public_lobby;

import entities.LobbyManager;
import entities.Player;
import entities.PlayerPoolListener;
import entities.games.Game;
import exceptions.EntityException;
import org.example.Log;
import usecases.GameDTO;
import usecases.InterruptibleThread;
import usecases.Response;
import usecases.ThreadRegister;

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

    private final Lock gameLock;

    /**
     * The ThreadRegister that keeps track of all the running use case threads
     * for the shutdown-server use case
     */
    private final ThreadRegister register;

    /**
     * Thread which executes the core logic of this use case
     */
    public class JplThread extends InterruptibleThread implements PlayerPoolListener {

        private volatile Game game;
        private volatile boolean hasCancelled;
        private final JplInputData data;
        private final JplOutputBoundary pres;
        private final Lock lock;

        private final Condition conditionVariable;

        /**
         * @param data Data passed into this use case
         * @param pres output boundary for this use case
         */
        public JplThread (JplInputData data, JplOutputBoundary pres) {
            super(JplInteractor.this.register, pres);
            this.data = data;
            this.pres = pres;
            hasCancelled = false;
            lock = new ReentrantLock(true);
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
        public void threadLogic() throws InterruptedException {
            try {
                // It is better to always lock the whole critical section (a useful rule of thumb)
                Log.useCaseMsg("JPL", "Wants JPL lock " + data.getId());
                lock.lock();
                Log.useCaseMsg("JPL", "Got JPL lock " + data.getId());

                // Throws IdInUseException, code after runs if this line succeeded
                Player player = lobbyManager.createNewPlayer(data.getDisplayName(), data.getId());

                // Add player to matchmaking pool and subscribe to hear updates
                Log.useCaseMsg("JPL", "Wants POOL");
                lobbyManager.addPlayerToPool(player, this);
                Log.useCaseMsg("JPL", "Got and Released POOL");

                // Notifies presenter that player was successfully added to pool
                pres.inPool(new JplOutputDataResponse(
                        Response.getSuccessful("Player with ID " + player.getPlayerId() + " added to pool."),
                        player.getPlayerId()
                ));

                Log.useCaseMsg("JPL", "Awaiting Signal...");
                // Block thread until an update is heard on the player in the pool
                while (game == null && !hasCancelled) {
                    // Makes this waiting loop less CPU expensive
                    conditionVariable.await();
                }
                Log.useCaseMsg("JPL", "Got Signal!");

                if (game != null) {
                    Log.useCaseMsg("JPL", "Wants GAME lock");
                    JplInteractor.this.gameLock.lock();
                    Log.useCaseMsg("JPL", "Got GAME lock");
                    GameDTO gameState = GameDTO.fromGame(game);
                    JplInteractor.this.gameLock.unlock();
                    Log.useCaseMsg("JPL", "Released GAME lock");

                    pres.inGame(new JplOutputDataJoinedGame(
                            Response.getSuccessful("Player successfully joined a game"),
                            player.getPlayerId(), gameState));
                }

                // Player has cancelled waiting
                else {
                    pres.cancelled(new JplOutputDataResponse(
                            Response.getSuccessful("Player successfully cancelled their pool waiting"),
                            player.getPlayerId()));
                }

            } catch (EntityException e) {
                // Notifies that adding player with given ID to pool has failed
                pres.inPool(
                        new JplOutputDataResponse(
                                // Response object with IdInUseException response code
                                Response.fromException(e, e.getMessage()),
                                data.getId()));
            } finally {
                lock.unlock();
                Log.useCaseMsg("JPL", "Released JPL lock " + data.getId());
            }
        }
    }

    /**
     * @param lobbyManager Shared object representing game state
     */
    public JplInteractor (LobbyManager lobbyManager, ThreadRegister register) {
        this.lobbyManager = lobbyManager;
        this.gameLock = lobbyManager.getGameLock();
        this.register = register;
    }

    /**
     * Thin wrapper to start a thread which performs this use case
     * @param data Describes player who wishes to join a public lobby
     * @param pres output boundary for this use case
     */
    @Override
    public void joinPublicLobby(JplInputData data, JplOutputBoundary pres) {
        InterruptibleThread thread = new JplThread(data, pres);
        if (!register.registerThread(thread)) {
            pres.outputShutdownServer();
        }
    }
}
