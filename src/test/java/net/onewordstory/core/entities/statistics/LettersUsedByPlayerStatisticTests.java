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

        // Confirms p1 counts
        assertEquals(new SymboledInteger(2), out.get(p1).get("Letters Used").get("B").get());
        assertEquals(new SymboledInteger(2), out.get(p1).get("Letters Used").get("A").get());
        assertEquals(new SymboledInteger(2), out.get(p1).get("Letters Used").get("O").get());
        assertEquals(new SymboledInteger(2), out.get(p1).get("Letters Used").get("F").get());
        assertEquals(new SymboledInteger(1), out.get(p1).get("Letters Used").get("D").get());
        assertEquals(new SymboledInteger(1), out.get(p1).get("Letters Used").get("R").get());

        // Confirms p2 counts
        assertEquals(new SymboledInteger(1), out.get(p2).get("Letters Used").get("F").get());
        assertEquals(new SymboledInteger(1), out.get(p2).get("Letters Used").get("U").get());
        assertEquals(new SymboledInteger(1), out.get(p2).get("Letters Used").get("N").get());
    }

}
