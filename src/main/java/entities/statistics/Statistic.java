package entities.statistics;

import entities.Player;
import entities.games.GameReadOnly;

public interface Statistic<T> {
    void onSubmitWord (String word, Player author);

    void onTimerUpdate (GameReadOnly gameInfo);

    void onSuccessfulSwitchTurn (Player newCurrentTurnPlayer, int newSecondsLeftInCurrentTurn);

    T getStatData ();
}
