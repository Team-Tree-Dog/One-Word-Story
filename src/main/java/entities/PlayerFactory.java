package entities;

import exceptions.IdInUseException;

import java.util.ArrayList;

public class PlayerFactory {

    private ArrayList<String> idsInUse;

    /**
     * Accepting any settings, create the appropriate game instance of the GameRegular
     * @param newPlayerId the ID of the new player.
     * @param newName the name of the new player.
     * @return the created Player instance
     */
    public Player createPlayer(String newName, String newPlayerId) throws IdInUseException {
        if (idsInUse.contains(newPlayerId)) {throw new IdInUseException("ID " + newPlayerId + " already in use.");}
        idsInUse.add(newPlayerId);
        return new Player(newName, newPlayerId);
    }
}
