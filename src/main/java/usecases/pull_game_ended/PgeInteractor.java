package usecases.pull_game_ended;

import entities.Player;
import entities.statistics.AllPlayerNamesStatistic;
import entities.statistics.PerPlayerIntStatistic;
import org.example.Log;
import usecases.Response;
import util.RecursiveSymboledIntegerHashMap;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Interactor for the Pull Game Ended Data use case
 */
public class PgeInteractor implements PgeInputBoundary {

    private final PgeOutputBoundary presenter;
    private final PgeGatewayStory repo;

    /**
     * Constructor for PgeInteractor
     * @param presenter Object to call for output
     */
    public PgeInteractor(PgeOutputBoundary presenter, PgeGatewayStory repo) {
        this.presenter = presenter;
        this.repo = repo;
    }

    /**
     * Notifies presenter of player ids that need to be notified that the game has ended
     * FUTURE: Updates database about something
     * @param data game statistics, including players in the game that ended
     */
    @Override
    public void onGameEnded(PgeInputData data) {
        /*
        This code takes care of repo saving
        ---------------
         */
        AllPlayerNamesStatistic authorNames = data.getAuthorNamesStat();
        // We currently don't do anything on fail except print. If repo returns a fail code, then the
        // story has failed to save so "oh well"
        Response res = repo.saveStory(data.getStoryString(), Instant.now().getEpochSecond(),
                authorNames.getStatData());

        // Show message
        if (res.getCode() == Response.ResCode.SUCCESS) Log.useCaseMsg("PGE", "Saved Story to DB!");
        else Log.useCaseMsg("PGE ERROR", "Failed to save story: " + res);

        /*
        Below code takes care of output
        ---------------
         */
        List<Player> players = data.getPlayers();
        List<PerPlayerIntStatistic> statistics = data.getStatistics();

        int numberOfPlayers = players.size();

        PlayerStatisticDTO[] playerStatDTOs = new PlayerStatisticDTO[numberOfPlayers];

        Map<Player, List<RecursiveSymboledIntegerHashMap>> playerToStats = new HashMap<>();

        // Go through statistic objects
        for (PerPlayerIntStatistic s : statistics) {
            // Compute stat map Player : Recursive hashmap
            Map<Player, RecursiveSymboledIntegerHashMap> statMap = s.getStatData();

            // For each player, append their recursive map to their overall list of accumulating
            // recursive maps
            for (Player p : statMap.keySet()) {
                playerToStats.computeIfAbsent(p, k -> new ArrayList<>());
                playerToStats.get(p).add(statMap.get(p));
            }
        }

        for (int i = 0; i < numberOfPlayers; i++) {
            // Get Player
            Player p = players.get(i);

            // Pass id, display name, and array-converted rec map stat data accumulation
            playerStatDTOs[i] = new PlayerStatisticDTO(
                    p.getPlayerId(), p.getDisplayName(),
                    playerToStats.getOrDefault(p, new ArrayList<>()).toArray(new RecursiveSymboledIntegerHashMap[0])
            );
        }

        presenter.notifyGameEnded(new PgeOutputData(playerStatDTOs));
    }
}
