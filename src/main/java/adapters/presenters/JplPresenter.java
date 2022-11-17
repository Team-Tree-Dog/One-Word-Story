package adapters.presenters;

import adapters.ViewModel;
import usecases.join_public_lobby.JplOutputBoundary;
import usecases.join_public_lobby.JplOutputDataJoinedGame;
import usecases.join_public_lobby.JplOutputDataResponse;

public class JplPresenter implements JplOutputBoundary {

    private final ViewModel viewM;

    /**
     * @param viewM Instance of the view model to write to
     */
    public JplPresenter (ViewModel viewM) { this.viewM = viewM; }

    /**
     * Notify the view model that a player with a particular ID was added to the pool,
     * or if a fail occurred, then notify with an error
     * @param dataJoinedPool Data that associates a player with a response
     */
    @Override
    public void inPool(JplOutputDataResponse dataJoinedPool) {

    }

    /**
     * Notify the view model that a player with a particular ID has been sorted into a game,
     * or a fail code otherwise
     * @param dataJoinedGame Data that provides a player ID and the game that player joined
     */
    @Override
    public void inGame(JplOutputDataJoinedGame dataJoinedGame) {

    }

    /**
     * Notify the view model that a player with a particular ID has cancelled their
     * pool waiting, or a fail code otherwise
     * @param dataCancelled Data that associates a player with a response
     */
    @Override
    public void cancelled(JplOutputDataResponse dataCancelled) {

    }
}
