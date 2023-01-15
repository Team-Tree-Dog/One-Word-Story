package adapters.view_models;

import adapters.display_data.GameEndPlayerDisplayData;

import java.util.Map;

/**
 * Callback that gets called each time a game ends and PGE outputs
 */
public interface PgeCallback {

    /**
     * Custom callback for reacting to a game ending
     * @param playerStatData The data that PGE provides containing a list of player statistics
     */
    void onGameEnd(Map<String, GameEndPlayerDisplayData> playerStatData);
}
