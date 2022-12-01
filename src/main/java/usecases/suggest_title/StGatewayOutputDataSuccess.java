package usecases.suggest_title;

/**
 * One of two Gateway Output Data for the use case. Records whether the title was successfully added to the database.
 * This output data will be passed to the presenter, and depending on the success or fail, the presenter will
 * update the view model.
 */
public class StGatewayOutputDataSuccess {
    boolean success;

    /**
     * Constructor for the Success Gateway Output Data
     * @param success True if and only if the database has successfully added the title
     */
    public StGatewayOutputDataSuccess(boolean success){this.success = success;}

    public boolean getSuccess(){
        return success;
    }
}
