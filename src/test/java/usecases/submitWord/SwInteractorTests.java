package usecases.submitWord;

import entities.*;
import entities.games.Game;
import entities.games.GameFactory;
import entities.games.GameFactoryRegular;
import exceptions.GameRunningException;
import exceptions.IdInUseException;
import org.junit.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

public class SwInteractorTests {
    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Tests creating Game, no timer, but the player submits word out of turn.
     */
    @Test(timeout = 10000)
    public void testOutOfTurn() throws IdInUseException, GameRunningException {

        class LocalDisplayName implements DisplayNameChecker {
            @Override
            public boolean checkValid(String displayName) {
                return false;
            }
        }

        PlayerFactory playerFac = new PlayerFactory(new LocalDisplayName());
        GameFactory gameFac = new GameFactoryRegular();
        LobbyManager lobman = new LobbyManager(playerFac, gameFac);
        PlayerPoolListener ppl = new PlayerPoolListener() {
            @Override
            public void onJoinGamePlayer(Game game) {}

            @Override
            public void onCancelPlayer() {}
        };

        Player player1 = lobman.createNewPlayer("player1", "1");
        Player player2 = lobman.createNewPlayer("player2", "2");

        lobman.addPlayerToPool(player1, ppl);
        lobman.addPlayerToPool(player2, ppl);

        Game currGame = lobman.newGameFromPool(new HashMap<String, Integer>());
        lobman.setGame(currGame);

        assertTrue("Player 1 is not in the Game", currGame.getPlayers().contains(player1));
        assertTrue("Player 2 is not in the Game", currGame.getPlayers().contains(player2));

        assertEquals("It should be Player 1's turn, but it isn't.", player1, currGame.getCurrentTurnPlayer());

        SwOutputBoundary pres = new SwOutputBoundary() {
            @Override
            public void valid(SwOutputDataValidWord outputDataValidWord) throws Exception {
                throw new Exception("THIS ISN'T WHAT IT'S SUPPOSED TO GO TO");
            }

            @Override
            public void invalid(SwOutputDataFailure outputDataFailure) {}
        };



        }

    /**
     * Tests creating Game, no timer, but the player submits word when they don't exist.
     */
    @Test(timeout = 10000)
    public void testPlayerNotFound() {
        // TODO: Create Test, don't assert throw
    }

    /**
     * Tests submitting a word to a non-existent Game.
     */
    @Test(timeout = 10000)
    public void testGameDoesntExist() {
        // TODO: Create Test, don't assert throw
    }

    /**
     * Tests submitting a word that is invalid.
     */
    @Test(timeout = 10000)
    public void testInvalidWord() {
        // TODO: Create Test, don't assert throw
        // TODO: Use a new inner Validity Checker which will always return false, causing the word to be invalid.
    }

    /**
     * Tests submitting a word that is valid.
     */
    @Test(timeout = 10000)
    public void testValidWord() {
        // TODO: Create Test
        // TODO: Use a new inner Validity Checker which will always return true, causing the word to be valid.
    }
}
