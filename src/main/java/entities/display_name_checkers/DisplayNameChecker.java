package entities.display_name_checkers;

/**
 * Validates a new display name for a new Player.
 * Will also be used in the future to check if the name is valid (no inappropriate names).
 */
public interface DisplayNameChecker {

    /**
     * Checks if the new Display Name is valid.
     *
     * @param displayName new display name for the Player, must be trimmed first!
     * @return returns if the name is valid/appropiate.
     */
    boolean checkValid(String displayName);

}
