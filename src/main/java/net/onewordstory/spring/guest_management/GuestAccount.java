package net.onewordstory.spring.guest_management;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GuestAccount {

    public static final int EXPIRE_TIME_SECONDS = 60 * 60 * 24 * 7; // 7 days

    private final String id;
    private final Map<Integer, Set<String>> upvotedTitles;
    private final Set<Integer> likedStories;
    AtomicReference<LocalDateTime> lastTouch = new AtomicReference<>();

    public GuestAccount(String id, LocalDateTime lastTouch) {
        this.id = id;
        this.lastTouch.set(lastTouch);
        likedStories = ConcurrentHashMap.newKeySet();
        upvotedTitles = new ConcurrentHashMap<>();
    }

    /**
     * @return The date time of the last time a user did something using this account
     * (every web request should touch the requester's account)
     */
    public LocalDateTime getLastTouch() { return lastTouch.get(); }

    /**
     * Refresh expiration date by settings the last touch to now
     */
    public void touch() { lastTouch.set(LocalDateTime.now()); }

    /**
     * @return if this account has expired
     */
    public boolean isExpired() {
        return lastTouch.get().until(LocalDateTime.now(), ChronoUnit.SECONDS) > EXPIRE_TIME_SECONDS;
    }

    /**
     * @return ID of this account
     */
    public String getId() { return id; }

    public boolean hasLikedStory(int storyId) {
        return likedStories.contains(storyId);
    }

    public void setLikedStory(int storyId) {
        likedStories.add(storyId);
    }

    public boolean hasUpvotedTitle(int storyId, String title) {
        Set<String> titles = upvotedTitles.get(storyId);
        if (titles != null) {
            return titles.contains(title);
        }
        return false;
    }

    /*TODO: It should be noted that this method is not thread safe in some very rare cases:
    * If the titles set is detected to be null but is subsequently added before the if block
    * begins execution, then the contents will be overwritten. Similarly, if the key is detected
    * to exist but is subsequently removed before the else block executes, the title will fail to
    * be added and an exception will be thrown. A lock encompassing the entire method execution
    * would solve these issues. This error is however very unlikely since the same account should
    * rarely execute concurrent operations.
    * */
    public void setUpvotedTitle(int storyId, String title) {
        Set<String> titles = upvotedTitles.get(storyId);
        if (titles == null) {
            Set<String> set = ConcurrentHashMap.newKeySet();
            set.add(title);
            upvotedTitles.put(storyId, set);
        } else {
            upvotedTitles.get(storyId).add(title);
        }
    }
}
