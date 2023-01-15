package entities.statistics;

import entities.Player;
import entities.games.GameReadOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import util.RecursiveSymboledIntegerHashMap;
import util.SymboledInteger;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class AllPlayerNamesStatisticTests {

    private static class TestGameReadOnly implements GameReadOnly {

        private Player[] plys;

        public TestGameReadOnly (Player[] plys) {
            this.plys = plys;
        }

        @Override
        public @NotNull String getStoryString() {
            return null;
        }

        @Override
        public @NotNull Collection<Player> getPlayers() {
            return Arrays.asList(plys);
        }

        @Override
        public @NotNull PerPlayerIntStatistic[] getPlayerStatistics() {
            return new PerPlayerIntStatistic[0];
        }

        @Override
        public @NotNull AllPlayerNamesStatistic getAuthorNamesStatistic() {
            return null;
        }

        @Override
        public @Nullable Player getPlayerById(String playerId) {
            return null;
        }

        @Override
        public @Nullable Player getCurrentTurnPlayer() {
            return null;
        }

        @Override
        public boolean isGameOver() {
            return false;
        }

        @Override
        public boolean isTimerStopped() {
            return false;
        }

        @Override
        public int getSecondsLeftInCurrentTurn() { return 0; }
    }

    private AllPlayerNamesStatistic stat;

    @BeforeEach
    public void setup() {
        stat = new AllPlayerNamesStatistic();
    }

    @AfterEach
    public void teardown() {

    }

    @Test
    @Timeout(1000)
    public void testProperTracking() {
        Player p1 = new Player("Anna", "1");
        Player p2 = new Player("Anna1", "2");
        Player p3 = new Player("Anna2", "3");
        Player p4 = new Player("Anna3", "4");
        Player p5 = new Player("Anna4", "5");
        Player p6 = new Player("Anna5", "6");

        stat.onTimerUpdate(new TestGameReadOnly(new Player[]{p1}));
        stat.onTimerUpdate(new TestGameReadOnly(new Player[]{p1}));
        stat.onTimerUpdate(new TestGameReadOnly(new Player[]{p1, p2}));
        stat.onTimerUpdate(new TestGameReadOnly(new Player[]{p2}));
        stat.onTimerUpdate(new TestGameReadOnly(new Player[]{p2, p3}));
        stat.onTimerUpdate(new TestGameReadOnly(new Player[]{p6}));

        Set<String> out = stat.getStatData();

        assertTrue(out.contains(p1.getDisplayName()));
        assertTrue(out.contains(p2.getDisplayName()));
        assertTrue(out.contains(p3.getDisplayName()));
        assertTrue(out.contains(p6.getDisplayName()));
        assertFalse(out.contains(p4.getDisplayName()));
        assertFalse(out.contains(p5.getDisplayName()));
    }
}
