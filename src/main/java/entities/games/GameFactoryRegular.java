package entities.games;

import entities.Player;
import entities.boundaries.OnTimerUpdateBoundary;
import entities.boundaries.GameEndedBoundary;

import java.util.*;

public class GameFactoryRegular implements GameFactory {
    /**
     * Accepting any settings, create the appropriate game instance of the GameRegular
     * @param settings A map of strings to integer settings
     * @param initialPlayers A list of initial players
     * @return the created game instance
     */
<<<<<<< Updated upstream
    public Game createGame(Map<String, Integer> settings, Collection<Player> initialPlayers) {
        Queue<Player> queueOfInitialPlayers = new LinkedList<>(initialPlayers);
        return new GameRegular(queueOfInitialPlayers);
=======
    public Game createGame(Map<String, Integer> settings, List<Player> initialPlayers) {
        return new GameRegular(initialPlayers);
>>>>>>> Stashed changes
    }
}
