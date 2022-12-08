package usecases.pull_game_ended;

import usecases.Response;

import java.util.Set;

/**
 * Repo requirements for this use case, related to the Story data being stored in DB
 */
public interface PgeGatewayStory {

    /**
     * @param storyString String of the story from the finished game
     * @param publishUnixTimeStamp Unix Epoch (since Jan 1970 or something) in seconds
     * @param authorDisplayNames Set of author display names
     * @return Response of saving the story
     */
    Response saveStory(String storyString, double publishUnixTimeStamp, Set<String> authorDisplayNames);
}
