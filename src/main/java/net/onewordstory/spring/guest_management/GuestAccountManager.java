package net.onewordstory.spring.guest_management;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.onewordstory.core.usecases.like_story.LsGatewayGuestAccounts;
import net.onewordstory.core.usecases.upvote_title.UtGatewayGuestAccounts;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class GuestAccountManager implements HandlerInterceptor, LsGatewayGuestAccounts, UtGatewayGuestAccounts {

    public static String COOKIE_NAME = "account_id";

    private final Map<String, GuestAccount> uuidToAcc = new HashMap<>();
    private final Lock lock = new ReentrantLock();

    /**
     *
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler chosen handler to execute (the @GetMapping method for this request)
     * @return true to further process the request (pass to handler) or false to terminate
     */
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) {
        System.out.println("Request INTERCEPTED");

        lock.lock();

        Cookie cookie = null;
        for (Cookie c : request.getCookies()) {
            if (c.getName().equals(COOKIE_NAME)) {
                cookie = c;
            }
        }



        lock.unlock();

        return true;
    }

    @Override
    public boolean hasLikedStory(String guestAccId, int storyId) {
        return false;
    }

    @Override
    public void setLikedStory(String guestAccId, int storyId) {

    }

    @Override
    public boolean hasUpvotedTitle(String guestAccountId, int storyId, String title) {
        return false;
    }

    @Override
    public void setUpvotedTitle(String guestAccountId, int storyId, String title) {

    }
}
