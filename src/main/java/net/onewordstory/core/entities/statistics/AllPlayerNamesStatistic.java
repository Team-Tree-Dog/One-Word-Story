package net.onewordstory.core.entities.statistics;

import net.onewordstory.core.entities.Player;
import net.onewordstory.core.entities.games.GameReadOnly;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Tracks the set of display names of all players who have
 * participated in the game. Used explicitly in Game for PGE
 * to know the authors
 */
public class AllPlayerNamesStatistic implements Statistic<Set<String>> {

    private final Set<String> displayNames;

    public AllPlayerNamesStatistic() {
        displayNames = new HashSet<>();
    }

    @Override
    public void onSubmitWord(String word, Player author) {}

    @Override
    public void onTimerUpdate(GameReadOnly gameInfo) {
        List<String> displayNames = new ArrayList<>();

        // Extract display names of all players in the game
        for (Player p : gameInfo.getPlayers()) {
            displayNames.add(p.getDisplayName());
        }

        // Add them to the set
        this.displayNames.addAll(displayNames);
    }

    @Override
    public void onSuccessfulSwitchTurn(Player newCurrentTurnPlayer, int newSecondsLeftInCurrentTurn) {}

    @Override
    public Set<String> getStatData() {
        return displayNames;
    }
}
