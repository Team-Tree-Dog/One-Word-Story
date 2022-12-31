package usecases.disconnecting;

import entities.LobbyManager;
import entities.Player;
import entities.PlayerPoolListener;
import exceptions.GameDoesntExistException;
import exceptions.PlayerNotFoundException;
import org.example.ANSI;
import org.example.Log;
import usecases.GameDTO;
import usecases.InterruptibleThread;
import usecases.Response;
import usecases.ThreadRegister;

import java.util.concurrent.locks.Lock;

/**
 * Interactor for Disconnecting Use Case
 */
public class DcInteractor implements DcInputBoundary {
    private final LobbyManager lm;
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
     */
    public DcInteractor(LobbyManager lm, ThreadRegister register) {
        this.lm = lm;
        this.playerPoolLock = lm.getPlayerPoolLock();
        this.gameLock = lm.getGameLock();
        this.register = register;
    }

    /**
     * Disconnects the user
     * @param data input data which contains playerId
     * @param pres output boundary for this use case
     */
    @Override
    public void disconnect(DcInputData data, DcOutputBoundary pres) {
        InterruptibleThread dcThread = this.new DcThread(data.getPlayerId(), pres);
        if (!register.registerThread(dcThread)) {
            pres.outputShutdownServer();
        }
    }

    /**
     * Thread for disconnecting the player
     */
    public class DcThread extends InterruptibleThread {
        private final String playerId;

        private final DcOutputBoundary pres;

        /**
         * Constructor for Disconnecting Thread
         * @param playerId ID of the player we need to disconnect
         * @param pres output boundary for this use case
         */
        public DcThread(String playerId, DcOutputBoundary pres) {
            super(DcInteractor.this.register, pres);
            this.playerId = playerId;
            this.pres = pres;
        }

        @Override
        public void threadLogic() {
            // Player existence in both removeFromPoolCancel and removePlayerFromGame
            // is checked via Player.equals, which checks only the ID, thus we can
            // have an empty display name as a dummy
            Player playerToDisconnect = new Player("", playerId);

            // Innocent until proven guilty
            Response response = Response.getSuccessful(playerToDisconnect + " disconnected successfully!");

            // Gets set if player was removed from game
            GameDTO gameData = null;

            Log.useCaseMsg("DC", "Wants POOL lock");
            playerPoolLock.lock();
            Log.useCaseMsg("DC", "Got POOL lock");

            // Null if player not found, looks through pool hence above lock is needed
            LobbyManager.PlayerObserverLink playerLink = lm.getLinkFromPlayer(playerToDisconnect);

            PlayerPoolListener playerListener = null;

            try {
                // If player was not in pool, throw exception, run catch, and release pool in finally block
                if(playerLink == null) {
                    throw new PlayerNotFoundException(playerToDisconnect + " is not present in the pool");
                }
                Log.useCaseMsg("DC", "Found in pool PLY " + playerId);
                Log.useCaseMsg("DC", "Wants JPL lock PlyID " + playerId);
                playerListener = playerLink.getPlayerPoolListener();
                // Before we continue, we should lock the pool listener's lock
                playerListener.getLock().lock();
                Log.useCaseMsg("DC", "Got JPL lock PlyID " + playerId);

                // Try to cancel player from pool. Will throw PlayerNotFound
                // if player isn't in pool so no need to check contains explicitly
                lm.removeFromPoolCancel(playerToDisconnect);
            } catch (PlayerNotFoundException ignored) {
                Log.useCaseMsg("DC", "Wants GAME lock");
                gameLock.lock();
                Log.useCaseMsg("DC", "Got GAME lock");
                try {
                    // In this catch block, we know player was not in the pool. However, we don't know if the player
                    // is in the game. We try to see if the player is in the game using .contains, which uses .equals,
                    // so only playerIDs are compared. If the game is null, GameDoesntExistException is thrown.
                    if (lm.getGameReadOnly().getPlayers().contains(playerToDisconnect)){

                        // If the game is ended but not yet set to null, the player is effectively not there
                        // anymore, so we could consider this a PlayerNotFound scenario
                        if (lm.isGameEnded()) {
                            throw new PlayerNotFoundException("Player was found but game is ended and will soon" +
                                    "be set to null, so player is technically not there");
                        }

                        // The player is in the game. We then check if it's the player's turn.
                        // If it is, then we switch the turn so play can continue.
                        if (lm.getGameReadOnly().getCurrentTurnPlayer().getPlayerId().equals(this.playerId)) {
                            // Switch turn returns a boolean of whether switch turn succeeded.
                            // In this case, it should succeed! If game makes it fail for whatever reason
                            // then we have an issue. TODO: Perhaps switch turn should not be allowed to fail
                            lm.switchTurn();
                        }

                        // Now try to remove player from game.
                        lm.removePlayerFromGame(playerToDisconnect);

                        // We create GameDTO since player was likely removed from game
                        gameData = GameDTO.fromGame(lm.getGameReadOnly());
                    }
                    else {
                        throw new PlayerNotFoundException("Player is not present in the pool or the game.");
                    }

                } catch (PlayerNotFoundException | GameDoesntExistException e) {
                    // In both PlayerNotFound & GameDoesntExist, player was
                    // not found to be in the game, so respond with fail
                    response = Response.fromException(e, playerToDisconnect + " not found!");
                } finally {
                    gameLock.unlock();
                    Log.useCaseMsg("DC", "Released GAME lock");
                }
            }
            finally {
                if(playerListener != null) {
                    playerListener.getLock().unlock();
                    Log.useCaseMsg("DC", "Released JPL lock PlyID " + playerId);
                }
                playerPoolLock.unlock();
                Log.useCaseMsg("DC", "Released POOL lock");
            }

            DcOutputData outputData = new DcOutputData(response, playerId, gameData);
            pres.hasDisconnected(outputData);
        }
    }
}
