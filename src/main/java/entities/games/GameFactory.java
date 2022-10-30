package entities.games;

import entities.Player;
import entities.boundaries.OnTimerUpdateBoundary;
import entities.boundaries.GameEndedBoundary;

import java.util.Collection;
import java.util.Map;
import java.util.List;

public interface GameFactory {
    /**
     * Given some arguments, create the appropriate game instance
     * @param settings A map of strings to integer settings
     * @param initialPlayers A list of initial players
     * @return the created game instance
     */
    Game createGame(Map<String, Integer> settings, Collection<Player> initialPlayers);
}