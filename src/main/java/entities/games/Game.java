package entities.games;

import entities.Player;
import entities.Story;
import entities.ValidityChecker;
import entities.WordFactory;

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
     * @param initialPlayers The initial players in this Game
     * @param secondsPerTurn The amount of seconds for each turn
     * @param v The validity checker (to check if a word is valid)
     */
    public Game(Collection<Player> initialPlayers, int secondsPerTurn, ValidityChecker v) {
        this.story = new Story(new WordFactory(v));
        this.secondsPerTurn = secondsPerTurn;
        this.addInitialPlayers(initialPlayers);
        this.gameTimer = new Timer();
        this.timerStopped = false;
    }

    /**
     * @return The story as it has been typed in this game instance
     */
    public Story getStory() {return story;}

    public int getSecondsPerTurn() {return secondsPerTurn;}

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
    protected void addInitialPlayers(Collection<Player> players) {
        for(Player player : players) {
            this.addPlayer(player);
        }
    }

    public Timer getGameTimer() {return this.gameTimer;}

    public void setTimerStopped(boolean timerStopped) {this.timerStopped = timerStopped;}

    public boolean isTimerStopped() {return timerStopped;}

    public abstract Collection<Player> getPlayers();

    public abstract boolean isGameOver();

    protected abstract void onTimerUpdate();

    public abstract Player getPlayerById(String PlayerId);

    public abstract boolean removePlayer(Player playerToRemove);

    public abstract boolean addPlayer(Player playerToAdd);

    public abstract boolean switchTurn();

    public abstract void modifyTurnTime();

    public abstract Player getCurrentTurnPlayer();

}
