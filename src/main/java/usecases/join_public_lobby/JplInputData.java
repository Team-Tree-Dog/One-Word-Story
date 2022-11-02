package usecases.join_public_lobby;

public class JplInputData {

    private final String displayName;
    private final String id;

    public JplInputData(String displayName, String id) {
        this.displayName = displayName;
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getId() {
        return id;
    }
}
