package usecases.suggest_title;
import usecases.Response;

/**
 * The Output Data for this use case. Contains the ID corresponding to this particular title suggestion request
 * and a Response containing the response code and description to be sent to the presenter.
 */
public class StOutputData {
    String requestID;
    Response res;

    /**
     * The constructor for this output data object.
     * @param requestID the ID that is used to track this specific request by a user to suggest a story title
     * @param res       the response object for this use case that will be sent to the presenter.
     */
    public StOutputData(String requestID, Response res){
        this.requestID = requestID;
        this.res = res;
    }

    public Response getRes() {return res;}

    public String getRequestID() {return requestID;}
}
