package entities.games;

import entities.Player;


import java.util.*;

/**
 * Factory designed specifically for GameRegular
 */
public class GameFactoryRegular implements GameFactory {
    /**
     * Accepting any settings, create the appropriate game instance of the GameRegular
     * @param settings A map of strings to integer settings
     * @param initialPlayers A list of initial players
     * @return the created game instance
     */
    public Game createGame(Map<String, Integer> settings, Collection<Player> initialPlayers) {
        Queue<Player> queueOfInitialPlayers = new LinkedList<>(initialPlayers);
        return new GameRegular(queueOfInitialPlayers);
    }
}
