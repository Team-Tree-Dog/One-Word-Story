package usecases.sort_players;

import entities.DisplayNameChecker;
import entities.LobbyManager;
import entities.PlayerFactory;
import entities.games.GameFactoryRegular;
import org.junit.After;
import org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import usecases.join_public_lobby.*;
import usecases.pull_data.PdInteractor;
import usecases.pull_data.PdOutputBoundary;
import usecases.pull_data.PdOutputData;

/**
 *
 */
public class SpInteractorTests {

    /**
     *
     */
    @Before
    public void setup () {

    }

    /**
     *
     */
    @After
    public void teardown () {

    }

    /**
     * Test the scenario where two players are in the pool. Sort players
     * should in this case empty the players from the pool into a new game
     * and start the game timer.
     */
    @Test(timeout=1000)
    public void testTwoPlayersInPoolStartGame () {
        PdInteractor pd = new PdInteractor(new PdOutputBoundary() {
            @Override
            public void updateGameInfo(PdOutputData d) {

            }
        });
        PgeInteractor pge = new PgeInteractor(new PgeOutputBoundary() {
            @Override
            public void notifyGameEnded (PgeOutputData d) {

            }
        });
        LobbyManager m = new LobbyManager(new PlayerFactory(new DisplayNameChecker() {
            @Override
            public boolean checkValid(String displayName) {
                return true;
            }
        }), new GameFactoryRegular());

        SpInteractor sp = new SpInteractor(m, pge, pd);

        SpInteractor.SpTask spTimerTask = sp.new SpTask();
    }

    /**
     * Test the scenario where the game is not null but is over. Sort players
     * should set the game to null
     */
    @Test(timeout=1000)
    public void testGameNotNullIsOverSetNull () {

    }

    /**
     * Test the scenario where the game is not null and not over and players
     * are in the pool. Sort players should try to add the players to the game
     * and remove them from the pool. The players will be successfully added due
     * to the game implementation used for this test
     */
    @Test(timeout=1000)
    public void testGameRunningPlayersInPool () {

    }
}
