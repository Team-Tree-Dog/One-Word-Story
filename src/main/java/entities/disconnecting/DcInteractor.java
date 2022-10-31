package entities.disconnecting;

import entities.LobbyManager;
import entities.Player;
import entities.disconnecting.boundaries.DcInputData;
import entities.disconnecting.boundaries.DcOutputBoundary;
import exceptions.PlayerNotFoundException;


public class DcInteractor {
    static LobbyManager lm;

    /**
     * Constructor for DcInteractor
     * @param lm lobby manager
     */
    public DcInteractor(LobbyManager lm, DcOutputBoundary dcOutputBoundary) {
        DcInteractor.lm = lm;
    }

    /**
     * Disconnects the user
     * @param data input data
     */
    public void disconnect(DcInputData data)  {
        new Thread(new DcThread(data.playerId)).start();
    }

    /**
     * Thread for disconnecting the player
     */
    public static class DcThread implements Runnable {
        private final String playerId;
        private Player playerToDisconnect;

        /**
         * Constructor for Disconnecting Thread
         * @param playerId ID of the player we need to disconnect
         */
        public DcThread(String playerId) {
            this.playerId = playerId;
        }

        @Override
        public void run() {
            try {
                if(playerIsInThePool(playerId))
                    lm.removeFromPoolCancel(playerToDisconnect);
            } catch (PlayerNotFoundException e) {
                e.printStackTrace();
            }
        }

        //TODO: create the output data (waiting for Response)

        /**
         * Checks whether the player is in the pool
         * @param playerId ID of the player we need to check
         * @return true if the player is in the pool
         * @throws PlayerNotFoundException if the player is not in the pool
         */
        private boolean playerIsInThePool(String playerId) throws PlayerNotFoundException {
            for(Player player : lm.getPlayersFromPool())
                if(player.getPlayerId().equals(playerId)) {
                    playerToDisconnect = player;
                    return true;
                }
            return false;
        }
    }
}

