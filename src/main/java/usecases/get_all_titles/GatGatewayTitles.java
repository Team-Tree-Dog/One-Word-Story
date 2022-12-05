package usecases.get_all_titles;

import usecases.RepoRes;
import usecases.TitleRepoData;

public interface GatGatewayTitles {

    RepoRes<TitleRepoData> getAllTitles(int storyId);
}
