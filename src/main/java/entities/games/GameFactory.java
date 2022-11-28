package entities.games;

import entities.Player;

import java.util.Collection;
import java.util.Map;

/**
 * An interface for game factories
 * Every type of game factory implements this class
 */
public interface GameFactory {
    /**
     * Given some arguments, create the appropriate game instance
     * @param settings A map of strings to integer settings
     * @param initialPlayers A list of initial players
     * @return the created game instance
     */
    Game createGame(Map<String, Integer> settings, Collection<Player> initialPlayers);
}
