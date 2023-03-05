package net.onewordstory.core.usecases.like_story;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import net.onewordstory.core.usecases.Response;
import net.onewordstory.core.usecases.ThreadRegister;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LsTests {

    private TestLsPresenter presenter;
    private TestLsGateway repository;
    private LsInputBoundary interactor;
    private Random random;

    private ThreadRegister register;


    @BeforeEach
    public void setup() {
        presenter = new TestLsPresenter();
        repository = new TestLsGateway();
        register = new ThreadRegister();
        interactor = new LsInteractor(repository, register);
        random = new Random();
    }

    /**
     * This test checks that all the likes are added correctly
     * */
    @Test
    @Timeout(10000)
    public void testLikesAreAddedCorrectly() {
        int numberOfStories = random.nextInt(100);
        for(int i = 0; i < numberOfStories; i++) {
            repository.addStoryId(i);
            int numberOfLikes = random.nextInt(9) + 1;
            int currentNumberOfResponses = presenter.responses.size();
            for (int j = 0; j < numberOfLikes; j++) {
                LsInputData inputData = new LsInputData(i);
                interactor.likeStory(inputData, presenter);
            }
            while (repository.getNumberOfLikesForStory(i) < numberOfLikes) {
                Thread.onSpinWait();
            }
            Assertions.assertEquals(repository.getNumberOfLikesForStory(i), numberOfLikes);
            while (presenter.responses.size() < currentNumberOfResponses + numberOfLikes) {
                Thread.onSpinWait();
            }
            Assertions.assertEquals(currentNumberOfResponses + numberOfLikes,
                    presenter.responses.size());
                    }
        for (LsOutputData data : presenter.responses) {
            Assertions.assertEquals(data.getResponse().getCode(), Response.ResCode.SUCCESS);
        }
    }

    /**
     * This test checks that the use case works correctly with non-existing stories
     * */
    @Test
    @Timeout(10000)
    public void testInteractorReturnsFailForIncorrectStories() {
        int numberOfStories = random.nextInt(100);
        for (int i = 0; i < numberOfStories; i++) {
            LsInputData inputData = new LsInputData(i);
            interactor.likeStory(inputData, presenter);
        }
        while (presenter.responses.size() < numberOfStories) {
            Thread.onSpinWait();
        }
        for (LsOutputData data : presenter.responses) {
            Assertions.assertEquals(data.getResponse().getCode(), Response.ResCode.FAIL);
        }
    }

    /**
     * The test presenter that just stores the responses
     * */
    private static class TestLsPresenter implements LsOutputBoundary {

        private final List<LsOutputData> responses = new CopyOnWriteArrayList<>();

        @Override
        public void likeOutput(LsOutputData data) {
            responses.add(data);
        }

        @Override
        public void outputShutdownServer() {
            throw new RuntimeException("This method is not implemented and should not be called");
        }

    }

    /**
     * This is a simple runtime storage that keeps the stories` likes in a map
     * */
    private static class TestLsGateway implements LsGatewayStory {

        private final Map<Integer, Integer> storyToLikes = new HashMap<>();
        private final Lock lock = new ReentrantLock();

        @Override
        public @NotNull Response likeStory(int storyId) {
            lock.lock();
            boolean success = true;
            Integer numberOfLikes = storyToLikes.get(storyId);
            if (numberOfLikes == null) {
                success = false;
            } else {
                storyToLikes.put(storyId, numberOfLikes + 1);
            }
            lock.unlock();
            return success ? Response.getSuccessful("") : Response.getFailure("");
        }

        public void addStoryId(int storyId) {
            lock.lock();
            storyToLikes.put(storyId, 0);
            lock.unlock();
        }

        public int getNumberOfLikesForStory(int storyId) {
            lock.lock();
            Integer result = storyToLikes.get(storyId);
            if (result == null) {
                lock.unlock();
                return 0;
            }
            lock.unlock();
            return result;
        }

    }
}
