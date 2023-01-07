package adapters.view_models;

import adapters.display_data.not_ended_display_data.GameDisplayData;
import org.jetbrains.annotations.NotNull;

/**
 * An interface which PD can optionally
 * have set so PD calls this method on each update
 */
public interface PdCallback {

    /**
     * Called by PD each time new game data is set
     * @param newGameData new display data for game
     */
    void onUpdate(@NotNull GameDisplayData newGameData);
}
