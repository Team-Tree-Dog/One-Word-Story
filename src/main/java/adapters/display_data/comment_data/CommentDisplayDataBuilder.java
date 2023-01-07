package adapters.display_data.comment_data;

public class CommentDisplayDataBuilder {
    private String displayName;
    private String content;

    public CommentDisplayData build() {
        if (displayName == null || content == null) {
            throw new IllegalStateException("building with missing data!");
        }
        return new CommentDisplayData(displayName, content);
    }

    public String getDisplayName() { return displayName; }

    public String getContent() { return content; }

    public CommentDisplayDataBuilder setDisplayName(String newName) {
        displayName = newName;
        return this;
    }

    public CommentDisplayDataBuilder setContent(String content) {
        this.content = content;
        return this;
    }
}
