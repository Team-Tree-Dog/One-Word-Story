package usecases.pull_data;

import entities.Player;
import entities.games.Game;
import entities.games.GameRegular;
import exceptions.InvalidWordException;
import org.junit.After;
import org.junit.Before;
import org.junit.*;
import usecases.GameDTO;
import usecases.PlayerDTO;
import usecases.pull_data.PdInputData;
import usecases.pull_data.PdInteractor;
import usecases.pull_data.PdOutputBoundary;
import usecases.pull_data.PdOutputData;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Testing the Pull-Data use-case. In addition to PdInteractor, testing PdInputBoundary,
 * PdInputData, PdOutputBoundary, PdOutputData, GameDTO, and PlayerDTO
 */
public class PdInteractorTests {

    /**
     * We will run our tests using the following instance of GameRegular,
     * which we instantiate in the setUp
     */
    private final Game g = new GameRegular(new LinkedList<>());

    @Before
    public void setUp() {
        Player p1 = new Player("p1", "1");
        Player p2 = new Player("p2", "2");
        Player p3 = new Player("p3", "3");
        g.addPlayer(p1);
        g.addPlayer(p2);
        g.addPlayer(p3);
        try {
            g.getStory().addWord("lol", p1);
            g.getStory().addWord("kek", p2);
            g.getStory().addWord("haha", p2);
        } catch (InvalidWordException ignored) {}
    }

    @After
    public void tearDown() {
    }

    /**
     * Tests accurate creation of GameDTO and its internal PlayerDTOs based on our GameRegular g.
     * Since we did not redefine equality for those classes, we are checking equality of each
     * attribute directly
     */
    @Test(timeout = 1000)
    public void testRegularGameToGameDTO() {

        // To simulate the presenter, we create PdOutBoundary with overridden method for testing
        PdOutputBoundary pob = new PdOutputBoundary() {
            @Override
            public void updateGameInfo (PdOutputData d) {
                GameDTO obj1 = d.getGameInfo();

                assertEquals("Incorrect copy of Story", obj1.getStory(), g.getStory().toString());

                List<String> IdList = new ArrayList<>();
                for (PlayerDTO p : obj1.getPlayers()) {
                    IdList.add(p.getPlayerId());
                }
                for (Player p : g.getPlayers()) {
                    assertTrue("Missing Player", IdList.contains(p.getPlayerId()));
                }

                assertEquals("Incorrect current turn player id", obj1.getCurrentTurnPlayerId(),
                        g.getCurrentTurnPlayer().getPlayerId());

                assertEquals("Incorrect seconds left in current turn", obj1.getSecondsLeftCurrentTurn(),
                        g.getSecondsLeftInCurrentTurn());
            }
        };

        PdInteractor interactor = new PdInteractor(pob);

        interactor.onTimerUpdate(new PdInputData(g));

    }

}
