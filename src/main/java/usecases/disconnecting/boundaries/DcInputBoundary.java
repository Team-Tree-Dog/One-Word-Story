package usecases.disconnecting.boundaries;

import usecases.disconnecting.data.DcInputData;

/**
 * Input Boundary interface for Disconnecting Use Case
 * It is implemented by DcInteractor
 *
 */
public interface DcInputBoundary {

    /**
     * Disconnects user from the server
     * @param data input data which contains playerId
     */
    void disconnect(DcInputData data);
}
