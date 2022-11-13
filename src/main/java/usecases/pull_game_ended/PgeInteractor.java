package usecases.pull_game_ended;

/**
 * Interactor for the Pull Game Ended Data use case
 */
public class PgeInteractor implements PgeInputBoundary {

    public PgeOutputBoundary presenter;

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
        int numberOfPlayers = data.players.size();
        String[] playerIds = new String[numberOfPlayers];

        for (int i = 0; i < numberOfPlayers; i++) {
            playerIds[i] = data.players.get(i).getPlayerId();
        }

        PgeOutputData idsToBeNotified = new PgeOutputData(playerIds);
        presenter.notifyGameEnded(idsToBeNotified);
    }
}
