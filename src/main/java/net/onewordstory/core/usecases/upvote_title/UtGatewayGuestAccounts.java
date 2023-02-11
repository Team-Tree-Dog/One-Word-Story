package net.onewordstory.core.usecases.upvote_title;

public interface UtGatewayGuestAccounts {
    /**
     * @return if the given account has liked a title for a particular story
     */
    boolean hasUpvotedTitle(String guestAccountId, int storyId, String title);

    /**
     * Record that an account has upvoted a particular title
     */
    void setUpvotedTitle(String guestAccountId, int storyId, String title);
}
