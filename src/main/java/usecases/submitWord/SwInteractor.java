package usecases.submitWord;

import entities.LobbyManager;
import exceptions.GameDoesntExistException;
import exceptions.InvalidWordException;
import exceptions.OutOfTurnException;
import exceptions.PlayerNotFoundException;
import usecases.Response;

import static usecases.Response.ResCode.*;

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

    /**
     * The game in which we are changing the Story, and accessing Players and their information.
     * Constructor.
     * @param presenter The output boundary which will be used to pass outputs to the ViewModel.
     * @param lobbyManager The LobbyManager, as described before.
     */
    public SwInteractor (SwOutputBoundary presenter, LobbyManager lobbyManager) {
        this.presenter = presenter;
        this.lobbyManager = lobbyManager;
    }

    /**
     * The method called by the ViewModel. It first checks that the player that attempted to submit the word
     * is indeed in the game, to prevent exploits. It then initiates the actual thread which checks that it
     * is the player's turn, then checks if the word is valid, then adds the word if it is valid.
     * @param inputData the SwInputData object that includes the word to be added as well as the ID of the player.
     */
    @Override
    public void submitWord (SwInputData inputData) {(new Thread(new SwThread(inputData))).start();}

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

            boolean success = true;

            try{
                lobbyManager.addWord(inputData.getWord(), this.playerId);
            } catch (GameDoesntExistException e) {
                success = false;
                Response resp = new Response(GAME_DOESNT_EXIST, "The Game Doesn't Exist");
                presenter.invalid(new SwOutputDataFailure(this.playerId, resp));
            } catch (InvalidWordException e) {
                success = false;
                String mess = String.format("The word %1$s is not valid, please try another word.", inputData.getWord());
                Response resp = new Response(INVALID_WORD, mess);
                presenter.invalid(new SwOutputDataFailure(this.playerId, resp));
            } catch (OutOfTurnException e) {
                success = false;
                String mess = "It is not player " + inputData.getPlayerId() + "'s turn.";
                Response resp = new Response(OUT_OF_TURN, mess);
                presenter.invalid(new SwOutputDataFailure(this.playerId, resp));
            } catch (PlayerNotFoundException e) {
                success = false;
                String mess = "Player with ID " + inputData.getPlayerId() + " does not exist or is not in the Game.";
                Response resp = new Response(PLAYER_NOT_FOUND, mess);
                presenter.invalid(new SwOutputDataFailure(this.playerId, resp));
            }

            if (success) {
                lobbyManager.switchTurn(); // Switch the turn.
                Response resp = new Response(SUCCESS, "Word has been added!");
                presenter.valid(new SwOutputDataValidWord(inputData.getWord(), this.playerId, resp));
            }

//            // Checks if it is the player's turn. If not, then output invalid response.
//            if (this.playerId != game.getCurrentTurnPlayer()) {
//                presenter.invalid(SwOutputDataFailure(this.playerId, rescode));
//            }
//
//            boolean success = true; // If true, then change turn and output valid response.
//
//            // Try to add the word.
//            try {
//                game.getStory().addWord(inputData.getWord(), this.playerId);
//            }
//            catch(Exception InvalidWordException) {
//                // If the word is invalid, throw the InvalidWordException.
//                success = false; // Don't run the valid word response block.
//                presenter.invalid(SwOutputDataFailure(this.playerId, rescode));
//            }
//
//            if (success) {
//                game.switchTurn(); // Switch the turn.
//                presenter.valid(SwOutputDataValidWord(game.getStory(), inputData.getWord(),
//                        this.playerId, rescode)); // Tell ViewModel the word was valid.
//            }
        }
    }
}
