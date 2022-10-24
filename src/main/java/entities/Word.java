package entities;

public class Word {
    String word;
    Player author;

    Word(String word, Player a) {
        this.word = word.trim();
    }

    String getWord() { return this.word;}

    Player getAuthor() { return this.author;}
}
