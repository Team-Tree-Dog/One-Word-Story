package entities.statistics;

import entities.Player;
import entities.games.GameReadOnly;
import org.jetbrains.annotations.Nullable;
import util.RecursiveSymboledIntegerHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tracks the average time each player takes to complete their turn
 */
public class AverageTurnDurationPlayerStatistic implements PerPlayerIntStatistic {

    private final Map<Player, List<Integer>> playerTurnTimes;
    private Player prevPlayer;

    public AverageTurnDurationPlayerStatistic() {
        playerTurnTimes = new HashMap<>();}

    @Override
    public void onSubmitWord(String word, Player author) {}

    @Override
    public void onTimerUpdate(GameReadOnly gameInfo) {
        Player curPlayer = gameInfo.getCurrentTurnPlayer();

        // New list if player previously had no record
        playerTurnTimes.computeIfAbsent(curPlayer, k -> new ArrayList<>());

        // Get list of turn times for current player
        List<Integer> curTurnTimes = playerTurnTimes.get(curPlayer);

        // If the player is different, it means the turn switched somewhere so we start a new count
        if (curPlayer != prevPlayer) {
            curTurnTimes.add(1);
        }

        else {
            // Add 1 second to the current turn count for this player
            curTurnTimes.set(curTurnTimes.size() - 1, curTurnTimes.get(curTurnTimes.size() - 1) + 1);
        }

    }

    @Override
    public void onSuccessfulSwitchTurn(@Nullable Player newCurrentTurnPlayer, int newSecondsLeftInCurrentTurn) {}

    @Override
    public Map<Player, RecursiveSymboledIntegerHashMap> getStatData() {
        Map<Player, RecursiveSymboledIntegerHashMap> output = new HashMap<>();

        for (Player p: playerTurnTimes.keySet()) {
            List<Integer> turnTimes = playerTurnTimes.get(p);
            // TODO: Sum avg
            output.put(p, )
        }
    }
}
