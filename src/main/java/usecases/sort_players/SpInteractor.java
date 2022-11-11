package usecases.sort_players;

import entities.LobbyManager;
import entities.Player;
import entities.games.Game;
import exceptions.GameDoesntExistException;
import exceptions.GameRunningException;
import exceptions.PlayerNotFoundException;
import usecases.pull_data.PdInputBoundary;

import java.util.Map;
import java.util.TimerTask;

/**
 * Interactor for Sort Players use case
 */
public class SpInteractor {
    private final LobbyManager lobbyManager;
    private final PgeInputBoundary pge;
    private final PdInputBoundary pd;

    /**
     * Constructor for SpInteractor
     * @param lobbyManager the lobby manager players are being sorted from
     * @param pge pull game ended use case input boundary
     * @param pd pull data use case input boundary
     */
    public SpInteractor(LobbyManager lobbyManager, PgeInputBoundary pge,
                         PdInputBoundary pd) {
        this.lobbyManager = lobbyManager;
        this.pge = pge;
        this.pd = pd;
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
            if (!lobbyManager.isGameNull()) {
                if (lobbyManager.isGameEnded()) {
                    try {
                        lobbyManager.setGameNull();
                    } catch (GameRunningException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    for (LobbyManager.PlayerObserverLink playerObserverLink : lobbyManager.getPool()) {
                        Player player = playerObserverLink.getPlayer();
                        try {
                            lobbyManager.addPlayerToGame(player);
                        } catch (GameDoesntExistException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            lobbyManager.removeFromPoolJoin(player);
                        } catch (PlayerNotFoundException | GameDoesntExistException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            if (lobbyManager.isGameNull() && lobbyManager.getPool().size() > 2) {
                Map<String, Integer> settings = null; // currently player settings isn't a feature, thus null
                Game game = lobbyManager.newGameFromPool(settings);
                try {
                    lobbyManager.setGame(game);
                } catch (GameRunningException e) {
                    throw new RuntimeException(e);
                }
                lobbyManager.removeAllFromPoolJoin();
                new RgInteractor(game, pge, pd).startTimer();
            }
        }
    }

    /**
     * Starts the sort players timer with the sort players task
     */
    public void startTimer() {
        lobbyManager.getSortPlayersTimer().schedule(new SpTask(), 0);
    }
}
