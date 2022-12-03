package usecases.disconnecting;

import entities.LobbyManager;
import entities.Player;
import entities.PlayerPoolListener;
import exceptions.GameDoesntExistException;
import exceptions.PlayerNotFoundException;
import usecases.InterruptibleThread;
import usecases.Response;
import usecases.ThreadRegister;

import java.util.concurrent.locks.Lock;

/**
 * Interactor for Disconnecting Use Case
 */
public class DcInteractor implements DcInputBoundary {
    private final LobbyManager lm;
    private final DcOutputBoundary dcOutputBoundary;
    private final Lock playerPoolLock;
    private final Lock gameLock;

    /**
     * The ThreadRegister that keeps track of all the running use case threads
     * for the shutdown-server use case
     */
    private final ThreadRegister register;

    /**
     * Constructor for DcInteractor
     * @param lm Lobby Manager
     * @param dcOutputBoundary DcOutputBoundary
     */
    public DcInteractor(LobbyManager lm, DcOutputBoundary dcOutputBoundary, ThreadRegister register) {
        this.lm = lm;
        this.dcOutputBoundary = dcOutputBoundary;
        this.playerPoolLock = lm.getPlayerPoolLock();
        this.gameLock = lm.getGameLock();
        this.register = register;
    }

    /**
     * Disconnects the user
     * @param data input data which contains playerId
     */
    @Override
    public void disconnect(DcInputData data) {
        // new Thread(new DcThread(data.getPlayerId())).start();
        InterruptibleThread dcThread = this.new DcThread(data.getPlayerId());
        if (!register.registerThread(dcThread)) {
            dcOutputBoundary.outputShutdownServer();
        }
    }

    /**
     * Thread for disconnecting the player
     */
    public class DcThread extends InterruptibleThread {
        private final String playerId;

        /**
         * Constructor for Disconnecting Thread
         * @param playerId ID of the player we need to disconnect
         */
        public DcThread(String playerId) {
            super(DcInteractor.this.register, DcInteractor.this.dcOutputBoundary);
            this.playerId = playerId;
        }

        @Override
        public void threadLogic() {
            // Player existence in both removeFromPoolCancel and removePlayerFromGame
            // is checked via Player.equals, which checks only the ID, thus we can
            // have an empty display name as a dummy
            Player playerToDisconnect = new Player("", playerId);

            // Innocent until proven guilty
            Response response = Response.getSuccessful("Disconnecting was successful.");

            playerPoolLock.lock();

            // Null if player not found, looks through pool hence above lock is needed
            LobbyManager.PlayerObserverLink playerLink = lm.getLinkFromPlayer(playerToDisconnect);

            PlayerPoolListener playerListener = null;

            try {
                // If player was not in pool, throw exception, run catch, and release pool in finally block
                if(playerLink == null) {
                    throw new PlayerNotFoundException("Player is not present in the pool");
                }
                playerListener = playerLink.getPlayerPoolListener();
                // Before we continue, we should lock the pool listener's lock
                playerListener.getLock().lock();

                // Try to cancel player from pool. Will throw PlayerNotFound
                // if player isn't in pool so no need to check contains explicitly
                lm.removeFromPoolCancel(playerToDisconnect);
            } catch (PlayerNotFoundException ignored) {
                gameLock.lock();
                try {
                    // In this catch block, we know player was not in the pool. However, we don't know if the player
                    // is in the game. We try to see if the player is in the game using .contains, which uses .equals,
                    // so only playerIDs are compared. If the game is null, GameDoesntExistException is thrown.
                    if (lm.getPlayersFromGame().contains(playerToDisconnect)){
                        // The player is in the game. We then check if it's the player's turn.
                        // If it is, then we switch the turn so play can continue.
                        if (lm.getCurrentTurnPlayer().getPlayerId().equals(this.playerId)) {
                            lm.switchTurn();
                        }
                        // Now try to remove player from game.
                        lm.removePlayerFromGame(playerToDisconnect);
                    }
                    else {
                        throw new PlayerNotFoundException("Player is not present in the pool or the game.");
                    }

                } catch (PlayerNotFoundException | GameDoesntExistException e) {
                    // In both PlayerNotFound & GameDoesntExist, player was
                    // not found to be in the game, so respond with fail
                    response = Response.fromException(e, "Player not found");
                    e.printStackTrace();
                } finally {
                    gameLock.unlock();
                }
            }
            finally {
                if(playerListener != null) {
                    playerListener.getLock().unlock();
                }
                playerPoolLock.unlock();
            }

            DcOutputData outputData = new DcOutputData(response, playerId);
            dcOutputBoundary.hasDisconnected(outputData);
        }
    }
}
