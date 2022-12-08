package usecases.disconnecting;

/**
 * Input Boundary interface for Disconnecting Use Case
 * It is implemented by DcInteractor
 *
 */
public interface DcInputBoundary {

    /**
     * Disconnects user from the server
     * @param data input data which contains playerId
     * @param pres output boundary for this use case
     */
    void disconnect(DcInputData data, DcOutputBoundary pres);
}
