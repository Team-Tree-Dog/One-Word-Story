package entities.games;

import entities.Player;
import entities.Story;
import entities.WordFactory;
import entities.statistics.AllPlayerNamesStatistic;
import entities.statistics.PerPlayerIntStatistic;
import entities.statistics.Statistic;
import entities.validity_checkers.ValidityCheckerFacade;
import exceptions.InvalidWordException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.RecursiveSymboledIntegerHashMap;

import java.util.*;

/**
 * An abstract game
 * Every type of game extends this class.
 * <br> <br>
 * <h2>Thread Safety:</h2>
 * <p>
 *     The game is not a thread safe object on its own. A game must
 *     only exist inside the LobbyManager which possesses a game lock.
 *     The lobby manager does not give direct access to the game object
 *     and instead contains pertinent wrappers for public use. It is
 *     LobbyManager's job to maintain thread safety with the game object.
 * </p>
 * <p>
 *     However, some code such as RG engages with the Game object directly.
 *     If this is the case, such code should also request the LobbyManger's
 *     game lock to be injected and should engage the lock for all method
 *     calls in Game, since Game is by default not thread safe
 * </p>
 */
public abstract class Game implements GameReadOnly {

    private final Story story;

    private final Timer gameTimer;

    private boolean timerStopped;

    private final int secondsPerTurn;
    private final Statistic<?>[] statistics;
    private final AllPlayerNamesStatistic authorNames;
    private final PerPlayerIntStatistic[] playerStatistics;
    protected int secondsLeftInCurrentTurn;

    /**
     * Constructor for a Game with statistics to track
     * @param secondsPerTurn The amount of seconds for each turn
     * @param v The validity checker (to check if a word is valid)
     * @param playerStatsToTrack Statistics to track during game for each player
     */
    public Game(int secondsPerTurn, ValidityCheckerFacade v,
                PerPlayerIntStatistic[] playerStatsToTrack) {
        this.story = new Story(new WordFactory(v));
        this.secondsPerTurn = secondsPerTurn;
        this.gameTimer = new Timer(true);
        this.timerStopped = false;
        playerStatistics = playerStatsToTrack;
        authorNames = new AllPlayerNamesStatistic();

        // ALL statistics go in the general stats list, so they could be tracked
        statistics = new Statistic<?>[playerStatistics.length + 1];
        System.arraycopy(playerStatsToTrack, 0, statistics, 0, playerStatsToTrack.length);
        statistics[statistics.length - 1] = authorNames;


    }

    /**
     * Constructor for a Game. No player statistics will be tracked
     * @param secondsPerTurn The amount of seconds for each turn
     * @param v The validity checker (to check if a word is valid)
     */
    public Game(int secondsPerTurn, ValidityCheckerFacade v) {
        this(secondsPerTurn, v, new PerPlayerIntStatistic[0]);
    }

    /**
     * @return Single string of the entire story in the game currently
     */
    @Override
    @NotNull
    public String getStoryString() {
        return story.toString();
    }

    /**
     * Thin wrapper to add a word to the story
     * @param word Word string to be added
     * @param author Player who submitted this word
     */
    public void addWord(@NotNull String word,
                        @NotNull Player author) throws InvalidWordException {
        story.addWord(word, author);
        // The above method throws an exception. Thus, the statistics
        // below are only notified if the above call succeeds

        for (Statistic<?> s: statistics) {
            s.onSubmitWord(word, author);
        }
    }

    /**
     * @return A list of statistics which track per player data to be displayed
     * in the game end screen
     */
    @Override
    @NotNull
    public PerPlayerIntStatistic[] getPlayerStatistics() {
        return playerStatistics;
    }

    /**
     * @return Special statistic which keeps track of the display names of all contributing
     * players
     */
    @Override
    @NotNull
    public AllPlayerNamesStatistic getAuthorNamesStatistic() {
        return authorNames;
    }

    /**
     * @return Returns how many seconds is given for every player per turn (therefore, every player gets the same
     * amount of time every turn)
     */
    public int getSecondsPerTurn() {return secondsPerTurn;}

    /**
     * @return Returns how many seconds are left for the current turn, or null if game timer not yet started
     */
    @Override
    public int getSecondsLeftInCurrentTurn() {return secondsLeftInCurrentTurn;}

    /**
     * Sets the seconds left in the current turn
     * @param newSeconds The amount of seconds to set the current turn for
     */
    public void setSecondsLeftInCurrentTurn(int newSeconds) {
        this.secondsLeftInCurrentTurn = newSeconds;
    }

    /**
     * This method adds the initial players to the game by looping and calling addPlayer
     */
    protected void addAllPlayers(Collection<Player> players) {
        for(Player player : players) {
            this.addPlayer(player);
        }
    }

    /**
     * @return Returns the game timer which is used by the use case layer
     */
    @NotNull
    public Timer getGameTimer() {return this.gameTimer;}

    /**
     * Called from the run game use case timer task to notify, after timer cancellation, when the last
     * execution of the run method has finished, meaning, no more game timer code will run
     */
    public void setTimerStopped() {this.timerStopped = true;}

    /**
     * See the implementation notes for isGameOver to see where this method
     * falls into the scheme of things
     * @return If RG has processed the fact that isGameOver is true and stopped the timer
     */
    public boolean isTimerStopped() {return timerStopped;}

    /**
     * @return a shallow copy of all the present players in the game
     */
    @NotNull
    public abstract Collection<Player> getPlayers();

    /**
     * <h3>Important Notes: </h3>
     * The general flow of use cases is as follows:
     * <ol>
     *  <li> RG catches lock. if isGameOver is true, it stops itself via setTimeStopped, so isGameEnded now
     * returns true </li>
     *  <li> SP catches lock. If isGameEnded, the game object in LobbyManager is set to null </li>
     *  <li> There is an opportunity for another use case to catch the lock in between, in which case
     * the game timer stopped but the game still exists </li>
     *  <li> There is also an opportunity for a use case to catch before RG, where isGameOver is true
     * but the time has not had a chance to be stopped yet    </li>
     * </ol>

     *
     * @return If the game's custom conditions evaluate that the game is over
     */
    public abstract boolean isGameOver();


    /**
     * Calls onTimerUpdateLogic and calls each statistic event handler
     */
    public void onTimerUpdate() {
        onTimerUpdateLogic();
        for (Statistic<?> s: statistics) {
            s.onTimerUpdate(this);
        }
    };

    /**
     * Custom additional actions that can be done by the game every time the timer is updated
     */
    protected abstract void onTimerUpdateLogic();

    /**
     * @param playerId ID of player you'd like to retrieve
     * @return the player object with the corresponding playerId, or null if not found
     */
    @Nullable
    public abstract Player getPlayerById(String playerId);

    /**
     * Removes the player specified from this GameRegular instance
     * @param playerToRemove The Player to be removed
     * @return if the player was successfully removed
     */
    public abstract boolean removePlayer(Player playerToRemove);

    /**
     * Adds new player to the game
     */
    public abstract boolean addPlayer(Player playerToAdd);

    /**
     * Switches this game's turn and resets the timer
     * @return if the turn was switched successfully
     */
    public boolean switchTurn() {
        boolean output = switchTurnLogic();

        // Notify statistics if turn was successfully switched
        if (output) {
            for (Statistic<?> s: statistics) {
                s.onSuccessfulSwitchTurn(
                        getCurrentTurnPlayer(), getSecondsLeftInCurrentTurn()
                );
            }
        }
        return output;
    }

    /**
     * Switches this game's turn and resets the timer
     * @return if the turn was switched successfully
     */
    protected abstract boolean switchTurnLogic();

    /**
     * @return Player whose turn it is currently in the game, or null if the game
     * has no players
     */
    @Nullable
    public abstract Player getCurrentTurnPlayer();

}
