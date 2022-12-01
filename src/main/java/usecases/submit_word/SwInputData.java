package usecases.submit_word;

/**
 * The data that the SwInteractor needs in order to determine if a word can be added,
 * and then if it is valid, then add the word.
 */
public class SwInputData {
    private String word;

    private String playerId;

    /**
     * The constructor of SwInputData.
     * @param word     the word that has been submitted.
     * @param playerId the Id of the player that attempted to submit the word.
     */
    public SwInputData (String word, String playerId) {
        this.word = word;
        this.playerId = playerId;
    }

    /**
     * Returns the word that has been attempted to be submitted.
     * @return the word.
     */
    public String getWord() {
        return this.word;
    }

    /**
     * Sets a new word into the inputData.
     * @param word the new word.
     */
    public void setWord(String word) {
        this.word = word;
    }

    /**
     * Returns the id of the player that attempted to submit the word.
     * @return the playerId.
     */
    public String getPlayerId() {return this.playerId;}

    /**
     * Sets a new player using their Id.
     * @param playerId the id of the new player.
     */
    public void setPlayerId(String playerId) {this.playerId = playerId;}
}
