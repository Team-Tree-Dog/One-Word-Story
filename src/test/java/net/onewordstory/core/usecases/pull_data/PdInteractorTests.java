package net.onewordstory.core.usecases.pull_data;

import net.onewordstory.core.entities.Player;
import net.onewordstory.core.entities.games.Game;
import net.onewordstory.core.entities.validity_checkers.ValidityCheckerFacade;
import net.onewordstory.core.exceptions.InvalidWordException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import net.onewordstory.core.usecases.GameDTO;
import net.onewordstory.core.usecases.PlayerDTO;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


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

        public static final ValidityCheckerFacade v = new ValidityCheckerFacade(
                puncValidityChecker -> "",
                wordValidityChecker -> ""
        );

        /**
         * @param allowAddingPlayers Does addPlayer successfully add the player and return true
         */
        public CustomizableTestGame(boolean allowAddingPlayers) {
            super(99, v);
            this.allowAddingPlayers = allowAddingPlayers;
        }

        @Override
        public @NotNull Collection<Player> getPlayers() {
            return players;
        }

        @Override
        public boolean isGameOver() {
            return true;
        }

        @Override
        public void onTimerUpdateLogic() {

        }

        @Override
        public Player getPlayerById(String playerId) {
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
        public boolean switchTurnLogic() {
            return false;
        }

        @Override
        public @NotNull Player getCurrentTurnPlayer() {
            return this.players.peek();
        }

    }

    private final Game g = new CustomizableTestGame(true);

    @BeforeEach
    public void setUp() {
        Player p1 = new Player("p1", "1");
        Player p2 = new Player("p2", "2");
        Player p3 = new Player("p3", "3");
        g.addPlayer(p1);
        g.addPlayer(p2);
        g.addPlayer(p3);
        try {
            g.addWord("lol", p1);
            g.addWord("kek", p2);
            g.addWord("haha", p2);
        } catch (InvalidWordException ignored) {
            // This error is completely impossible
        }
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Tests accurate creation of GameDTO and its internal PlayerDTOs based on our GameRegular g.
     * Since we did not redefine equality for those classes, we are checking equality of each
     * attribute directly
     */
    @Test
    @Timeout(1)
    public void testRegularGameToGameDTO() {

        // To simulate the presenter, we create PdOutBoundary with overridden method for testing
        PdOutputBoundary pob = d -> {
            GameDTO obj1 = d.getGameInfo();

            assertEquals(obj1.getStory(), g.getStoryString(), "Incorrect copy of Story");

            List<String> IdList = new ArrayList<>();
            for (PlayerDTO p : obj1.getPlayers()) {
                IdList.add(p.getPlayerId());
            }
            for (Player p : g.getPlayers()) {
                assertTrue(IdList.contains(p.getPlayerId()), "Missing Player");
            }

            assertEquals(obj1.getCurrentTurnPlayerId(), g.getCurrentTurnPlayer().getPlayerId(),
                    "Incorrect current turn player id");

            assertEquals(obj1.getSecondsLeftCurrentTurn(), g.getSecondsLeftInCurrentTurn(),
                    "Incorrect seconds left in current turn");
        };

        PdInteractor interactor = new PdInteractor(pob);

        interactor.onTimerUpdate(new PdInputData(g));

    }

}
