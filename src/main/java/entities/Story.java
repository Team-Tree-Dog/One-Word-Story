package entities;

import exceptions.InvalidWordException;

import java.util.List;

public class Story {
    List<Word> words;
    WordFactory wordFactory;

    public Story(WordFactory wordFactory, Word[] words) {}

    public Story(WordFactory wordFactory) {}

    public void addWord(String word, Player author) throws InvalidWordException {
        Word newWord = wordFactory.create(word, author);
        this.words.add(newWord);
    }
}
