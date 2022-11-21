package usecases.get_most_liked_stories;

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
            int[] INDICES = getIndices(stories, data.getLowerInclusive(), data.getUpperExclusive());
            if (INDICES[0] > INDICES[1] || stories.length == 0){
                return new StoryData[]{};
            }
            sortStoriesByLikes(stories);
            return Arrays.copyOfRange(stories, INDICES[0], INDICES[1]);
        }

        /**
         * Private helper method for helper method getIndices. Returns the range of likes
         * corresponding to the range of likes specified by the InputData
         * @param lower     the lower bound for likes specified by the user, can be null
         * @param upper     the upper bound for likes specified by the user, can be null
         * @param length    the length of the list
         * @return an array of length 2, for which:
         * 1. the element at index 0 is a new lower bound for likes that is
         *    within 0 to length inclusive, and is equal to 0 if lower == null
         * 2. the element at index 1 is a new upper bound for likes that is
         *    within 0 to length inclusive, and is equal to length if upper == null
         */
        private int[] getBoundsInArrayRange(Integer lower, Integer upper, int length){
            int A = (lower == null)? 0:lower;
            int B = (upper == null)? length:upper;
            return new int[]{Math.max(0,A), Math.min(length,B)};
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
         */
        private int[] getIndices(StoryData[] stories, Integer lower , Integer upper){
            int[] BOUNDS = getBoundsInArrayRange(lower, upper, stories.length);
            //The method returns [0,0] if the upper bound (exclusive) is greater than or equal to the lower bound
            //(inclusive)
            if (BOUNDS[0] >= BOUNDS[1]){return new int[]{0,0};}
            int INDEX_LOWER = stories.length;
            int INDEX_UPPER = 0;
            // the following loop iterates from the start of the array till we find an story with
            // likes >= the lower bound
            for (int i = 0; i < stories.length; i++){
                if (stories[i].getLikes() >= BOUNDS[0]){
                    INDEX_LOWER = i;
                    break; }
            }
            // the following loop iterates from the end of the array till we find an story with
            // likes < the upper bound
            for (int i = stories.length-1; i >= 0; i--){
                if (stories[i].getLikes() < BOUNDS[1]){
                    INDEX_UPPER = i+1;
                    break; }
            }
            //The method returns [0,0] if the list does not lie in the desired range of likes
            if (INDEX_LOWER == stories.length || INDEX_UPPER == 0){ return new int[]{0,0};}
            int[] INDICES = {INDEX_LOWER, INDEX_UPPER};
            return INDICES;
        }
    }

    /**
     * Starts thread for use case interactor
     */
    public void getLatestStories(GmlsInputData data){
        (new Thread(new GmlsThread(data))).start();
    }
}
