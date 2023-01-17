package net.onewordstory.core.adapters.presenters;

import net.onewordstory.core.adapters.view_models.CagViewModel;
import net.onewordstory.core.usecases.Response;
import net.onewordstory.core.usecases.comment_as_guest.CagOutputBoundary;
import net.onewordstory.core.usecases.comment_as_guest.CagOutputData;

import static net.onewordstory.core.usecases.Response.ResCode.SHUTTING_DOWN;

public class CagPresenter implements CagOutputBoundary {

    private final CagViewModel viewM;

    /**
     * Constructor for CagPresenter
     * @param viewM Instance of the view model to write to
     */
    public CagPresenter(CagViewModel viewM) {
        this.viewM = viewM;
    }

    /**
     * Notify the view model that a user has made a guest comment on a story
     * @param data the relevant comment data
     */
    @Override
    public void commentAsGuestOutput(CagOutputData data) {
        viewM.getResponseAwaitable().set(data.getRes());
    }

    @Override
    public void outputShutdownServer() {
        viewM.getResponseAwaitable().set(new Response(SHUTTING_DOWN, "Server shutting down"));
    }
}
