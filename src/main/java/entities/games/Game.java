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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;

/**
 * An abstract game
 * Every type of game extends this class
 */
public abstract class Game implements GameReadOnly {

    private final Story story;

    private final Timer gameTimer;

    private boolean timerStopped;

    private final int secondsPerTurn;
    private final List<Statistic> statistics;
    private final AllPlayerNamesStatistic authorNames;
    private final List<PerPlayerIntStatistic> playerStatistics;
    protected int secondsLeftInCurrentTurn;

    /**
     * Constructor for a Game with statistics to track
     * @param secondsPerTurn The amount of seconds for each turn
     * @param v The validity checker (to check if a word is valid)
     * @param playerStatsToTrack Statistics to track during game for each player
     */
    public Game(int secondsPerTurn, ValidityCheckerFacade v,
                List<PerPlayerIntStatistic> playerStatsToTrack) {
        this.story = new Story(new WordFactory(v));
        this.secondsPerTurn = secondsPerTurn;
        this.gameTimer = new Timer(true);
        this.timerStopped = false;
        this.playerStatistics = playerStatsToTrack;

        // ALL statistics go in the general stats list, so they could be tracked
        statistics = new ArrayList<>();
        statistics.addAll(playerStatistics);

        authorNames = new AllPlayerNamesStatistic();
    }

    /**
     * Constructor for a Game. No player statistics will be tracked
     * @param secondsPerTurn The amount of seconds for each turn
     * @param v The validity checker (to check if a word is valid)
     */
    public Game(int secondsPerTurn, ValidityCheckerFacade v) {
        this(secondsPerTurn, v, new ArrayList<>());
    }

    /**
     * <br> <br>
     * <b><u>THREAD SAFETY:</u></b> Not safe; Does not engage any locks
     * @return Single string of the entire story in the game currently
     */
    @Override
    public @NotNull String getStoryString() {
        return story.toString();
    }

    /**
     * Thin wrapper to add a word to the story
     * <br> <br>
     * <b><u>THREAD SAFETY:</u></b> Not safe; Does not engage any locks
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
     * <br> <br>
     * <b><u>THREAD SAFETY:</u></b> Not safe; Does not engage any locks
     * @return Returns how many seconds is given for every player per turn (therefore, every player gets the same
     * amount of time every turn)
     */
    public int getSecondsPerTurn() {return secondsPerTurn;}

    /**
     * <br> <br>
     * <b><u>THREAD SAFETY:</u></b> Not safe; Does not engage any locks
     * @return Returns how many seconds are left for the current turn, or null if game timer not yet started
     */
    public int getSecondsLeftInCurrentTurn() {return secondsLeftInCurrentTurn;}

    /**
     * Sets the seconds left in the current turn
     * <br> <br>
     * <b><u>THREAD SAFETY:</u></b> Not safe; Does not engage any locks
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
    public Timer getGameTimer() {return this.gameTimer;}

    /**
     * Called from the run game use case timer task to notify, after timer cancellation, when the last
     * execution of the run method has finished, meaning, no more game timer code will run
     */
    public void setTimerStopped() {this.timerStopped = true;}

    /**
     * @return Returns whether timer has been stopped
     */
    public boolean isTimerStopped() {return timerStopped;}

    /**
     * @return Returns all the present players in the game
     */
    public abstract Collection<Player> getPlayers();

    /**
     * @return Returns whether the game is over
     */
    public abstract boolean isGameOver();

    /**
     * Additional actions that can be done by the game every time the timer is updated
     */
    public abstract void onTimerUpdate();

    /**
     * @return the player object with the corresponding playerId, or null if not found
     */
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
     */
    public abstract boolean switchTurn();

    /**
     * Returns the player whose turn it is
     */
    public abstract Player getCurrentTurnPlayer();

}
