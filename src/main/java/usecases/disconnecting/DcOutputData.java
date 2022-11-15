package usecases.disconnecting;

import usecases.Response;

/**
 * Output Data for Disconnecting Use Case
 */
public class DcOutputData {
     private final Response response;

    /**
     * Constructor for DcOutputData
     * @param response Response which reports whether disconnecting was successful or not
     */
    public DcOutputData(Response response) {
         this.response = response;
     }

    /**
     * @return the response object in this data
     */
    public Response getResponse() {
        return response;
    }
}
