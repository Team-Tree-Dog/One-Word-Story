package entities;

import exceptions.InvalidWordException;

import java.util.List;

public class Story {
    List<Word> words;
    WordFactory wordFactory;

    Story(WordFactory wordFactory, Word[] words) {}

    Story(WordFactory wordFactory) {}

    void addWord(String word, Player author) throws InvalidWordException {
        Word newWord = wordFactory.create(word, author);
        this.words.add(newWord);
    }
}
