package entities.statistics;

import entities.Player;
import entities.games.GameReadOnly;

public class PerPlayerIntStatistic implements Statistic {
    @Override
    public void onSubmitWord(String word, Player author) {

    }

    @Override
    public void onTimerUpdate(GameReadOnly gameInfo) {

    }

    @Override
    public void onSuccessfulSwitchTurn(Player newCurrentTurnPlayer, int newSecondsLeftInCurrentTurn) {

    }

    @Override
    public Object getStatData() {
        return null;
    }
}
