package usecases.get_most_liked_stories;

import usecases.StoryData;

import java.util.Arrays;
import java.util.*;

public class GmlsInteractor {
    private GmlsOutputBoundary pres;
    private GmlsGateway repo;

    /**
     * Constructor for use case interactor
     * @param pres the output boundary to update the view model
     * @param repo the repository from which the stories will be extracted
     */
    public GmlsInteractor(GmlsOutputBoundary pres, GmlsGateway repo){
        this.pres = pres;
        this.repo = repo;
    }

    /**
     * Thread for use case interactor
     */
    public class GmlsThread implements Runnable {
        GmlsInputData data;

        /**
         * Constructor for GmlsThread class
         * @param data Input data for the use case
         */
        public GmlsThread(GmlsInputData data){
            this.data = data;
        }

        /**
         * Runs thread for use case
         * This is a mutating method that mutates the stories in the repo by sorting them only
         * (no addition or deletion of stories)
         */
        @Override
        public void run() {
            GmlsGatewayOutputData gatewayOutputData = repo.getAllStories();
            StoryData[] OUTPUT_STORIES = sortAndExtractStories(gatewayOutputData.getStories(), this.data);
            pres.putStories(new GmlsOutputData(OUTPUT_STORIES));
        }

        /**
         * Comparator class to sort Stories in descending order.
         */
        public class orderStoriesByLikes implements Comparator<StoryData>{
            /**
             *
             * @param stories1 the first story to be compared.
             * @param stories2 the second story to be compared.
             * @return a value n of type int. n > 0 if stories1 has less likes than stories2,
             * n < 0 if stories 1 has more likes than stories2,
             * n = 0 if stories1 and stories2 have the same number of likes.
             */
            public int compare(StoryData stories1, StoryData stories2){
                return Integer.compare(stories2.getLikes(), stories1.getLikes());
            }
        }

        /**
         * Private helper method to use the comparator class orderStoriesByLikes to sort the stories
         * in descending order.
         * @param stories the stories to be sorted in descending order
         */
        private void sortStoriesByLikes(StoryData[] stories){
            orderStoriesByLikes COMPARATOR = new orderStoriesByLikes();
            Arrays.sort(stories, COMPARATOR);
        }

        /**
         * Private Helper Method to sort the stories in descending order and extract the desired range.
         * This is a mutating method and mutates stories by sorting them only (no addition or deletion of stories)
         * @param stories the stories to be sorted and from which the desired range should be extracted
         * @param data the input data specifying the lower and upper bounds for the range
         * @return the range of stories sorted in descending
         */
        private StoryData[] sortAndExtractStories(StoryData[] stories, GmlsInputData data){
            sortStoriesByLikes(stories);
            int[] INDICES = getIndices(stories, data.getLowerInclusive(), data.getUpperExclusive());
            return Arrays.copyOfRange(stories, INDICES[0], INDICES[1]);
        }

//        /**
//         * Private helper method for helper method getIndices. Returns the range of likes
//         * corresponding to the range of likes specified by the InputData
//         * @param lower     the lower bound for likes specified by the user, can be null
//         * @param upper     the upper bound for likes specified by the user, can be null
//         * @param length    the length of the list
//         * @return an array of length 2, for which:
//         * 1. the element at index 0 is a new lower bound for likes that is
//         *    within 0 to length inclusive, and is equal to 0 if lower == null
//         * 2. the element at index 1 is a new upper bound for likes that is
//         *    within 0 to length inclusive, and is equal to length if upper == null
//         */
//        private int[] getBoundsInArrayRange(Integer lower, Integer upper, int length){
//            int A = (lower == null)? 0:lower;
//            int B = (upper == null)? length:upper;
//            return new int[]{Math.max(0,A), Math.min(length,B)};
//        }

        /**
         * Private helper method
         * @param story the story for which the number of likes are compare to the bounds
         * @param lower the lower bound for likes specified by the user, can be null, in which case it corresponds
         *              to a lower bound of 0 likes
         * @param upper the upper bounds for likes specified by the user, can be null, in which case it corresponds
         *              to no upper bound on the desired range of likes
         * @return      True if and only if the number of likes in this story is within the bounds
         *              specified by the user
         */
        private boolean inBounds(StoryData story, Integer lower, Integer upper){
            boolean WITHIN_UPPER_BOUND;
            boolean WITHIN_LOWER_BOUND;
            if (lower == null){ WITHIN_LOWER_BOUND = (story.getLikes() >= 0); }
            else {WITHIN_LOWER_BOUND = (story.getLikes() >= lower);}
            if (upper == null){ WITHIN_UPPER_BOUND = true;}
            else {WITHIN_UPPER_BOUND = (story.getLikes() < upper);}
            return (WITHIN_LOWER_BOUND && WITHIN_UPPER_BOUND);
        }

        /**
         * Private helper method for helper method sortAndExtractStories. Returns an array containing the indices
         * that specify the desired range of stories in the StoryData[] object stories.
         * Precondition: stories is sorted in descending order of likes
         * @param stories the Collection of stories for which to determine the indices
         * @param upper   the lower bound for likes specified by the user, can be null
         * @param lower   the upper bound for likes specified by the user, can be null
         * @return n array of length 2, for which:
         * 1. the element at index 0 is the 'start' index for the subarray of stories that we want to extract
         * 2. the element at index 1 is the 'to' index for the subarray of stories that we want to extract
         * The method returns [0,0] if there are no stories that fall within the bounds
         */
        private int[] getIndices(StoryData[] stories, Integer lower , Integer upper){
            int INDEX_LOWER = 0;
            int INDEX_UPPER = 0;
            // the following loop iterates from the start of the array till we find an story
            // whose number of likes is within the bounds specified by the user
            for (int i = 0; i < stories.length; i++){
                if (inBounds(stories[i], lower, upper)){
                    INDEX_LOWER = i;
                    break; }
            }
            // the following loop iterates from the end of the array till we find an story
            // whose number of likes is within the bounds specified by the user
            for (int i = stories.length-1; i >= 0; i--){
                if (inBounds(stories[i], lower, upper)){
                    INDEX_UPPER = i+1;
                    break; }
            }
            return new int[]{INDEX_LOWER, INDEX_UPPER};
        }
    }

    /**
     * Starts thread for use case interactor
     */
    public void getLatestStories(GmlsInputData data){
        (new Thread(new GmlsThread(data))).start();
    }
}
