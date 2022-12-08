package entities.statistics;

import entities.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import util.RecursiveSymboledIntegerHashMap;
import util.SymboledInteger;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WordCountPlayerStatisticTests {
    private WordCountPlayerStatistic stat;

    @BeforeEach
    public void setup() {
        stat = new WordCountPlayerStatistic();
    }

    @AfterEach
    public void teardown() {

    }

    @Test
    @Timeout(1000)
    public void testProperTracking() {
        Player p1 = new Player("Anna", "1");

        stat.onSubmitWord(".....example", p1);
        stat.onSubmitWord(".....example", p1);
        stat.onSubmitWord(".....artificial", p1);
        stat.onSubmitWord(".....ok", p1);
        stat.onSubmitWord(".....the", p1);
        stat.onSubmitWord(".....a", p1);
        stat.onSubmitWord(".....of", p1);
        stat.onSubmitWord(".....is", p1);
        stat.onSubmitWord(".....axis", p1);
        stat.onSubmitWord(".....cat", p1);

        Map<Player, RecursiveSymboledIntegerHashMap> out = stat.getStatData();

        assertEquals(out.get(p1).get("Total Words").get(), new SymboledInteger(10));
        assertEquals(out.get(p1).get("< 4 Letter words").get(), new SymboledInteger(6));
        assertEquals(out.get(p1).get("< 3 Letter words").get(), new SymboledInteger(4));
        assertEquals(out.get(p1).get("> 5 Letter words").get(), new SymboledInteger(3));
        assertEquals(out.get(p1).get("Percent < 4 Letter words").get(), new SymboledInteger(60));
        assertEquals(out.get(p1).get("Percent < 3 Letter words").get(), new SymboledInteger(40));
        assertEquals(out.get(p1).get("Percent > 5 Letter words").get(), new SymboledInteger(30));
    }
}
