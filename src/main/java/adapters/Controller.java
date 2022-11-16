package adapters;

import usecases.disconnecting.DcInputBoundary;
import usecases.disconnecting.DcInputData;
import usecases.join_public_lobby.JplInputBoundary;
import usecases.join_public_lobby.JplInputData;
import usecases.submit_word.SwInputBoundary;
import usecases.submit_word.SwInputData;

/**
 * Contains wrapper methods for all use cases which are meant
 * to be triggered by a user from the view.
 */
public class Controller {

    private final JplInputBoundary jpl;
    private final DcInputBoundary dc;
    private final SwInputBoundary sw;

    /**
     * Take in and set an instance of each Use Case input boundary that
     * is intended to be called by users from the view
     */
    public Controller (JplInputBoundary jpl, DcInputBoundary dc, SwInputBoundary sw) {
        this.jpl = jpl;
        this.dc = dc;
        this.sw = sw;
    }

    /**
     * Provide a unique ID and a display name for a player who wishes to join a public lobby
     * @param playerId Unique ID of player never previously used
     * @param displayName Desired display name, may be duplicated
     */
    public void joinPublicLobby (String playerId, String displayName) {
        jpl.joinPublicLobby(
                new JplInputData(displayName, playerId)
        );
    }

    /**
     * Provide the ID of a player who wishes to disconnect from the game, or who has already
     * disconnected and the server is simply requesting their removal
     * @param playerId Unique ID of player never previously used
     */
    public void disconnect (String playerId) {
        dc.disconnect(
                new DcInputData(playerId)
        );
    }

    /**
     * Called for a player who wishes to submit a word during their turn, or out
     * of turn which will result in an error
     * @param playerId Unique ID of player never previously used
     * @param word Word that the player is submitting
     */
    public void submitWord (String playerId, String word) {
        sw.submitWord(
                new SwInputData(word, playerId)
        );
    }

}
