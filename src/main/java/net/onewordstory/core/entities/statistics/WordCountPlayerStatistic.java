package net.onewordstory.core.entities.statistics;

import net.onewordstory.core.entities.Player;
import net.onewordstory.core.entities.games.GameReadOnly;
import org.jetbrains.annotations.Nullable;
import net.onewordstory.core.util.RecursiveSymboledIntegerHashMap;
import net.onewordstory.core.util.SymboledInteger;

import java.util.HashMap;
import java.util.Map;

/**
 * Tracks various word count information per player
 */
public class WordCountPlayerStatistic implements PerPlayerIntStatistic {

    private final Map<Player, WordCountPlayerStatisticData> perPlayerData;

    public WordCountPlayerStatistic() {
        perPlayerData = new HashMap<>();
    }

    @Override
    public void onSubmitWord(String word, Player author) {
        // Trim and remove anything non-letter
        word = word.trim().replaceAll("[^a-zA-Z]","");

        // Add new data object if missing, then get the player's data
        perPlayerData.computeIfAbsent(author, k -> new WordCountPlayerStatisticData());
        WordCountPlayerStatisticData dat = perPlayerData.get(author);

        // Tracks counts about specific words, or word count in general
        dat.totalWords++;
        if (word.length() > 5) {
            dat.moreThanFiveLetterWords++;
        }
        if (word.length() < 3) {
            dat.lessThanThreeLetterWords++;
        }
        if (word.length() < 4) {
            dat.lessThanFourLetterWords++;
        }
        if (dat.isFiller(word)) {
            dat.fillerWords++;
        }

    }

    @Override
    public void onTimerUpdate(GameReadOnly gameInfo) {}

    @Override
    public void onSuccessfulSwitchTurn(@Nullable Player newCurrentTurnPlayer, int newSecondsLeftInCurrentTurn) {}

    @Override
    public Map<Player, RecursiveSymboledIntegerHashMap> getStatData() {
        Map<Player, RecursiveSymboledIntegerHashMap> output = new HashMap<>();

        for (Player p: perPlayerData.keySet()) {
            WordCountPlayerStatisticData oldDat = perPlayerData.get(p);

            // Single depth recursive map to store the above properties
            RecursiveSymboledIntegerHashMap newDat = new RecursiveSymboledIntegerHashMap();

            newDat.put("Total Words",
                    new RecursiveSymboledIntegerHashMap(new SymboledInteger(oldDat.totalWords)));
            newDat.put("< 4 Letter words",
                    new RecursiveSymboledIntegerHashMap(new SymboledInteger(oldDat.lessThanFourLetterWords)));
            newDat.put("< 3 Letter words",
                    new RecursiveSymboledIntegerHashMap(new SymboledInteger(oldDat.lessThanThreeLetterWords)));
            newDat.put("> 5 Letter words",
                    new RecursiveSymboledIntegerHashMap(new SymboledInteger(oldDat.moreThanFiveLetterWords)));

            newDat.put("Percent < 4 Letter words",
                    new RecursiveSymboledIntegerHashMap(
                            new SymboledInteger(
                                    Math.round(((float) oldDat.lessThanFourLetterWords /
                                            ((float) oldDat.totalWords)) * 100)
                            )));
            newDat.put("Percent < 3 Letter words",
                    new RecursiveSymboledIntegerHashMap(
                            new SymboledInteger(
                                    Math.round(((float) oldDat.lessThanThreeLetterWords /
                                            ((float) oldDat.totalWords)) * 100)
                            )));
            newDat.put("Percent > 5 Letter words",
                    new RecursiveSymboledIntegerHashMap(
                            new SymboledInteger(
                                    Math.round(((float) oldDat.moreThanFiveLetterWords /
                                            ((float) oldDat.totalWords)) * 100)
                            )));
            newDat.put("Percent Filler Words",
                    new RecursiveSymboledIntegerHashMap(
                            new SymboledInteger(
                                    Math.round(((float) oldDat.fillerWords /
                                            ((float) oldDat.totalWords)) * 100)
                            )));

            output.put(p, newDat);
        }

        return output;
    }
}
