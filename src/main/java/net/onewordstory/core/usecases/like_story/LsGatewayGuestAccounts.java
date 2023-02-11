package net.onewordstory.core.usecases.like_story;

public interface LsGatewayGuestAccounts {
    /**
     * @return if a guest account has liked the story
     */
    boolean hasLikedStory(String guestAccId, int storyId);

    /**
     * Record that an account liked a story
     */
    void setLikedStory(String guestAccId, int storyId);
}
