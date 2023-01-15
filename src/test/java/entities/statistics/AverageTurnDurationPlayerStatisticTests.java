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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AverageTurnDurationPlayerStatisticTests {

    private static class TestGameReadOnly implements GameReadOnly {

        private Player p;

        public TestGameReadOnly (Player p) {
            this.p = p;
        }

        @Override
        public @NotNull String getStoryString() {
            return null;
        }

        @Override
        public @NotNull Collection<Player> getPlayers() {
            return null;
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
            return p;
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

    private AverageTurnDurationPlayerStatistic stat;

    @BeforeEach
    public void setup() {
        stat = new AverageTurnDurationPlayerStatistic();
    }

    @AfterEach
    public void teardown() {

    }

    @Test
    @Timeout(1000)
    public void testProperTracking() {
        Player p1 = new Player("Anna", "1");
        Player p2 = new Player("Bob", "2");
        Player p3 = new Player("Tim", "3");

        for (int i = 0; i < 10; i++) {
            stat.onTimerUpdate(new TestGameReadOnly(p1));
        }

        for (int i = 0; i < 20; i++) {
            stat.onTimerUpdate(new TestGameReadOnly(p2));
        }

        for (int i = 0; i < 30; i++) {
            stat.onTimerUpdate(new TestGameReadOnly(p3));
        }

        for (int i = 0; i < 30; i++) {
            stat.onTimerUpdate(new TestGameReadOnly(p1));
        }

        for (int i = 0; i < 100; i++) {
            stat.onTimerUpdate(new TestGameReadOnly(p2));
        }

        Map<Player, RecursiveSymboledIntegerHashMap> out = stat.getStatData();

        assertEquals(out.get(p1).get(), new SymboledInteger(20));
        assertEquals(out.get(p2).get(), new SymboledInteger(60));
        assertEquals(out.get(p3).get(), new SymboledInteger(30));
    }
}
