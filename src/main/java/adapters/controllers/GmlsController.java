package adapters.controllers;

import usecases.get_most_liked_stories.GmlsInputBoundary;
import usecases.get_most_liked_stories.GmlsInputData;
import usecases.get_most_liked_stories.GmlsInteractor;

public class GmlsController {
    GmlsInputBoundary gmls;

    /**
     * 1. Build the input data for the Get Most Liked Stories use case
     * 2. Call the method gmls.getMostLikedStories() to begin the use case
     * @param lowerRangeInclusive the lower inclusive bound for the range of stories sorted
     *                            in descending order by likes
     * @param upperRangeExclusive the upper inclusive bound for the range of stories sorted
     *                            in descending order by likes
     */
    public void getMostLikedStories(int lowerRangeInclusive, int upperRangeExclusive){
        GmlsInputData inputData = new GmlsInputData(lowerRangeInclusive, upperRangeExclusive);
        gmls.getMostLikedStories(inputData);
    }
}
