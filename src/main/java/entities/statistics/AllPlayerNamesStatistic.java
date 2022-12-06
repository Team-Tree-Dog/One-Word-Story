package entities.statistics;

import entities.Player;
import entities.games.GameReadOnly;

import java.util.Set;

public class AllPlayerNamesStatistic implements Statistic<Set<String>> {
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
    public Set<String> getStatData() {
        return null;
    }
}
