package net.onewordstory.core.entities.statistics;

import net.onewordstory.core.entities.Player;
import net.onewordstory.core.util.RecursiveSymboledIntegerHashMap;

import java.util.Map;

/**
 * An interface which extends statistic and is intended for statistics which store data for individual players.
 * That is, the statistic is duplicated per player and stored in a map which links Players
 * to stat content. The stat content is enforced as JSON style, that is, the type is a recursive DS of maps.
 * <br> <br>
 * This statistic is capable of storing stats of the following form: <br>
 * <code>
 * { <br>
 * &emsp;    Player1: {<br>
 * &emsp;&emsp;        "field_1": {...},<br>
 * &emsp;&emsp;        ...,<br>
 * &emsp;&emsp;        "field_n": {...}<br>
 * &emsp;    }<br>
 * }
 * </code>
 */
public interface PerPlayerIntStatistic extends Statistic<Map<Player, RecursiveSymboledIntegerHashMap>> { }
