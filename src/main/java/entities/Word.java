package entities;

public class Word {
    private String word;
    private final Player author;

    public Word(String word, Player author) {
        this.word = word.trim();
        this.author = author;
    }

    public String getWord() { return this.word;}

    public Player getAuthor() { return this.author;}
}
