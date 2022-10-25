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

public abstract class Game {
    private Timer gameTimer;
    private OnTimerUpdateBoundary onTimerBound;
    private GameEndedBoundary gameEndBound;
    private Story story;
    private int secondsPerTurn;
    private int secondsLeftInCurrentTurn;
    private boolean gameEnded;

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

    public Story getStory() {return story;}
    public OnTimerUpdateBoundary getOnTimerBound() {return onTimerBound;}
    public GameEndedBoundary getGameEndBound() {return gameEndBound;}
    public int getSecondsPerTurn() {return secondsPerTurn;}
    public int getSecondsLeftInCurrentTurn() {return secondsLeftInCurrentTurn;}
    public void setSecondsLeftInCurrentTurn(int newSeconds) {
        secondsLeftInCurrentTurn = newSeconds;
    }
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
