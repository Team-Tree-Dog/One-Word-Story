package adapters.display_data.not_ended_display_data;

import org.jetbrains.annotations.NotNull;

/**
 * Data structure for player display data
 */
public class PlayerDisplayData {

    public @NotNull String id;
    public @NotNull String displayName;
    public boolean isCurrentTurnPlayer;

    /**
     * Constructor for PlayerDisplayData
     * @param id id of the player
     * @param displayName display name of the player
     * @param isCurrentTurnPlayer true if it is the player's turn currently
     */
    protected PlayerDisplayData(@NotNull String id, @NotNull String displayName,
                                boolean isCurrentTurnPlayer) {
        this.id = id;
        this.displayName = displayName;
        this.isCurrentTurnPlayer = isCurrentTurnPlayer;
    }

    public @NotNull String getId() { return this.id; }

    public @NotNull String getDisplayName() { return this.displayName; }

    public boolean getIsCurrentTurnPlayer() { return this.isCurrentTurnPlayer; }
}
