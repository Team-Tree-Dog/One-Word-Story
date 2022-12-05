package usecases.get_all_titles;

import org.jetbrains.annotations.Nullable;
import usecases.Response;
import usecases.TitleRepoData;
import java.util.*;

public class GatOutputData {
    private List<TitleRepoData> suggestedTitles;
    private Response res;

    public GatOutputData(@Nullable List<TitleRepoData> suggestedTitles, Response res) {
        this.suggestedTitles = suggestedTitles;
        this.res = res;
    }

    @Nullable
    public List<TitleRepoData> getSuggestedTitles() {return suggestedTitles;}

    public Response getRes() {return res;}
}
