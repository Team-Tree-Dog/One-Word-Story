package net.onewordstory.core.usecases.pull_game_ended;

import net.onewordstory.core.entities.Player;
import net.onewordstory.core.entities.statistics.AllPlayerNamesStatistic;
import net.onewordstory.core.entities.statistics.PerPlayerIntStatistic;
import net.onewordstory.core.entities.story_save_checkers.Action;
import net.onewordstory.core.entities.story_save_checkers.FilterOutput;
import net.onewordstory.core.entities.story_save_checkers.StorySaveChecker;
import org.example.Log;
import net.onewordstory.core.usecases.Response;
import net.onewordstory.core.util.RecursiveSymboledIntegerHashMap;

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
    private final StorySaveChecker storySaveChecker;

    /**
     * Constructor for PgeInteractor
     * @param presenter Object to call for output
     */
    public PgeInteractor(PgeOutputBoundary presenter, PgeGatewayStory repo, StorySaveChecker storychecker) {
        this.presenter = presenter;
        this.repo = repo;
        this.storySaveChecker = storychecker;
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

        FilterOutput out = storySaveChecker.filterStory(data.getStoryString());
        if (out.getAction() == Action.ACCEPTED) {
            repo.saveStory(out.getFilteredStory(), Instant.now().getEpochSecond(),
                    data.getAuthorNamesStat().getStatData());
            Log.useCaseMsg("PGE", "Story passed through filter!");
        }
        if (out.getAction() == Action.MODIFIED) {
            repo.saveStory(out.getFilteredStory(), Instant.now().getEpochSecond(),
                    data.getAuthorNamesStat().getStatData());
            Log.useCaseMsg("PGE", "Modified Story passed through filter.");
        }
        else {
            Log.useCaseMsg("PGE", "Story rejected by filter.");
        }
        presenter.notifyGameEnded(new PgeOutputData(playerStatDTOs));
    }
}
