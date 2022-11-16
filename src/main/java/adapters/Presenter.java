package adapters;

import usecases.disconnecting.DcOutputBoundary;
import usecases.disconnecting.DcOutputData;
import usecases.join_public_lobby.JplOutputBoundary;
import usecases.join_public_lobby.JplOutputDataJoinedGame;
import usecases.join_public_lobby.JplOutputDataResponse;
import usecases.pull_data.PdOutputBoundary;
import usecases.pull_data.PdOutputData;
import usecases.pull_game_ended.PgeOutputBoundary;
import usecases.pull_game_ended.PgeOutputData;
import usecases.submit_word.SwOutputBoundary;
import usecases.submit_word.SwOutputDataFailure;
import usecases.submit_word.SwOutputDataValidWord;

/**
 * Implements output boundaries of all use cases and passes information
 * to the view model through the corresponding output methods. Acts as
 * a bridge between the view model and use cases
 */
public class Presenter implements JplOutputBoundary, PdOutputBoundary,
        PgeOutputBoundary, DcOutputBoundary, SwOutputBoundary {

    private final ViewModel viewM;

    /**
     * @param viewM Instance of the view model to write to
     */
    public Presenter (ViewModel viewM) { this.viewM = viewM; }

    /*
     *  _______   ______
     * |       \ /      |
     * |  .--.  |  ,----'
     * |  |  |  |  |
     * |  '--'  |  `----.
     * |_______/ \______|
     *
     */

    /**
     * Notify the view model whether or not the player was found in
     * the entities (success or error code respectively). Note that
     * if player was found, they were guaranteed to be removed. If they
     * were not found, it means they were never there. In both cases, the
     * player is guaranteed to be gone from the entities
     * @param data response associated with a player id
     */
    @Override
    public void hasDisconnected(DcOutputData data) {

    }

    /*
     *        __  .______    __
     *       |  | |   _  \  |  |
     *       |  | |  |_)  | |  |
     * .--.  |  | |   ___/  |  |
     * |  `--'  | |  |      |  `----.
     *  \______/  | _|      |_______|
     *
     */

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

    /*
     * .______    _______
     * |   _  \  |       \
     * |  |_)  | |  .--.  |
     * |   ___/  |  |  |  |
     * |  |      |  '--'  |
     * | _|      |_______/
     *
     */

    /**
     * Update the view model's state of the current game
     * @param d PdOutputData
     */
    @Override
    public void updateGameInfo(PdOutputData d) {

    }

    /*
     * .______     _______  _______
     * |   _  \   /  _____||   ____|
     * |  |_)  | |  |  __  |  |__
     * |   ___/  |  | |_ | |   __|
     * |  |      |  |__| | |  |____
     * | _|       \______| |_______|
     *
     */

    /**
     * Notify the view model that the current game has ended along with a list
     * of player IDs who were in that game and now have been removed since the game
     * ended. Also notify of any end-of-game data to display
     * @param data contains data to pass to this output boundary (relevant player ids)
     */
    @Override
    public void notifyGameEnded(PgeOutputData data) {

    }

    /*
     *      _______.____    __    ____
     *     /       |\   \  /  \  /   /
     *    |   (----` \   \/    \/   /
     *     \   \      \            /
     * .----)   |      \    /\    /
     * |_______/        \__/  \__/
     *
     */

    /**
     * Notify that a player with a particular ID has submitted a valid word
     * and that it has been added to the story
     * @param outputDataValidWord the wrapped output data.
     */
    @Override
    public void valid(SwOutputDataValidWord outputDataValidWord) {

    }

    /**
     * Notify that a player with a particular ID has submitted an invalid word
     * and that it has not been added to the story
     * @param outputDataFailure the wrapped output data.
     */
    @Override
    public void invalid(SwOutputDataFailure outputDataFailure) {

    }
}
