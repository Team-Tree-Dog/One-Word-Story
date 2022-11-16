package usecases.disconnecting;

import entities.LobbyManager;
import entities.Player;
import exceptions.GameDoesntExistException;
import exceptions.PlayerNotFoundException;
import usecases.Response;

/**
 * Interactor for Disconnecting Use Case
 */
public class DcInteractor implements DcInputBoundary {
    private final LobbyManager lm;
    private final DcOutputBoundary dcOutputBoundary;

    /**
     * Constructor for DcInteractor
     * @param lm Lobby Manager
     * @param dcOutputBoundary DcOutputBoundary
     */
    public DcInteractor(LobbyManager lm, DcOutputBoundary dcOutputBoundary) {
        this.lm = lm;
        this.dcOutputBoundary = dcOutputBoundary;
    }

    /**
     * Disconnects the user
     * @param data input data which contains playerId
     */
    @Override
    public void disconnect(DcInputData data) {new Thread(new DcThread(data.getPlayerId())).start();}

    /**
     * Thread for disconnecting the player
     */
    public class DcThread implements Runnable {
        private final String playerId;

        /**
         * Constructor for Disconnecting Thread
         * @param playerId ID of the player we need to disconnect
         */
        public DcThread(String playerId) {this.playerId = playerId;}

        @Override
        public void run() {
            // Player existence in both removeFromPoolCancel and removePlayerFromGame
            // is checked via Player.equals, which checks only the ID, thus we can
            // have an empty display name as a dummy
            Player playerToDisconnect = new Player("", playerId);

            // Innocent until proven guilty
            Response response = Response.getSuccessful("Disconnecting was successful.");

            try {
                // Try to cancel player from pool. Will throw PlayerNotFound
                // if player isn't in pool so no need to check contains explicitly
                lm.removeFromPoolCancel(playerToDisconnect);
            } catch (PlayerNotFoundException ignored) {

                try {
                    // In this catch block, we know player was not in the pool.
                    // Now try to remove player from game.
                    lm.removePlayerFromGame(playerToDisconnect);
                } catch (PlayerNotFoundException | GameDoesntExistException e) {
                    // In both PlayerNotFound & GameDoesntExist, player was
                    // not found to be in the game, so respond with fail
                    response = Response.fromException(e, "Player not found");
                    e.printStackTrace();
                }
            }

            DcOutputData outputData = new DcOutputData(response, playerId);
            dcOutputBoundary.hasDisconnected(outputData);
        }
    }
}
