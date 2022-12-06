package entities.games;

import entities.Player;
import entities.statistics.PerPlayerIntStatistic;

import java.util.Collection;
import java.util.Map;

/**
 * An interface for game factories
 * Every type of game factory implements this class
 */
public abstract class GameFactory {

    private final PerPlayerIntStatistic[] perPlayerIntStatistics;

    /**
     * @param perPlayerIntStatistics Player statistics you'd like to track
     */
    public GameFactory(PerPlayerIntStatistic[] perPlayerIntStatistics) {
        this.perPlayerIntStatistics = perPlayerIntStatistics;
    }

    /**
     * Given some arguments, create the appropriate game instance
     * @param settings A map of strings to integer settings
     * @param initialPlayers A list of initial players
     * @return the created game instance
     */
    public abstract Game createGame(Map<String, Integer> settings, Collection<Player> initialPlayers);

    protected PerPlayerIntStatistic[] getPerPlayerIntStatistics() {
        return perPlayerIntStatistics;
    }
}
