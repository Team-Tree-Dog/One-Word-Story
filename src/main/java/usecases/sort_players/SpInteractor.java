package usecases.sort_players;

import entities.LobbyManager;
import entities.Player;
import entities.games.Game;
import exceptions.GameDoesntExistException;
import exceptions.GameRunningException;
import exceptions.PlayerNotFoundException;
import usecases.pull_data.PdInputBoundary;
import usecases.pull_game_ended.PgeInputBoundary;
import usecases.run_game.RgInteractor;

import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;

/**
 * Interactor for Sort Players use case
 */
public class SpInteractor {
    private final LobbyManager lobbyManager;
    private final PgeInputBoundary pge;
    private final PdInputBoundary pd;
    private final Lock playerPoolLock;
    private final Lock gameLock;

    /**
     * Constructor for SpInteractor
     * @param lobbyManager the lobby manager players are being sorted from
     * @param pge pull game ended use case input boundary
     * @param pd pull data use case input boundary4
     */
    public SpInteractor(LobbyManager lobbyManager, PgeInputBoundary pge,
                         PdInputBoundary pd) {
        this.lobbyManager = lobbyManager;
        this.pge = pge;
        this.pd = pd;
        this.playerPoolLock = lobbyManager.getPlayerPoolLock();
        this.gameLock = lobbyManager.getGameLock();
    }

    /**
     * Sort players task for timer to perform.
     * If the game exists;
     * If the game is over sets the game to null.
     * If the game is ongoing try to add players in the pool to the game.
     * If the game doesn't exist and there are 2 or more players waiting;
     * Creates a new game, adds waiting players in pool to game, and starts the run game timer.
     */
    public class SpTask extends TimerTask {
        @Override
        public void run() {
            // We need to lock all the accesses to the pool and the game to avoid race conditions
            //System.out.println("SP: Wants POOL and GAME locks");
            playerPoolLock.lock();
            gameLock.lock();
            //System.out.println("SP: Got POOL and GAME locks");

            // If game has ended, set it to null.
            if (!lobbyManager.isGameNull()) {
                //System.out.println("SP: Detected isGameNull = false");
                if (lobbyManager.isGameEnded()) {
                    System.out.println("SP: Detected isGameEnded = true");

                    // if isGameEnded() is true, GameRunningException cannot be thrown
                    // No other thread currently changes isGameEnded() state so this error is IMPOSSIBLE
                    try {
                        lobbyManager.setGameNull();
                        System.out.println("SP: Set game to null successfully");
                    } catch (GameRunningException e) {
                        gameLock.unlock();
                        playerPoolLock.unlock();
                        System.out.println("SP: Released POOL and GAME locks");
                        System.out.println("SP: IMPOSSIBLE ERROR: Tried to set game null when game was running");
                        throw new RuntimeException(e);
                    }

                } else {
                    System.out.println("SP: Detected isGameEnded = false");
                    for (LobbyManager.PlayerObserverLink playerObserverLink : lobbyManager.getPool()) {
                        Player player = playerObserverLink.getPlayer();

                        System.out.println("SP: Processing player with ID: " + player.getPlayerId() +
                                " NAME: " + player.getDisplayName());

                        System.out.println("SP: Wants JPL lock for Ply ID: " + player.getPlayerId());
                        Lock lock = playerObserverLink.getPlayerPoolListener().getLock();
                        lock.lock();
                        System.out.println("SP: Got JPL lock for Ply ID: " + player.getPlayerId());
                        try {
                            lobbyManager.addPlayerToGameRemoveFromPool(player);
                            System.out.println("SP: Successfully added player to game and removed from pool ID: " + player.getPlayerId());
                        } catch (PlayerNotFoundException | GameDoesntExistException e) {
                            // GameDoesntExist is an IMPOSSIBLE Error. In this if block, game is not null and
                            // only SortPlayers sets game to null.
                            // PlayerNotFoundException occurs if player is removed from the pool from another
                            // thread. Proper lock architecture will prevent this
                            System.out.println("SP: IMPOSSIBLE ERROR: " + e.getClass().getSimpleName());
                            gameLock.unlock();
                            playerPoolLock.unlock();
                            System.out.println("SP: Released POOL and GAME locks");
                            throw new RuntimeException(e);
                        } finally {
                            lock.unlock();
                            System.out.println("SP: Released JPL lock for Ply ID: " + player.getPlayerId());
                        }
                    }
                }
            }
            else if (lobbyManager.getPool().size() >= LobbyManager.PLAYERS_TO_START_GAME) {
                System.out.println("SP: Detected isGameNull = true AND >= 2 players in pool");
                System.out.println("SP: Detected Pool Length: " + lobbyManager.getPool().size());

                Map<String, Integer> settings = null; // currently player settings isn't a feature, thus null
                Game game = lobbyManager.newGameFromPool(settings);

                // IMPOSSIBLE error. isGameNull is true in this block, and setGame is only
                // called from this thread, so another thread couldn't have changed it
                try {
                    lobbyManager.setGame(game);
                } catch (GameRunningException e) {
                    gameLock.unlock();
                    playerPoolLock.unlock();
                    throw new RuntimeException(e);
                }

                // This method has built in thread safety which ensures that each
                // PlayerPoolListener lock is engaged during callback execution
                lobbyManager.removeAllFromPoolJoin();
                new RgInteractor(game, pge, pd, gameLock).startTimer();
            }
            gameLock.unlock();
            playerPoolLock.unlock();
            //System.out.println("SP: Released POOL and GAME locks");
        }
    }

    /**
     * Starts the sort players timer with the sort players task
     */
    public void startTimer() {
        lobbyManager.getSortPlayersTimer().scheduleAtFixedRate(new SpTask(), 0, 500);
    }
}
