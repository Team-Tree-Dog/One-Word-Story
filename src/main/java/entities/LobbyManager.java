package entities;

import entities.boundaries.GameEndedBoundary;
import entities.boundaries.OnTimerUpdateBoundary;
import exceptions.InvalidWordException;
import exceptions.PlayerNotFoundException;
import exceptions.GameDoesntExistException;
import exceptions.OutOfTurnException;
import exceptions.GameRunningException;
import entities.games.Game;
import entities.games.GameFactory;

import java.util.*;

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
        private final Player player;
        private final PlayerPoolListener playerPoolListener;

        public PlayerObserverLink (Player p, PlayerPoolListener o) {
            this.player = p;
            this.playerPoolListener = o;
        }

        public Player getPlayer() {
            return this.player;
        }

        public PlayerPoolListener getPlayerPoolListener () {
            return this.playerPoolListener;
        }
    }

    private final ArrayList<PlayerObserverLink> playerPool;
    private Game game;
    private final GameFactory gameFac;
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
    public boolean isGameRunning () { return !(game == null || game.isGameOver()); }

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
                        if (game.isGameOver()) {
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
                            game = gameFac.createGame(new HashMap<>(), initialPlayers);

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

    /**
     * Set the game attribute
     * @param game Game to be set for this lobby
     * @throws GameRunningException if game already exists
     */
    public void setGame (Game game) throws GameRunningException {
        if (!this.isGameNull()) {
            throw new GameRunningException(
                    "Trying to set an existing game");
        }
        this.game = game;
    }

    /**
     * Add word from the current-turn player to the story of our game
     * @param word String to add to the story
     * @param playerId String of the player who attempts to submit a word
     * @throws GameDoesntExistException if game does not exist
     * @throws PlayerNotFoundException if player cannot be found
     * @throws OutOfTurnException if this is not our player's turn
     * @throws InvalidWordException if the word is not valid
     */
    public void addWord (String word, String playerId) throws GameDoesntExistException, PlayerNotFoundException,
            OutOfTurnException, InvalidWordException {
        if (!this.isGameRunning()) {
            throw new GameDoesntExistException(
                    "The game you are trying to add word to does not exist");
        }
        if (this.game.getPlayerById(playerId) == null) {
            throw new PlayerNotFoundException(
                    "The player you are trying to add word is not found int the game");
        }
        if (!this.game.getCurrentTurnPlayer().getPlayerId().equals(playerId)) {
            throw new OutOfTurnException(
                    "Trying to submit a word out of turn");
        }
        Player author = this.game.getPlayerById(playerId);
        this.game.getStory().addWord(word, author);
    }

    /**
     * Create a game based on the provided settings and players from the pool
     * @param settings Map<String, Integer> String to add to the story
     */
    public Game newGameFromPool (Map<String, Integer> settings) {
        List<Player> initialPlayers = new ArrayList<>();
        for (PlayerObserverLink pol : this.playerPool) {
            initialPlayers.add(pol.getPlayer());
        }
        return this.gameFac.createGame(settings, initialPlayers);
    }
}
