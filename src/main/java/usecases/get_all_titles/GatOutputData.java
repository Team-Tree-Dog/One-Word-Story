package usecases.get_all_titles;

import usecases.Response;

public class GatOutputData {
    private List<TitleRepoData> suggestedTitles;
    private Response res;

    public GatOutputData(List<TitleRepoData> suggestedTitles, Response res) {
        this.suggestedTitles = suggestedTitles;
        this.res = res;
    }

    public List<TitleRepoData> getSuggestedTitles() {return suggestedTitles;}

    public Response getRes() {return res;}
}
