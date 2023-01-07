package adapters.view_models;

import adapters.display_data.not_ended_display_data.GameDisplayData;
import org.jetbrains.annotations.Nullable;

/**
 * A callback for the second stage of JPL. That is, inPool response
 * is almost instant, so it should be awaited. Subsequently, this callback
 * can be injected to react to the second output that either the player
 * cancelled, or is in a game
 */
public interface JplCallback {
    /**
     * @param hasCancelled true if player cancelled waiting
     * @param gameData game data if player joined game, or null if hasCancelled
     */
    void onUpdate(boolean hasCancelled, @Nullable GameDisplayData gameData);
}
