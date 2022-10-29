package entities.games;

import entities.Player;
import entities.Story;
import entities.ValidityChecker;
import entities.WordFactory;
import entities.boundaries.GameEndedBoundary;
import entities.boundaries.OnTimerUpdateBoundary;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * An abstract game
 * Every type of game extends this class
 */
public abstract class Game {
    private Timer gameTimer;
    private OnTimerUpdateBoundary onTimerBound;
    private GameEndedBoundary gameEndBound;
    private Story story;
    private int secondsPerTurn;
    private int secondsLeftInCurrentTurn;
    private boolean gameEnded;

    /**
     * Constructor for a Game
     * @param initialPlayers The initial players in this Game
     * @param otub The timer update boundary
     * @param geb The game ended boundary
     * @param secondsPerTurn The amount of seconds for each turn
     * @param v The validity checker (to check if a word is valid)
     */
    public Game(List<Player> initialPlayers, OnTimerUpdateBoundary otub,
                GameEndedBoundary geb, int secondsPerTurn, ValidityChecker v) {
        this.onTimerBound = otub;
        this.gameEndBound = geb;
        this.story = new Story(new WordFactory(v));
        this.secondsPerTurn = secondsPerTurn;
        this.gameTimer = new Timer();
        for (Player p : initialPlayers) {
            this.addPlayer(p);
        }
    }

    /**
     * Starts the game's timer
     * Tracks how many seconds are left in the current turn, and switches turns when time has run out
     * If the game is over, the timer stops
     */
    protected void startTimer() {
        this.secondsLeftInCurrentTurn = secondsPerTurn;
        gameTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isGameOver()) {
                    gameTimer.cancel();
                    gameEnded = true;
                    //TODO: bounds (unfinished)
                } else {
                    modifyTurnTime();
                    if (secondsLeftInCurrentTurn == 0) {
                        switchTurn();
                        secondsLeftInCurrentTurn = secondsPerTurn;
                    }
                }
            }
        }, 0, 500);
    }

    /**
     * @return The story as it has been typed in this game instance
     */
    public Story getStory() {return story;}

    public OnTimerUpdateBoundary getOnTimerBound() {return onTimerBound;}

    public GameEndedBoundary getGameEndBound() {return gameEndBound;}

    public int getSecondsPerTurn() {return secondsPerTurn;}

    public int getSecondsLeftInCurrentTurn() {return secondsLeftInCurrentTurn;}

    /**
     * Sets the seconds left in the current turn
     * @param newSeconds The amount of seconds to set the current turn for
     */
    public void setSecondsLeftInCurrentTurn(int newSeconds) {
        secondsLeftInCurrentTurn = newSeconds;
    }

    /**
     * @return if the game has ended
     */
    public boolean isGameEnded() {return gameEnded;}

    public abstract List<Player> getPlayers();

    protected abstract void onTimerUpdate();

    public abstract Player getPlayerById(String PlayerId);

    public abstract boolean removePlayer(Player playerToRemove);

    public abstract boolean addPlayer(Player playerToAdd);

    public abstract boolean switchTurn();

    public abstract void modifyTurnTime();

    public abstract Player getCurrentTurnPlayer();

    protected abstract boolean isGameOver();
}
