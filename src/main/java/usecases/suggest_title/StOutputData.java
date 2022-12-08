package usecases.suggest_title;
import usecases.Response;

/**
 * The Output Data for this use case. Contains the ID corresponding to this particular title suggestion request
 * and a Response containing the response code and description to be sent to the presenter.
 */
public class StOutputData {
    private Response res;

    /**
     * The constructor for this output data object.
     * @param res       the response object for this use case that will be sent to the presenter.
     */
    public StOutputData(Response res){
        this.res = res;
    }

    public Response getRes() {return res;}
}
