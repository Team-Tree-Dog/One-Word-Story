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
            sortStoriesByLikes(gatewayOutputData.getStories());
            StoryData[] OUTPUT_STORIES = sortAndExtractStories(gatewayOutputData.getStories(), this.data);
            pres.putStories(new GmlsOutputData(OUTPUT_STORIES));
        }

        /**
         * Comparator class to sort Stories in ascending order.
         */
        public class orderStoriesByLikes implements Comparator<StoryData>{
            /**
             *
             * @param stories1 the first story to be compared.
             * @param stories2 the second story to be compared.
             * @return a value n of type int. n > 0 if stories1 has more likes than stories2,
             * n < 0 if stories 1 has less likes than stories2,
             * n = 0 if stories1 and stories2 have the same number of likes.
             */
            public int compare(StoryData stories1, StoryData stories2){
                return Integer.compare(stories1.getLikes(), stories2.getLikes());
            }
        }

        /**
         * Private helper method to use the comparator class orderStoriesByLikes to sort the stories
         * in descending order.
         * @param stories the stories to be sorted in descending order
         */
        private void sortStoriesByLikes(StoryData[] stories){
            Arrays.sort(stories, (new orderStoriesByLikes()).reversed());
        }

        /**
         * Private Helper Method to sort the stories in descending order and extract the desired range.
         * This is a mutating method and mutates stories by sorting them only (no addition or deletion of stories)
         * @param stories the stories to be sorted and from which the desired range should be extracted
         * @param data the input data specifying the lower and upper bounds for the range
         * @return the range of stories sorted in descending
         */
        private StoryData[] sortAndExtractStories(StoryData[] stories, GmlsInputData data){
            int[] bounds = getIndices(data.getLowerInclusive(), data.getUpperExclusive(), stories.length);
            if (bounds[0] > bounds[1] || stories.length == 0){
                return new StoryData[]{};
            }
            sortStoriesByLikes(stories);
            return Arrays.copyOfRange(stories, bounds[0], bounds[1]);
        }

        /**
         * Private helper method for helper method sortAndExtractStories. Returns the range of List indices
         * corresponding to the lower bound and upper bound of likes
         * @param lower     the lower bound of likes
         * @param upper     the upper bound of likes
         * @param length    the length of the list
         * @return an array of length 2, in which the element at index 0 is the List index corresponding to
         * the lower bound, and the element at index 1 is the List index corresponding to the upper bound
         */
        private int[] getIndices(Integer lower, Integer upper, int length){
            int A = (lower == null)? 0:lower;
            int B = (upper == null)? length:upper;
            return new int[]{Math.max(0,A), Math.min(length,B)};
        }
    }

    /**
     * Starts thread for use case interactor
     */
    public void getLatestStories(GmlsInputData data){
        (new Thread(new GmlsThread(data))).start();
    }
}
