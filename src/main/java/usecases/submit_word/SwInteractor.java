package usecases.submit_word;

import entities.LobbyManager;
import exceptions.GameDoesntExistException;
import exceptions.InvalidWordException;
import exceptions.OutOfTurnException;
import exceptions.PlayerNotFoundException;
import org.example.Log;
import usecases.GameDTO;
import usecases.InterruptibleThread;
import usecases.Response;
import usecases.ThreadRegister;

import java.util.concurrent.locks.Lock;

/**
 * SwInteractor is the interactor that calls the necessary backend functions to access and change the story.
 * SwInteractor also tells the ViewModel, via the SwOutputBoundary presenter, if the word is valid or not.
 * SwInteractor in effect gives the frontend the command to change the output.
 */
public class SwInteractor implements SwInputBoundary {

    /**
     * The LobbyManager from which we obtain the game.
     */
    private final LobbyManager lobbyManager;

    /**
     * The ThreadRegister that keeps track of all the running use case threads
     * for the shutdown-server use case
     */
    private final ThreadRegister register;

    private final Lock gameLock;

    /**
     * The game in which we are changing the Story, and accessing Players and their information.
     * Constructor.
     * @param lobbyManager The LobbyManager, as described before.
     */
    public SwInteractor (LobbyManager lobbyManager, ThreadRegister register) {
        this.lobbyManager = lobbyManager;
        this.gameLock = lobbyManager.getGameLock();
        this.register = register;
    }

    /**
     * The method called by the SwInteractor. It initiates the actual thread, which calls the addWord method from the
     * LobbyManager. addWord raises an exception in case of anything that goes awry. If not, then the word is valid.
     * In any case, the submitWord method here transmits the response (i.e. exception response or "valid" response) to
     * the presenter.
     *
     * @param inputData the SwInputData object that includes the word to be added as well as the ID of the player.
     * @param presenter output boundary for this use case
     */
    @Override
    public void submitWord (SwInputData inputData, SwOutputBoundary presenter) {
        InterruptibleThread swintThread = this.new SwThread(inputData, presenter);
        if (!register.registerThread(swintThread)) {
            presenter.outputShutdownServer();
        }
    }

    /**
     * A thread that does all the processes in the SwInteractor.
     */
    public class SwThread extends InterruptibleThread {

        /**
         * The SwInputData, passed from the submitWord method.
         */
        private final SwInputData inputData;

        /**
         * ID of the Player that attempted to submit the word.
         */
        private final String playerId;

        private final SwOutputBoundary presenter;

        /**
         * Constructor.
         * @param inputData The SwInputData.
         */
        public SwThread(SwInputData inputData, SwOutputBoundary presenter) {
            super(SwInteractor.this.register, presenter);
            this.inputData = inputData;
            this.presenter = presenter;
            this.playerId = inputData.getPlayerId();
        }

        /**
         * This method includes all the processes that will happen in the thread.
         */
        @Override
        public void threadLogic() {
            Log.useCaseMsg("SW", "Wants GAME lock");
            gameLock.lock();
            Log.useCaseMsg("SW", "Got GAME lock");
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

                try {
                    presenter.valid(new SwOutputDataValidWord(GameDTO.fromGame(lobbyManager.getGameReadOnly()),
                            this.playerId, resp));
                } catch (GameDoesntExistException ignored) {/* IMPOSSIBLE ERROR */}

            }
            gameLock.unlock();
            Log.useCaseMsg("SP", "Released GAME lock");
        }
    }
}
