package adapters.display_data.title_data;

public class SuggestedTitleDisplayDataBuilder {

    private String title;
    private int upvotes;

    public SuggestedTitleDisplayData build() {
        if (title == null) {
            throw new IllegalStateException("building with missing data!");
        }
        return new SuggestedTitleDisplayData(title, upvotes);
    }

    public int getUpvotes() { return upvotes; }

    public String getTitle() { return title; }

    public SuggestedTitleDisplayDataBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public SuggestedTitleDisplayDataBuilder setUpvotes(int upvotes) {
        this.upvotes = upvotes;
        return this;
    }
}
