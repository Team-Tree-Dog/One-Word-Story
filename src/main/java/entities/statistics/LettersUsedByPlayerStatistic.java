package entities.statistics;

import entities.Player;
import entities.games.GameReadOnly;
import org.jetbrains.annotations.Nullable;
import util.RecursiveSymboledIntegerHashMap;
import util.SymboledInteger;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Tracks how many times each player used each letter
 */
public class LettersUsedByPlayerStatistic implements PerPlayerIntStatistic {

    private final Map<Player, Map<String, SymboledInteger>> playerLetterCounts;

    public LettersUsedByPlayerStatistic() {
        playerLetterCounts = new HashMap<>();
    }

    /**
     * Reacts to the player's submitted word by adding counts
     * as appropriate to the tracking playerLetterCounts map
     * @param word string word that was added
     * @param author Player object of who submitted this word
     */
    @Override
    public void onSubmitWord(String word, Player author) {
        // Creates a new blank map for this player if they were not previously recorded
        playerLetterCounts.computeIfAbsent(author, k -> new HashMap<>());

        // Gets the player's map, empty or otherwise
        Map<String, SymboledInteger> innerMap = playerLetterCounts.get(author);

        // Goes character by character
        for (String s: word.split("")) {
            s = s.toLowerCase(Locale.ENGLISH);

            // Only count letters, exclude punctuation
            if (s.matches("^[a-z]$")) {

                // If this letter has not been used previous, add it with a count of 1
                if (innerMap.get(s) == null) {
                    innerMap.put(s, new SymboledInteger(1, null));
                }
                // Otherwise, get the current letter count and add 1 to it
                else {
                    innerMap.put(s, innerMap.get(s).add(1));
                }
            }
        }
    }

    @Override
    public void onTimerUpdate(GameReadOnly gameInfo) {}

    @Override
    public void onSuccessfulSwitchTurn(@Nullable Player newCurrentTurnPlayer, int newSecondsLeftInCurrentTurn) {}

    /**
     * @return Players mapped to a 1-deep recursive map containing letters
     * mapped to their counts
     */
    @Override
    public Map<Player, RecursiveSymboledIntegerHashMap> getStatData() {
        Map<Player, RecursiveSymboledIntegerHashMap> output = new HashMap<>();

        // For each player, we want to convert their letter counts map to a recursive map
        for (Player p: playerLetterCounts.keySet()) {
            // Blank recursive map NOT at base case
            RecursiveSymboledIntegerHashMap letterCounts = new RecursiveSymboledIntegerHashMap();

            // Gets this player's letter counts map
            Map<String, SymboledInteger> letterCountsOld = playerLetterCounts.get(p);

            // Loops through letters
            for (String letter: letterCountsOld.keySet()) {
                // Adds a new base case recursive map with the count value and letter as key
                letterCounts.put(letter.toUpperCase(Locale.ENGLISH), new RecursiveSymboledIntegerHashMap(
                        letterCountsOld.get(letter)
                ));
            }

            // Adds outer title
            RecursiveSymboledIntegerHashMap out = new RecursiveSymboledIntegerHashMap();
            out.put("Letters Used", letterCounts);

            // Maps this recursive map to the player
            output.put(p, out);
        }

        return output;
    }
}
