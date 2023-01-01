package adapters.presenters;

import adapters.display_data.not_ended_display_data.GameDisplayData;
import adapters.view_models.JplViewModel;
import org.example.ANSI;
import org.example.Log;
import usecases.Response;
import usecases.join_public_lobby.JplOutputBoundary;
import usecases.join_public_lobby.JplOutputDataJoinedGame;
import usecases.join_public_lobby.JplOutputDataResponse;

import static usecases.Response.ResCode.SHUTTING_DOWN;

public class JplPresenter implements JplOutputBoundary {

    private final JplViewModel viewM;

    /**
     * @param viewM Instance of the view model to write to
     */
    public JplPresenter (JplViewModel viewM) { this.viewM = viewM; }

    /**
     * Notify the view model that a player with a particular ID was added to the pool,
     * or if a fail occurred, then notify with an error
     * @param dataJoinedPool Data that associates a player with a response
     */
    @Override
    public void inPool(JplOutputDataResponse dataJoinedPool) {
        Log.sendMessage(ANSI.BLUE, "JPL", ANSI.LIGHT_BLUE,
                "Presenter in pool for Player ID: " + dataJoinedPool.getPlayerId() + ", " +
                        dataJoinedPool.getRes());
        viewM.getResponseAwaitable().set(dataJoinedPool.getRes());
        viewM.getPlayerIdAwaitable().set(dataJoinedPool.getPlayerId());
    }

    /**
     * Notify the view model that a player with a particular ID has been sorted into a game,
     * or a fail code otherwise
     * @param dataJoinedGame Data that provides a player ID and the game that player joined
     */
    @Override
    public void inGame(JplOutputDataJoinedGame dataJoinedGame) {
        Log.sendMessage(ANSI.BLUE, "JPL", ANSI.LIGHT_BLUE,
                "Presenter in game ply ID " + dataJoinedGame.getPlayerId() + ", " +
                dataJoinedGame.getRes());

        viewM.setGameDisplay(GameDisplayData.fromGameDTO(dataJoinedGame.getGameData()));
    }

    /**
     * Notify the view model that a player with a particular ID has cancelled their
     * pool waiting, or a fail code otherwise
     * @param dataCancelled Data that associates a player with a response
     */
    @Override
    public void cancelled(JplOutputDataResponse dataCancelled) {
        Log.sendMessage(ANSI.BLUE, "JPL", ANSI.LIGHT_BLUE,
                "Presenter cancelled from pool ply ID " + dataCancelled.getPlayerId() +
                        ", " + dataCancelled.getRes());
        viewM.setCancelled();
    }

    @Override
    public void outputShutdownServer() {
        Log.sendMessage(ANSI.BLUE, "JPL", ANSI.LIGHT_BLUE,
                "Presenter outputShutdownServer");
        viewM.getResponseAwaitable().set(new Response(SHUTTING_DOWN, "Server shutting down"));
    }
}
