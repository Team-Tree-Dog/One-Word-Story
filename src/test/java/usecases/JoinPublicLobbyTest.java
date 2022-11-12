package usecases;

import entities.DisplayNameChecker;
import entities.LobbyManager;
import entities.Player;
import entities.PlayerFactory;
import entities.games.Game;
import entities.games.GameFactory;
import entities.games.GameFactoryRegular;
import entities.games.GameRegular;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import usecases.join_public_lobby.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class JoinPublicLobbyTest {

    private final TestOutputBoundary testOutputBoundary = new TestOutputBoundary();
    private final SimpleDisplayNameChecker simpleDisplayNameChecker = new SimpleDisplayNameChecker();
    private final GameFactory gameFactory = new GameFactoryRegular();
    private final PlayerFactory playerFactory = new PlayerFactory(simpleDisplayNameChecker);
    private JplInteractor interactor;

    private static class TestOutputBoundary implements JplOutputBoundary {

        List<JplOutputDataResponse> joinedPoolResponses = new CopyOnWriteArrayList<>();
        List<JplOutputDataJoinedGame> joinedGameResponses = new CopyOnWriteArrayList<>();
        List<JplOutputDataResponse> cancelledResponses = new CopyOnWriteArrayList<>();

        @Override
        public void inPool(JplOutputDataResponse dataJoinedPool) {
            joinedPoolResponses.add(dataJoinedPool);
        }

        @Override
        public void inGame(JplOutputDataJoinedGame dataJoinedGame) {
            joinedGameResponses.add(dataJoinedGame);
        }

        @Override
        public void cancelled(JplOutputDataResponse dataCancelled) {
            cancelledResponses.add(dataCancelled);
        }
    }

    private static class SimpleDisplayNameChecker implements DisplayNameChecker {
        @Override
        public boolean checkValid(String displayName) {
            return true;
        }
    }

    @BeforeEach
    public void setupJplInteractor(){
        LobbyManager lobbyManager = new LobbyManager(this.playerFactory, this.gameFactory);
        this.interactor = new JplInteractor(lobbyManager, this.testOutputBoundary);
    }

    @Test
    @Timeout(value = 10000, unit = TimeUnit.MILLISECONDS)
    public void checkAllJoinPoolQueriesRegistered() {
        Player firstPlayer = new Player("First", "1");
        Player secondPlayer = new Player("Second", "2");
        Set<String> expectedIds = new HashSet<>();
        expectedIds.add(firstPlayer.getPlayerId());
        expectedIds.add(secondPlayer.getPlayerId());

        JplInputData firstInputData = new JplInputData(firstPlayer.getDisplayName(), firstPlayer.getPlayerId());
        JplInputData secondInputData = new JplInputData(secondPlayer.getDisplayName(), secondPlayer.getPlayerId());

        this.interactor.joinPublicLobby(firstInputData);
        this.interactor.joinPublicLobby(secondInputData);
        while (this.testOutputBoundary.joinedPoolResponses.size() < 2) {
            Thread.onSpinWait();
        }
        Assertions.assertEquals(2, this.testOutputBoundary.joinedPoolResponses.size());
        Assertions.assertEquals(0, this.testOutputBoundary.joinedGameResponses.size());
        Assertions.assertEquals(0, this.testOutputBoundary.cancelledResponses.size());

        Set<String> actualIds = new HashSet<>();
        for(JplOutputDataResponse response : this.testOutputBoundary.joinedPoolResponses) {
            Assertions.assertEquals(response.getRes().getCode(), Response.ResCode.SUCCESS);
            actualIds.add(response.getPlayerId());
        }
        Assertions.assertIterableEquals(expectedIds, actualIds);
    }

    @Test
    public void checkAllJoinedGame() {
        Player firstPlayer = new Player("Name", "1");
        Player secondPlayer = new Player("Name", "2");
        JplInputData inputDataFirst = new JplInputData(firstPlayer.getDisplayName(), firstPlayer.getPlayerId());
        JplInputData inputDataSecond = new JplInputData(secondPlayer.getDisplayName(), secondPlayer.getPlayerId());
        JplInteractor.JplThread threadFirst = this.interactor.new JplThread(inputDataFirst);
        JplInteractor.JplThread threadSecond = this.interactor.new JplThread(inputDataSecond);

        Queue<Player> initialPlayers = new LinkedList<>();
        initialPlayers.add(firstPlayer);
        initialPlayers.add(secondPlayer);
        Game game = new GameRegular(initialPlayers);

        threadFirst.onJoinGamePlayer(game);
        threadSecond.onJoinGamePlayer(game);
        threadFirst.run();
        threadSecond.run();
        Assertions.assertEquals(2, this.testOutputBoundary.joinedPoolResponses.size());
        Assertions.assertEquals(2, this.testOutputBoundary.joinedGameResponses.size());
        Assertions.assertEquals(0, this.testOutputBoundary.cancelledResponses.size());
    }


    @Test
    public void testCancelWaiting() {
        Player firstPlayer = new Player("Name", "1");
        Player secondPlayer = new Player("Name", "2");
        Player thirdPlayer = new Player("Busy", "3");
        JplInputData inputDataFirst = new JplInputData(firstPlayer.getDisplayName(), firstPlayer.getPlayerId());
        JplInputData inputDataSecond = new JplInputData(secondPlayer.getDisplayName(), secondPlayer.getPlayerId());
        JplInputData inputDataThird = new JplInputData(thirdPlayer.getDisplayName(), thirdPlayer.getPlayerId());
        JplInteractor.JplThread threadFirst = this.interactor.new JplThread(inputDataFirst);
        JplInteractor.JplThread threadSecond = this.interactor.new JplThread(inputDataSecond);
        JplInteractor.JplThread threadThird = this.interactor.new JplThread(inputDataThird);

        threadThird.onCancelPlayer();
        threadThird.run();

        Queue<Player> initialPlayers = new LinkedList<>();
        initialPlayers.add(firstPlayer);
        initialPlayers.add(secondPlayer);
        Game game = new GameRegular(initialPlayers);

        threadFirst.onJoinGamePlayer(game);
        threadSecond.onJoinGamePlayer(game);
        threadFirst.run();
        threadSecond.run();
        Assertions.assertEquals(3, this.testOutputBoundary.joinedPoolResponses.size());
        Assertions.assertEquals(2, this.testOutputBoundary.joinedGameResponses.size());
        Assertions.assertEquals(1, this.testOutputBoundary.cancelledResponses.size());
    }


    @Test
    public void testDuplicateIds(){
        Player firstPlayer = new Player("Player", "1");
        Player secondPlayer = new Player("player", "1");
        JplInputData inputDataFirst = new JplInputData(firstPlayer.getDisplayName(), firstPlayer.getPlayerId());
        JplInputData inputDataSecond = new JplInputData(secondPlayer.getDisplayName(), secondPlayer.getPlayerId());
        JplInteractor.JplThread threadFirst = this.interactor.new JplThread(inputDataFirst);
        JplInteractor.JplThread threadSecond = this.interactor.new JplThread(inputDataSecond);
        Queue<Player> initialPlayers = new LinkedList<>();
        initialPlayers.add(firstPlayer);
        initialPlayers.add(secondPlayer);
        Game game = new GameRegular(initialPlayers);

        threadFirst.onJoinGamePlayer(game);
        threadSecond.onJoinGamePlayer(game);
        threadFirst.run();
        threadSecond.run();
        Assertions.assertEquals(2, this.testOutputBoundary.joinedPoolResponses.size());
        Assertions.assertEquals(1, this.testOutputBoundary.joinedGameResponses.size());
        Assertions.assertEquals(0, this.testOutputBoundary.cancelledResponses.size());
        int expectedFailNumber = 1;
        int actualFailNumber = 0;
        for(JplOutputDataResponse response : this.testOutputBoundary.joinedPoolResponses) {
            if(response.getRes().getCode() == Response.ResCode.ID_IN_USE) {
                actualFailNumber++;
            }
        }
        Assertions.assertEquals(expectedFailNumber, actualFailNumber);
    }

}
