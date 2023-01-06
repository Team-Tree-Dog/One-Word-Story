package usecases.get_most_liked_stories;

import usecases.*;

import java.util.Arrays;
import java.util.*;

public class GmlsInteractor implements GmlsInputBoundary {
    private final GmlsGatewayStory storyRepo;
    private final GmlsGatewayTitles titlesRepo;

    /**
     * The ThreadRegister that keeps track of all the running use case threads
     * for the shutdown-server use case
     */
    private final ThreadRegister register;

    /**
     * Constructor for use case interactor
     *
     * @param storyRepo the repository from which the stories will be extracted
     */
    public GmlsInteractor(GmlsGatewayStory storyRepo, GmlsGatewayTitles titlesRepo, ThreadRegister register) {
        this.storyRepo = storyRepo;
        this.titlesRepo = titlesRepo;
        this.register = register;
    }

    /**
     * Thread for use case interactor
     */
    public class GmlsThread extends InterruptibleThread {
        GmlsInputData data;
        GmlsOutputBoundary pres;

        /**
         * Constructor for GmlsThread class
         *
         * @param data Input data for the use case
         * @param pres Output boundary for the use case
         */
        public GmlsThread(GmlsInputData data, GmlsOutputBoundary pres) {
            super(GmlsInteractor.this.register, pres);
            this.data = data;
            this.pres = pres;
        }

        /**
         * Runs thread for use case
         * This is a mutating method that mutates the stories in the repo by sorting them only
         * (no addition or deletion of stories)
         */
        @Override
        public void threadLogic() {
            RepoRes<StoryRepoData> res = storyRepo.getAllStories();

            // DB Has failed to get the stories
            if (!res.isSuccess()) {
                pres.putStories(null,
                        res.getRes());
            }

            // DB Has gotten the stories
            else {
                StoryRepoData[] stories = res.getRows().toArray(new StoryRepoData[0]);

                StoryRepoData[] outputStories = sortAndExtractStories(stories, this.data);

                List<FullStoryDTO> fullStoryDTOs = new ArrayList<>();
                for (StoryRepoData story: outputStories) {
                    RepoRes<String> titleOut = titlesRepo.getMostUpvotedStoryTitle(story.getStoryId());

                    fullStoryDTOs.add(new FullStoryDTO(
                            titleOut.getRes().getCode() == Response.ResCode.SUCCESS ?
                                    titleOut.getRows().get(0) : null,
                            story
                    ));
                }

                pres.putStories(fullStoryDTOs,
                        Response.getSuccessful("Stories successfully extracted"));
            }
        }

        /**
         * Comparator class to sort Stories in descending order.
         */
        public class orderStoriesByLikes implements Comparator<StoryRepoData> {
            /**
             * @param stories1 the first story to be compared.
             * @param stories2 the second story to be compared.
             * @return a value n of type int:
             * n > 0 if stories1 has less likes than stories2,
             * n < 0 if stories 1 has more likes than stories2,
             * n = 0 if stories1 and stories2 have the same number of likes.
             */
            public int compare(StoryRepoData stories1, StoryRepoData stories2) {
                int COMPARE_LIKES = Integer.compare(stories2.getLikes(), stories1.getLikes());
                if (COMPARE_LIKES == 0) {
                    return stories2.getPublishTimeStamp().compareTo(stories1.getPublishTimeStamp());
                }
                return COMPARE_LIKES;
            }
        }

        /**
         * Private helper method to use the comparator class orderStoriesByLikes to sort the stories
         * in descending order.
         *
         * @param stories the stories to be sorted in descending order
         */
        private void sortStoriesByLikes(StoryRepoData[] stories) {
            orderStoriesByLikes COMPARATOR = new orderStoriesByLikes();
            Arrays.sort(stories, COMPARATOR);
        }

        /**
         * Private Helper Method to sort the stories in descending order and extract the desired range.
         * This is a mutating method and mutates stories by sorting them only (no addition or deletion of stories)
         *
         * @param stories the stories to be sorted and from which the desired range should be extracted
         * @param data    the input data specifying the lower and upper bounds for the range
         * @return the range of stories sorted in descending
         */
        private StoryRepoData[] sortAndExtractStories(StoryRepoData[] stories, GmlsInputData data) {
            sortStoriesByLikes(stories);
            int[] INDICES = getIndices(stories, data.getLowerInclusive(), data.getUpperExclusive());
            return Arrays.copyOfRange(stories, INDICES[0], INDICES[1]);
        }

        /**
         * Private helper method
         *
         * @param index the story for which the number of likes are compare to the bounds
         * @param lower the lower bound for the range of stories specified by the user, can be null,
         *              in which case it corresponds to a lower bound of 0 likes
         * @param upper the upper bounds for the range of stories specified by the user, can be null,
         *              in which case it corresponds to no upper bound on the desired range of likes
         * @return True if and only if the number of likes in this story is within the bounds
         * specified by the user
         */
        private boolean inBounds(int index, Integer lower, Integer upper) {
            boolean WITHIN_UPPER_BOUND;
            boolean WITHIN_LOWER_BOUND;
            if (lower == null) {
                WITHIN_LOWER_BOUND = (index >= 0);
            } else {
                WITHIN_LOWER_BOUND = (index >= lower);
            }
            if (upper == null) {
                WITHIN_UPPER_BOUND = true;
            } else {
                WITHIN_UPPER_BOUND = (index < upper);
            }
            return (WITHIN_LOWER_BOUND && WITHIN_UPPER_BOUND);
        }

        /**
         * Private helper method for helper method sortAndExtractStories. Returns an array containing the indices
         * that specify the desired range of stories in the StoryData[] object stories.
         * Precondition: stories is sorted in descending order of likes
         *
         * @param stories the Collection of stories for which to determine the indices
         * @param upper   the lower bound for the range of stories specified by the user, can be null
         * @param lower   the upper bound for the range of stories specified by the user, can be null
         * @return n array of length 2, for which:
         * 1. the element at index 0 is the 'start' index for the subarray of stories that we want to extract
         * 2. the element at index 1 is the 'to' index for the subarray of stories that we want to extract
         * The method returns [0,0] if there are no stories that fall within the bounds
         */
        private int[] getIndices(StoryRepoData[] stories, Integer lower, Integer upper) {
            int INDEX_LOWER = 0;
            int INDEX_UPPER = 0;
            // the following loop iterates from the start of the array till we find an story
            // whose number of likes is within the bounds specified by the user
            for (int i = 0; i < stories.length; i++) {
                if (inBounds(i, lower, upper)) {
                    INDEX_LOWER = i;
                    break;
                }
            }
            // the following loop iterates from the end of the array till we find an story
            // whose number of likes is within the bounds specified by the user
            for (int i = stories.length - 1; i >= 0; i--) {
                if (inBounds(i, lower, upper)) {
                    INDEX_UPPER = i + 1;
                    break;
                }
            }
            return new int[]{INDEX_LOWER, INDEX_UPPER};
        }
    }

    /**
     * Starts thread for use case interactor
     */
    public void getMostLikedStories(GmlsInputData data, GmlsOutputBoundary pres) {
        InterruptibleThread thread = new GmlsThread(data, pres);
        if (!register.registerThread(thread)) {
            pres.outputShutdownServer();
        }
    }

}
