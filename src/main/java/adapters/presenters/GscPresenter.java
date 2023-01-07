package adapters.presenters;

import adapters.display_data.comment_data.CommentDisplayData;
import adapters.view_models.GscViewModel;
import usecases.CommentRepoData;
import usecases.Response;
import usecases.get_story_comments.GscOutputBoundary;
import usecases.get_story_comments.GscOutputData;

import java.util.ArrayList;
import java.util.List;

import static usecases.Response.ResCode.SHUTTING_DOWN;

public class GscPresenter implements GscOutputBoundary {

    private final GscViewModel viewM;

    /**
     * Constructor for GscPresenter
     * @param viewM Instance of the view model to write to
     */
    public GscPresenter(GscViewModel viewM) { this.viewM = viewM; }

    /**
     * Notify the view model that getting comments from a story was successful/failed
     * If success, pass comments to be updated
     * @param data necessary comment data
     */
    @Override
    public void putStoryComments(GscOutputData data) {
        if (data.getComments() == null) {
            viewM.getResponseAwaitable().set(data.getRes());
        } else {
            // Convert to display data
            List<CommentDisplayData> displayDataList = new ArrayList<>();
            for (CommentRepoData comment: data.getComments()) {
                displayDataList.add(CommentDisplayData.fromCommentRepoData(comment));
            }

            viewM.getCommentsAwaitable().set(displayDataList);
            viewM.getResponseAwaitable().set(data.getRes());
        }
    }

    @Override
    public void outputShutdownServer() {
        viewM.getResponseAwaitable().set(new Response(SHUTTING_DOWN, "Server shutting down"));
    }
}
