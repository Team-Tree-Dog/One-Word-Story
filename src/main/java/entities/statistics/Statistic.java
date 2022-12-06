package entities.statistics;

import entities.Player;
import entities.games.GameReadOnly;
import org.jetbrains.annotations.Nullable;

/**
 * Class which can be injected into game. The game will call the 3 event
 * callbacks which can be used to track statistics
 * @param <T> Type of data this statistic returns
 */
public interface Statistic<T> extends StatisticReadOnly<T> {

    /**
     * Called by Game right as soon as a player's word has been successfully
     * added to the story
     * @param word string word that was added
     * @param author Player object of who submitted this word
     */
    void onSubmitWord (String word, Player author);

    /**
     * Called by Game each timer update
     * @param gameInfo Read Only version of the game
     */
    void onTimerUpdate (GameReadOnly gameInfo);

    /**
     * Called by Game when a turn has been successfully switched
     * @param newCurrentTurnPlayer the new player whose turn it is, or null if the game has no players
     * @param newSecondsLeftInCurrentTurn the new time for this turn (usually the same each turn switch)
     */
    void onSuccessfulSwitchTurn (@Nullable Player newCurrentTurnPlayer, int newSecondsLeftInCurrentTurn);

    /**
     * @return the data this statistic is tracking
     */
    @Override
    T getStatData();
}
