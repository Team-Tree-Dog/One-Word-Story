package usecases.join_public_lobby;

/**
 * Defines the methods of the JplInteractor which are exposed
 * to the adapters layer (controller)
 */
public interface JplInputBoundary {

    /**
     * Starts a thread which will run until a player is in a game or has
     * chosen to cancel waiting
     * @param data Describes player who wishes to join a public lobby
     * @param pres output boundary for this use case
     */
    void joinPublicLobby(JplInputData data, JplOutputBoundary pres);
}
