package usecases.submit_word;

import entities.LobbyManager;
import exceptions.GameDoesntExistException;
import exceptions.InvalidWordException;
import exceptions.OutOfTurnException;
import exceptions.PlayerNotFoundException;
import usecases.Response;

import java.util.concurrent.locks.Lock;

/**
 * SwInteractor is the interactor that calls the necessary backend functions to access and change the story.
 * SwInteractor also tells the ViewModel, via the SwOutputBoundary presenter, if the word is valid or not.
 * SwInteractor in effect gives the frontend the command to change the output.
 */
public class SwInteractor implements SwInputBoundary{

    /**
     * The presenter used to pass outputs to the ViewModel.
     */
    private final SwOutputBoundary presenter;

    /**
     * The LobbyManager from which we obtain the game.
     */
    private final LobbyManager lobbyManager;

    private final Lock gameLock;

    /**
     * The game in which we are changing the Story, and accessing Players and their information.
     * Constructor.
     * @param presenter The output boundary which will be used to pass outputs to the ViewModel.
     * @param lobbyManager The LobbyManager, as described before.
     */
    public SwInteractor (SwOutputBoundary presenter, LobbyManager lobbyManager) {
        this.presenter = presenter;
        this.lobbyManager = lobbyManager;
        this.gameLock = lobbyManager.getGameLock();
    }

    /**
     * The method called by the SwInteractor. It initiates the actual thread, which calls the addWord method from the
     * LobbyManager. addWord raises an exception in case of anything that goes awry. If not, then the word is valid.
     * In any case, the submitWord method here transmits the response (i.e. exception response or "valid" response) to
     * the presenter.
     *
     * @param inputData the SwInputData object that includes the word to be added as well as the ID of the player.
     */
    @Override
    public void submitWord (SwInputData inputData) {
        SwInteractor.SwThread swintThread = this.new SwThread(inputData);
        swintThread.run();
    }

    /**
     * A thread that does all the processes in the SwInteractor.
     */
    public class SwThread implements Runnable{

        /**
         * The SwInputData, passed from the submitWord method.
         */
        private final SwInputData inputData;

        /**
         * ID of the Player that attempted to submit the word.
         */
        private final String playerId;

        /**
         * Constructor.
         * @param inputData The SwInputData.
         */
        public SwThread(SwInputData inputData){
            this.inputData = inputData;
            this.playerId = inputData.getPlayerId();
        }

        /**
         * This method includes all the processes that will happen in the thread.
         */
        @Override
        public void run() {
            gameLock.lock();
            boolean success = true;

            try{
                lobbyManager.addWord(inputData.getWord(), this.playerId);
            } catch (GameDoesntExistException e) {
                success = false;
                String mess = "The Game you are trying to submit a word to doesn't exist";
                Response resp = Response.fromException(e, mess);
                presenter.invalid(new SwOutputDataFailure(this.playerId, resp));
            } catch (InvalidWordException e) {
                success = false;
                String mess = String.format("The word '%1$s' is not valid, please try another word.", inputData.getWord());
                Response resp = Response.fromException(e, mess);
                presenter.invalid(new SwOutputDataFailure(this.playerId, resp));
            } catch (OutOfTurnException e) {
                success = false;
                String mess = "It is not player " + inputData.getPlayerId() + "'s turn.";
                Response resp = Response.fromException(e, mess);
                presenter.invalid(new SwOutputDataFailure(this.playerId, resp));
            } catch (PlayerNotFoundException e) {
                success = false;
                String mess = "Player with ID " + inputData.getPlayerId() + " does not exist or is not in the Game.";
                Response resp = Response.fromException(e, mess);
                presenter.invalid(new SwOutputDataFailure(this.playerId, resp));
            }

            if (success) {
                lobbyManager.switchTurn(); // Switch the turn.
                String mess = String.format("Word '%1$s' has been added!", inputData.getWord());
                Response resp = Response.getSuccessful(mess);
                presenter.valid(new SwOutputDataValidWord(inputData.getWord(), this.playerId, resp));
            }
            gameLock.unlock();
        }
    }
}
