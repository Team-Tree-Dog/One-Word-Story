package adapters.presenters;

import adapters.display_data.not_ended_display_data.GameDisplayData;
import adapters.view_models.DcViewModel;
import org.example.Log;
import usecases.Response;
import usecases.disconnecting.DcOutputBoundary;
import usecases.disconnecting.DcOutputData;

import static usecases.Response.ResCode.SHUTTING_DOWN;

public class DcPresenter implements DcOutputBoundary {

    private final DcViewModel viewM;

    /**
     * @param viewM Instance of the view model to write to
     */
    public DcPresenter (DcViewModel viewM) { this.viewM = viewM; }

    /**
     * Notify the view model whether or not the player was found in
     * the entities (success or error code respectively). Note that
     * if player was found, they were guaranteed to be removed. If they
     * were not found, it means they were never there. In both cases, the
     * player is guaranteed to be gone from the entities
     * @param data response associated with a player id
     */
    @Override
    public void hasDisconnected(DcOutputData data) {
        Log.useCaseMsg("DC Presenter", data.getResponse() + " with player ID " + data.getPlayerId());
        if (data.getGameData() != null) {
            Log.useCaseMsg("DC Presenter", data.getGameData() + " with player ID " + data.getPlayerId());
            viewM.getGameDataAwaitable().set(GameDisplayData.fromGameDTO(data.getGameData()));
        }
        viewM.getResponseAwaitable().set(data.getResponse());
    }

    @Override
    public void outputShutdownServer() {
        Log.useCaseMsg("DC", "Presenter outputShutdownServer");
        viewM.getResponseAwaitable().set(new Response(SHUTTING_DOWN, "Server shutting down"));
    }
}
