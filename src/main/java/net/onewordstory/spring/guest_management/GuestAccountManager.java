package net.onewordstory.spring.guest_management;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.onewordstory.core.usecases.like_story.LsGatewayGuestAccounts;
import net.onewordstory.core.usecases.upvote_title.UtGatewayGuestAccounts;
import org.example.ANSI;
import org.example.Log;
import org.jetbrains.annotations.NotNull;
import org.openjdk.jol.vm.VM;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.UUID;

/**
 * Acts as a spring interceptor and manages guest accounts.
 * A cookie is automatically set to each client to store a uuid of a guest
 * account, which links to an account object in the server (here). The cookies
 * are set to never expire (10 years expire time) while accounts expire within a
 * few days. Every request refreshes the account's expiry time
 */
@Component
public class GuestAccountManager implements HandlerInterceptor, LsGatewayGuestAccounts, UtGatewayGuestAccounts {

    public static final String COOKIE_NAME = "account_id";
    public static final int COOKIE_EXPIRE_TIME_SECONDS = 60 * 60 * 24 * 365 * 10; // 10 years

    private final Map<String, GuestAccount> uuidToAcc = new ConcurrentHashMap<>();
    private final Lock lock = new ReentrantLock();
    private final AtomicBoolean isExpiryWorkerRunning = new AtomicBoolean(false);

    /**
     * Thread which loops the account map and deletes all expired accounts
     */
    private class ExpiryWorker implements Runnable {
        @Override
        public void run() {
            isExpiryWorkerRunning.set(true);
            Log.sendMessage(ANSI.YELLOW, "Expiry Worker",
                    ANSI.CYAN, "Started");

            Set<String> keys = uuidToAcc.keySet();

            for (String k: keys) {
                if (uuidToAcc.get(k).isExpired()) {
                    uuidToAcc.remove(k);
                    Log.sendMessage(ANSI.YELLOW, "Expiry Worker",
                            ANSI.CYAN, "Removing account " + k);
                }
            }

            isExpiryWorkerRunning.set(false);
            Log.sendMessage(ANSI.YELLOW, "Expiry Worker",
                    ANSI.CYAN, "Finished");
        }
    }

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
        Log.sendMessage(ANSI.YELLOW, "Interceptor", ANSI.CYAN,
                "Request Intercepted");
        System.out.println("Path " + request.getRequestURI());

        // uuid of found account or new one made
        String uuid;

        // Puts out a thread to delete expired accounts
        if (!isExpiryWorkerRunning.get()) {
            new Thread(new ExpiryWorker()).start();
        }

        // Look for the account cookie
        Cookie cookie = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals(COOKIE_NAME)) {
                    cookie = c;
                }
            }
        }

        // Make new account and add cookie
        if (cookie == null) {
            uuid = makeAccount();
            response.addCookie(new Cookie("account_id", uuid));
            Log.sendMessage(ANSI.YELLOW, "Interceptor", ANSI.CYAN,
                    "Cookie was null, new account made " + uuid);
        }

        else {
            Log.sendMessage(ANSI.YELLOW, "Interceptor", ANSI.CYAN,
                    "Cookie found with value " + cookie.getValue());

            // Tries to get associated account
            GuestAccount targetAcc = uuidToAcc.get(cookie.getValue());

            // Account doesn't exist or is expired
            if (targetAcc == null || targetAcc.isExpired()) {
                uuid = makeAccount();
                response.addCookie(new Cookie("account_id", uuid));
                Log.sendMessage(ANSI.YELLOW, "Interceptor", ANSI.CYAN,
                        "Account DNE or expired, new account made " + uuid);
            }
            // Account was found
            else {
                uuid = targetAcc.getId();
                targetAcc.touch();
                Log.sendMessage(ANSI.YELLOW, "Interceptor", ANSI.CYAN,
                        "Account touched " + targetAcc.getId());
            }
        }

        // Records account ID for the controller to use to pass to use case
        request.setAttribute("account_id", uuid);

        Log.sendMessage(ANSI.YELLOW, "Interceptor",
                ANSI.CYAN, Arrays.toString(uuidToAcc.keySet().toArray()));
        Log.sendMessage(ANSI.YELLOW, "Interceptor",
                ANSI.CYAN, String.valueOf(VM.current().addressOf(uuidToAcc)));

        return true;
    }

    /**
     * Add a new account to the map and return the uuid
     */
    private String makeAccount() {
        String uuid = UUID.randomUUID().toString();
        uuidToAcc.put(uuid, new GuestAccount(uuid, LocalDateTime.now()));
        return uuid;
    }

    @Override
    public boolean hasLikedStory(String guestAccId, int storyId) {
        Log.sendMessage(ANSI.YELLOW, "hasLikedStory",
                ANSI.CYAN, Arrays.toString(uuidToAcc.keySet().toArray()));
        Log.sendMessage(ANSI.YELLOW, "hasLikedStory",
                ANSI.CYAN, guestAccId);
        Log.sendMessage(ANSI.YELLOW, "hasLikedStory",
                ANSI.CYAN, String.valueOf(VM.current().addressOf(uuidToAcc)));

        GuestAccount acc = uuidToAcc.get(guestAccId);
        if (acc != null) {
            Log.sendMessage(ANSI.YELLOW, "hasLikedStory",
                    ANSI.CYAN, "Account found with ID " + guestAccId);
            return acc.hasLikedStory(storyId);
        }
        return false;
    }

    @Override
    public void setLikedStory(String guestAccId, int storyId) {
        Log.sendMessage(ANSI.YELLOW, "setLikedStory",
                ANSI.CYAN, Arrays.toString(uuidToAcc.keySet().toArray()));
        Log.sendMessage(ANSI.YELLOW, "setLikedStory",
                ANSI.CYAN, guestAccId);
        Log.sendMessage(ANSI.YELLOW, "hasLikedStory",
                ANSI.CYAN, String.valueOf(VM.current().addressOf(uuidToAcc)));

        GuestAccount acc = uuidToAcc.get(guestAccId);
        if (acc != null) {
            Log.sendMessage(ANSI.YELLOW, "setLikedStory",
                    ANSI.CYAN, "Account found with ID " + guestAccId);
            acc.setLikedStory(storyId);
        }
    }

    @Override
    public boolean hasUpvotedTitle(String guestAccountId, int storyId, String title) {
        GuestAccount acc = uuidToAcc.get(guestAccountId);
        if (acc != null) {
            return acc.hasUpvotedTitle(storyId, title);
        }
        return false;
    }

    @Override
    public void setUpvotedTitle(String guestAccountId, int storyId, String title) {
        GuestAccount acc = uuidToAcc.get(guestAccountId);
        if (acc != null) {
            acc.setUpvotedTitle(storyId, title);
        }
    }
}
