package entities.games;

import entities.Player;
import entities.Story;
import entities.WordFactory;
import entities.validity_checkers.ValidityCheckerFacade;

import java.util.Collection;
import java.util.Timer;

/**
 * An abstract game
 * Every type of game extends this class
 */
public abstract class Game {

    private final Story story;

    private final Timer gameTimer;

    private boolean timerStopped;

    private final int secondsPerTurn;
    protected int secondsLeftInCurrentTurn;

    /**
     * Constructor for a Game
     * @param secondsPerTurn The amount of seconds for each turn
     * @param v The validity checker (to check if a word is valid)
     */
    public Game(int secondsPerTurn, ValidityCheckerFacade v) {
        this.story = new Story(new WordFactory(v));
        this.secondsPerTurn = secondsPerTurn;
        this.gameTimer = new Timer();
        this.timerStopped = false;
    }

    /**
     * @return The story as it has been typed in this game instance
     */
    public Story getStory() {return story;}

    /**
     * @return Returns how many seconds is given for every player per turn (therefore, every player gets the same
     * amount of time every turn)
     */
    public int getSecondsPerTurn() {return secondsPerTurn;}

    /**
     * @return Returns how many seconds are left for the current turn, or null if game timer not yet started
     */
    public int getSecondsLeftInCurrentTurn() {return secondsLeftInCurrentTurn;}

    /**
     * Sets the seconds left in the current turn
     * @param newSeconds The amount of seconds to set the current turn for
     */
    public void setSecondsLeftInCurrentTurn(int newSeconds) {
        this.secondsLeftInCurrentTurn = newSeconds;
    }


    /** This method adds the initial players to the game by looping and calling addPlayer
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
