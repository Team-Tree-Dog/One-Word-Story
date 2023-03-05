package net.onewordstory.core.usecases.pull_game_ended;

import net.onewordstory.core.entities.Player;
import net.onewordstory.core.entities.statistics.AllPlayerNamesStatistic;
import net.onewordstory.core.entities.statistics.PerPlayerIntStatistic;

import java.util.List;

/**
 * Data to pass along when a game ends
 */
public class PgeInputData {

    private final List<Player> players;
    private final String storyString;
    private final List<PerPlayerIntStatistic> statistics;
    private final AllPlayerNamesStatistic authorNamesStat;

    /**
     * Constructor for PgeInputData
     * @param players the players in the game that ended
     */
    public PgeInputData(List<Player> players, String storyString,
                        List<PerPlayerIntStatistic> statistics, AllPlayerNamesStatistic authorNamesStat) {
        this.players = players;
        this.storyString = storyString;
        this.statistics = statistics;
        this.authorNamesStat = authorNamesStat;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public AllPlayerNamesStatistic getAuthorNamesStat() { return authorNamesStat; }

    public List<PerPlayerIntStatistic> getStatistics() { return statistics; }

    public String getStoryString() { return storyString; }
}
