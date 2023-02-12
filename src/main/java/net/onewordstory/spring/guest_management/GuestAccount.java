package net.onewordstory.spring.guest_management;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GuestAccount {

    private final String id;
    private LocalDateTime lastTouch;
    private final Map<Integer, Set<String>> upvotedTitles;
    private final Set<Integer> likedStories;

    public GuestAccount(String id, LocalDateTime lastTouch) {
        this.id = id;
        this.lastTouch = lastTouch;
        likedStories = new HashSet<>();
        upvotedTitles = new HashMap<>();
    }

    /**
     * @return The date time of the last time a user did something using this account
     * (every web request should touch the requester's account)
     */
    public LocalDateTime getLastTouch() { return lastTouch; }

    /**
     * @return ID of this account
     */
    public String getId() { return id; }
}
