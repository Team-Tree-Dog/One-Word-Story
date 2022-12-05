package usecases.pull_game_ended;

import entities.Player;

import java.util.List;

/**
 * Interactor for the Pull Game Ended Data use case
 */
public class PgeInteractor implements PgeInputBoundary {

    private final PgeOutputBoundary presenter;

    /**
     * Constructor for PgeInteractor
     * @param presenter Object to call for output
     */
    public PgeInteractor(PgeOutputBoundary presenter) {
        this.presenter = presenter;
    }

    /**
     * Notifies presenter of player ids that need to be notified that the game has ended
     * FUTURE: Updates database about something
     * @param data game statistics, including players in the game that ended
     */
    @Override
    public void onGameEnded(PgeInputData data) {
        List<Player> players = data.getPlayers();
        int numberOfPlayers = players.size();
        String[] playerIds = new String[numberOfPlayers];
        for (int i = 0; i < numberOfPlayers; i++) {
            playerIds[i] = players.get(i).getPlayerId();
        }
        presenter.notifyGameEnded(new PgeOutputData(playerIds));
        System.out.println("PGE has ended.");
    }
}
