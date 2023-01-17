package net.onewordstory.core.adapters.presenters;

import net.onewordstory.core.adapters.display_data.not_ended_display_data.GameDisplayData;
import net.onewordstory.core.adapters.view_models.SwViewModel;
import org.example.ANSI;
import org.example.Log;
import net.onewordstory.core.usecases.Response;
import net.onewordstory.core.usecases.submit_word.SwOutputBoundary;
import net.onewordstory.core.usecases.submit_word.SwOutputDataFailure;
import net.onewordstory.core.usecases.submit_word.SwOutputDataValidWord;

import static net.onewordstory.core.usecases.Response.ResCode.SHUTTING_DOWN;

public class SwPresenter implements SwOutputBoundary {

    private final SwViewModel viewM;

    /**
     * @param viewM Instance of the view model to write to
     */
    public SwPresenter (SwViewModel viewM) { this.viewM = viewM; }

    /**
     * Notify that a player with a particular ID has submitted a valid word
     * and that it has been added to the story
     * @param outputDataValidWord the wrapped output data.
     */
    @Override
    public void valid(SwOutputDataValidWord outputDataValidWord) {
        Log.sendMessage(ANSI.BLUE, "SW", ANSI.LIGHT_BLUE,
                "Presenter valid word ply ID " + outputDataValidWord.getPlayerId() +
                ", " + outputDataValidWord.getResponse());

        viewM.getGameDataAwaitable().set(GameDisplayData.fromGameDTO(outputDataValidWord.getGameData()));
        viewM.getResponseAwaitable().set(outputDataValidWord.getResponse());
    }

    /**
     * Notify that a player with a particular ID has submitted an invalid word
     * and that it has not been added to the story
     * @param outputDataFailure the wrapped output data.
     */
    @Override
    public void invalid(SwOutputDataFailure outputDataFailure) {
        Log.sendMessage(ANSI.BLUE, "SW", ANSI.LIGHT_BLUE,
                "Presenter invalid word ply ID " + outputDataFailure.getPlayerId() +
                        ", " + outputDataFailure.getResponse());
        viewM.getResponseAwaitable().set(outputDataFailure.getResponse());
    }

    @Override
    public void outputShutdownServer() {
        Log.sendMessage(ANSI.BLUE, "SW", ANSI.LIGHT_BLUE,
                "Presenter outputShutdownServer");
        viewM.getResponseAwaitable().set(new Response(SHUTTING_DOWN, "Server shutting down"));
    }
}
