package usecases.disconnecting;

import usecases.Response;

/**
 * Output Data for Disconnecting Use Case
 */
public class DcOutputData {
     Response response;

    /**
     * Constructor for DcOutputData
     * @param response Response which reports whether disconnecting was successful or not
     */
    public DcOutputData(Response response) {
         this.response = response;
     }
}
