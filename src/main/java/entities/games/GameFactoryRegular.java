package entities.games;

import entities.Player;
import entities.statistics.PerPlayerIntStatistic;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * Factory designed specifically for GameRegular
 */
public class GameFactoryRegular extends GameFactory {

    /**
     * @param perPlayerIntStatistics Player statistics you'd like to track
     */
    public GameFactoryRegular(PerPlayerIntStatistic[] perPlayerIntStatistics) {
        super(perPlayerIntStatistics);
    }

    public GameFactoryRegular() {}

    /**
     * Accepting any settings, create the appropriate game instance of the GameRegular
     * @param settings A map of strings to integer settings
     * @param initialPlayers A list of initial players
     * @return the created game instance
     */
    public Game createGame(Map<String, Integer> settings, Collection<Player> initialPlayers) {
        Queue<Player> queueOfInitialPlayers = new LinkedList<>(initialPlayers);
        return new GameRegular(queueOfInitialPlayers, getPerPlayerIntStatistics());
    }
}
