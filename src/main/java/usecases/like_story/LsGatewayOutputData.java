package usecases.like_story;

public class LsGatewayOutputData {

    private final boolean success;

    /**
     * @param success Specifies whether the like was added successfully
     * */
    public LsGatewayOutputData(boolean success) {
        this.success = success;
    }

    /**
     * @return Returns whether the like was added
     * */
    public boolean isSuccess() {
        return success;
    }

}
