package usecases.pull_data;

import entities.Player;
import entities.ValidityChecker;
import entities.games.Game;
import exceptions.InvalidWordException;
import org.junit.After;
import org.junit.Before;
import org.junit.*;
import usecases.GameDTO;
import usecases.PlayerDTO;

import static org.junit.Assert.*;

import java.util.*;


/**
 * Testing the Pull-Data use-case. In addition to PdInteractor, testing PdInputBoundary,
 * PdInputData, PdOutputBoundary, PdOutputData, GameDTO, and PlayerDTO
 */
public class PdInteractorTests {

    /**
     * We will run our tests using a local implementation of Game
     */

    private static class CustomizableTestGame extends Game {

        private final Queue<Player> players = new LinkedList<>();

        private final boolean allowAddingPlayers;

        /**
         * @param allowAddingPlayers Does addPlayer successfully add the player and return true
         */
        public CustomizableTestGame(boolean allowAddingPlayers) {
            super(99, new ValidityChecker() {
                @Override
                public boolean isValid(String word) {
                    return true;
                }
            });
            this.allowAddingPlayers = allowAddingPlayers;
        }

        @Override
        public Collection<Player> getPlayers() {
            return players;
        }

        @Override
        public boolean isGameOver() {
            return true;
        }

        @Override
        public void onTimerUpdate() {

        }

        @Override
        public Player getPlayerById(String PlayerId) {
            return null;
        }

        @Override
        public boolean removePlayer(Player playerToRemove) {
            return false;
        }

        @Override
        public boolean addPlayer(Player playerToAdd) {
            if (allowAddingPlayers) {
                players.add(playerToAdd);
                return true;
            } return false;
        }

        @Override
        public boolean switchTurn() {
            return false;
        }

        @Override
        public Player getCurrentTurnPlayer() {
            return this.players.peek();
        }
    }

    private final Game g = new CustomizableTestGame(true);

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
        } catch (InvalidWordException ignored) {
            // This error is completely impossible
        }
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