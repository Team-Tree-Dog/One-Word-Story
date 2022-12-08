package adapters.presenters;

import adapters.view_models.CagViewModel;
import usecases.comment_as_guest.CagOutputBoundary;
import usecases.comment_as_guest.CagOutputData;

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

    }

    @Override
    public void outputShutdownServer() {

    }
}
