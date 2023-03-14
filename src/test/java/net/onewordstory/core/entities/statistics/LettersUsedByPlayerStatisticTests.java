package net.onewordstory.core.entities.statistics;

import net.onewordstory.core.entities.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import net.onewordstory.core.util.RecursiveSymboledIntegerHashMap;
import net.onewordstory.core.util.SymboledInteger;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class LettersUsedByPlayerStatisticTests {

    private LettersUsedByPlayerStatistic stat;

    @BeforeEach
    public void setup() {
        stat = new LettersUsedByPlayerStatistic();
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

        stat.onSubmitWord("badoof", p1);
        stat.onSubmitWord("barf", p1);
        stat.onSubmitWord("fun", p2);

        Map<Player, RecursiveSymboledIntegerHashMap> out = stat.getStatData();

        // Confirms p3 isn't on record
        assertNull(out.get(p3));

        // TODO: Either fix these tests, or rework the stat.getStatData() method to give every player RSIHM a value (null right now)

        // Confirms p1 counts
        assertEquals(new SymboledInteger(2), out.get(p1).get("b").get());
        assertEquals(new SymboledInteger(2), out.get(p1).get("a").get());
        assertEquals(new SymboledInteger(2), out.get(p1).get("o").get());
        assertEquals(new SymboledInteger(2), out.get(p1).get("f").get());
        assertEquals(new SymboledInteger(1), out.get(p1).get("d").get());
        assertEquals(new SymboledInteger(1), out.get(p1).get("r").get());

        // Confirms p2 counts
        assertEquals(new SymboledInteger(1), out.get(p2).get("f").get());
        assertEquals(new SymboledInteger(1), out.get(p2).get("u").get());
        assertEquals(new SymboledInteger(1), out.get(p2).get("n").get());
    }

}
