package entities;

import exceptions.IdInUseException;

import java.util.ArrayList;

/**
 * Factory for creating players
 */
public class PlayerFactory {

    private final ArrayList<String> idsInUse;
    private final DisplayNameChecker displayChecker;

    /**
     * Constructor for PlayerFactory
     * @param nameChecker the display name checker the player factory will use
     */
    public PlayerFactory(DisplayNameChecker nameChecker) {
        displayChecker = nameChecker;
        idsInUse = new ArrayList<>();
    }

    /**
     * Create new player and throw exception if display name is invalid or id is already in use
     * @param newPlayerId the ID of the new player
     * @param newName the name of the new player
     * @return the created Player instance
     */
    public Player createPlayer(String newName, String newPlayerId) throws IdInUseException {
        if (idsInUse.contains(newPlayerId)) {throw new IdInUseException("ID " + newPlayerId + " already in use.");}
        idsInUse.add(newPlayerId);
        return new Player(newName, newPlayerId);
    }
}
