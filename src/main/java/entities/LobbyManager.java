package entities;

import entities.boundaries.GameEndedBoundary;
import entities.boundaries.OnTimerUpdateBoundary;
import entities.exceptions.PlayerNotFoundException;
import entities.games.Game;
import entities.games.GameFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Core entity which keeps track of all the games which are running
 * Every use case has access to an instance of this shared gamestate
 */
public class LobbyManager {

    public static int SORT_PLAYERS_TIMER_PERIOD_MS = 500;

    /**
     * Pairs Player objects with the corresponding Listener (join public lobby thread)
     * which is waiting for the player to either be sorted into a game
     * or cancel their waiting
     */
    private static class PlayerObserverLink {
        public Player player;
        public PlayerPoolListener playerPoolListener;

        public PlayerObserverLink (Player p, PlayerPoolListener o) {
            this.player = p;
            this.playerPoolListener = o;
        }
    }

    private final ArrayList<PlayerObserverLink> playerPool;
    private Game game;
    private final GameFactory gameFac;
    private final OnTimerUpdateBoundary onTimerUpdateBoundary;
    private final GameEndedBoundary gameEndedBoundary;
    private final Timer sortPlayersTimer;
    private boolean startedSortTimer;

    public LobbyManager (OnTimerUpdateBoundary otub, GameEndedBoundary geb, GameFactory gameFac) {
        this.onTimerUpdateBoundary = otub;
        this.gameEndedBoundary = geb;
        this.gameFac = gameFac;

        this.playerPool = new ArrayList<>();
        this.sortPlayersTimer = new Timer();
        this.startedSortTimer = false;
    }

    /**
     * @return If a game has been started but has not yet ended
     */
    public boolean isGameRunning () { return !(game == null || game.isGameEnded()); }

    /**
     * @return If the inner sort players timer thread has been started
     */
    public boolean isStartedSortTimer() { return startedSortTimer; }

    /**
     * Initiates and runs the internal timer which is in charge of sorting players
     * into lobbies (or the single game instance) and creating the game object/s
     */
    public void startSortPlayers () {
        // Ensures timer cant be started twice
        if (!startedSortTimer) {
            startedSortTimer = true;

            sortPlayersTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    // Keeps track of which players need to be removed from pool after
                    // this iteration, either due to cancellation or joining a game
                    ArrayList<PlayerObserverLink> linksToRemove = new ArrayList<>();

                    // Game object exists
                    if (game != null) {

                        // Game has just ended and terminated its internal timer
                        if (game.isGameEnded()) {
                            game = null;
                        }

                        // Game is running
                        else {
                            for (PlayerObserverLink pol: playerPool) {
                                // Tries to add player to existing game
                                boolean isPlayerAdded = game.addPlayer(pol.player);

                                // If success call the listener and set this link for removal
                                if (isPlayerAdded) {
                                    pol.playerPoolListener.onJoinGamePlayer(game);
                                    linksToRemove.add(pol);
                                }
                            }
                        }
                    }
                    // Game object is null
                    else {
                        // Minimum 2 players to start a game
                        if (playerPool.size() >= 2) {
                            // Extracts players from links and sets links for removal
                            ArrayList<Player> initialPlayers = new ArrayList<>();
                            for (PlayerObserverLink link: playerPool) {
                                initialPlayers.add(link.player);
                                linksToRemove.add(link);
                            }

                            // Creates game with initial players from pool
                            game = gameFac.createGame(new HashMap<>(), initialPlayers,
                                    onTimerUpdateBoundary, gameEndedBoundary);

                            // Calls listeners
                            for (PlayerObserverLink link: playerPool) {
                                link.playerPoolListener.onJoinGamePlayer(game);
                            }
                        }
                    }

                    // Removes the links that were set for removal
                    for (PlayerObserverLink pol: linksToRemove) {
                        playerPool.remove(pol);
                    }
                }
            }, 0, SORT_PLAYERS_TIMER_PERIOD_MS);
        }
    }

    /**
     * Link the player to the observer/listener and add them to the pool
     * @param p Player to add to the pool
     * @param observer Callbacks to call if player joins a game or cancels
     */
    public void addPlayer (Player p, PlayerPoolListener observer) {
        // TODO: This method should lock player pool
        playerPool.add(new PlayerObserverLink(p, observer));
    }

    /**
     * Remove the given player from the pool and notify the corresponding listener
     * @param p Player to remove from pool
     * @throws PlayerNotFoundException if p was not in the pool
     */
    public void cancelPlayer (Player p) throws PlayerNotFoundException {
        // TODO: This method should probably lock the playerPool throughout the entire algorithm
        PlayerObserverLink link = null;

        // Looks for the player-observer link with player p
        for (PlayerObserverLink pol : playerPool) {
            if (pol.player.equals(p)) {
                // Calls the cancel callback
                pol.playerPoolListener.onCancelPlayer();
                // Sets the found link
                link = pol;
                break;
            }
        }

        // If link was never set, means we are cancelling a player who wasn't in the pool
        // Throw exception in this xase
        if (link == null) {
            throw new PlayerNotFoundException(
                    "The player you are trying to cancel is not in the pool");
        } else {
            // Remove this play from the pool
            playerPool.remove(link);
        }
    }
}
