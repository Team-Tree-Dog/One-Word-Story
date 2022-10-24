package entities.games;

import entities.Player;
import entities.boundaries.OnTimerUpdateBoundary;
import entities.boundaries.GameEndedBoundary;

import java.util.Map;
import java.util.List;

public class GameFactoryRegular implements GameFactory {
    /**
     * Accepting any settings, create the appropriate game instance of the GameRegular
     * @param settings A map of strings to integer settings
     * @param initialPlayers A list of initial players
     * @param otub A boundary for the timer
     * @param geb A boundary for the game ended scenario
     * @return the created game instance
     */
    public Game createGame(Map<String, Integer> settings, List<Player> initialPlayers, OnTimerUpdateBoundary otub,
                           GameEndedBoundary geb) {
        return new GameRegular(initialPlayers, otub, geb);
    }
}
