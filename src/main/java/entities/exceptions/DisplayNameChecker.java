package entities.exceptions;

/**
 * Passes a new display name for a new Player.
 * Will also be used in the future to check if the name is valid (no inappropiate names).
 */
public interface DisplayNameChecker {

    /**
     * Checks if the new Display Name is valid.
     * For now (October 24 2022), will always return True.
     *
     * @param displayName new display name for the Player.
     * @return returns if the name is valid/appropiate.
     */
    boolean checkValid(String displayName);

}
