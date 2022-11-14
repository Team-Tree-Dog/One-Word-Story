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
    public void disconnect(DcInputData data) {new Thread(new DcThread(data.playerId)).start();}

    /**
     * Thread for disconnecting the player
     */
    public class DcThread implements Runnable {
        private final String playerId;
        private Player playerToDisconnect;

        /**
         * Constructor for Disconnecting Thread
         * @param playerId ID of the player we need to disconnect
         */
        public DcThread(String playerId) {this.playerId = playerId;}

        @Override
        public void run() {
            Response response = Response.getSuccessful("Disconnecting was successful.");
            try {
                if(playerIsInTheGame(playerId))
                    lm.removePlayerFromGame(playerToDisconnect);
                else if(playerIsInThePool(playerId))
                    lm.removeFromPoolCancel(playerToDisconnect);
                else
                    throw new PlayerNotFoundException("Player was not found.");
            } catch (PlayerNotFoundException e) {
                response = Response.fromException(new PlayerNotFoundException("Disconnecting was not successful. Player was not found."),
                        "Player not found");
                e.printStackTrace();
            } catch (GameDoesntExistException e) {
                e.printStackTrace();
            }
            DcOutputData outputData = new DcOutputData(response);
            dcOutputBoundary.hasDisconnected(outputData);
        }

        /**
         * Checks whether the player is in the pool
         * @param playerId ID of the player we need to check
         * @return true if the player is in the pool
         * @throws PlayerNotFoundException if the player is not in the pool
         */
        private boolean playerIsInThePool(String playerId) throws PlayerNotFoundException {
            for(Player player : lm.getPlayersFromPool())
                if (player.getPlayerId().equals(playerId)) {
                    playerToDisconnect = player;
                    return true;
                }
            return false;
        }

        /**
         * Checks whether the player is in the game
         * @param playerId ID of the player we need to check
         * @return true if the player is in the game
         * @throws PlayerNotFoundException if the player is not in the game
         */
        private boolean playerIsInTheGame(String playerId) throws PlayerNotFoundException {
            for(Player player : lm.getPlayersFromGame())
                if(player.getPlayerId().equals(playerId)) {
                    playerToDisconnect = player;
                    return true;
                }
            return false;
        }
    }
}
