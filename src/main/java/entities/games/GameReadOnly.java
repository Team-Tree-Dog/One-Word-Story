package entities.games;

import entities.Player;
import entities.statistics.AllPlayerNamesStatistic;
import entities.statistics.Statistic;
import entities.statistics.StatisticReadOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.RecursiveSymboledIntegerHashMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An "upcasted" version of the Game abstract class which only
 * permits using the getters. This allows the game to be passed around
 * freely without worrying that someone will accidentally overstep and
 * alter something they shouldn't
 */
public interface GameReadOnly {

    /**
     * @return Single string of the entire story in the game currently
     */
    @NotNull
    String getStoryString ();

    /**
     * @return shallow copy of all players currently in the game
     */
    @NotNull
    Collection<Player> getPlayers ();

    /**
     * @return A list of statistics which track per player data to be displayed
     * in the game end screen
     */
    @NotNull
    StatisticReadOnly<Map<Player, RecursiveSymboledIntegerHashMap>>[] getPlayerStatistics ();

    /**
     * @return Special statistic which keeps track of the display names of all contributing
     * players
     */
    @NotNull
    StatisticReadOnly<Set<String>> getAuthorNamesStatistic ();

    /**
     * @param playerId ID of player you'd like to retrieve
     * @return player with requested id, or null if not found
     */
    @Nullable
    Player getPlayerById (String playerId);

    /**
     * @return Player whose turn it is currently in the game, or null if the game
     * has no players
     */
    @Nullable
    Player getCurrentTurnPlayer ();

    /**
     * <h3>Important Notes: </h3>
     * The general flow of use cases is as follows:
     * <ol>
     *  <li> RG catches lock. if isGameOver is true, it stops itself via setTimeStopped, so isGameEnded now
     * returns true </li>
     *  <li> SP catches lock. If isGameEnded, the game object in LobbyManager is set to null </li>
     *  <li> There is an opportunity for another use case to catch the lock in between, in which case
     * the game timer stopped but the game still exists </li>
     *  <li> There is also an opportunity for a use case to catch before RG, where isGameOver is true
     * but the time has not had a chance to be stopped yet    </li>
     * </ol>

     *
     * @return If the game's custom conditions evaluate that the game is over
     */
    boolean isGameOver ();

    /**
     * See the implementation notes for isGameOver to see where this method
     * falls into the scheme of things
     * @return If RG has processed the fact that isGameOver is true and stopped the timer
     */
    boolean isTimerStopped ();
}
