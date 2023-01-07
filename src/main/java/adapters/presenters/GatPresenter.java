package adapters.presenters;

import adapters.display_data.comment_data.CommentDisplayData;
import adapters.display_data.title_data.SuggestedTitleDisplayData;
import adapters.view_models.GatViewModel;
import usecases.CommentRepoData;
import usecases.Response;
import usecases.TitleRepoData;
import usecases.get_all_titles.GatOutputBoundary;
import usecases.get_all_titles.GatOutputData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static usecases.Response.ResCode.SHUTTING_DOWN;

public class GatPresenter implements GatOutputBoundary {
    private final GatViewModel viewM;

    public GatPresenter(GatViewModel viewM) {this.viewM = viewM;}

    @Override
    public void putSuggestedTitles(GatOutputData data){
        if (data.getSuggestedTitles() == null) {
            viewM.getResponseAwaitable().set(data.getRes());
        } else {
            // Convert to display data
            List<SuggestedTitleDisplayData> displayDataList = new ArrayList<>();
            for (TitleRepoData title: data.getSuggestedTitles()) {
                displayDataList.add(SuggestedTitleDisplayData.fromTitleRepoData(title));
            }

            // Sort by title upvotes
            SuggestedTitleDisplayData[] arr = displayDataList.toArray(new SuggestedTitleDisplayData[0]);
            Arrays.sort(arr);
            displayDataList = Arrays.asList(arr);

            viewM.getSuggestedTitlesAwaitable().set(displayDataList);
            viewM.getResponseAwaitable().set(data.getRes());
        }
    }

    @Override
    public void outputShutdownServer(){
        viewM.getResponseAwaitable().set(new Response(SHUTTING_DOWN, "Server shutting down"));
    }
}
