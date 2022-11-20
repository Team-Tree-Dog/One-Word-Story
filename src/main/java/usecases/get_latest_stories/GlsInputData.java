package usecases.get_latest_stories;

/**
 * Input data class of Get Latest Stories use-case
 */
public class GlsInputData {

    private final Integer numToGet;

    /**
     * Constructor for GlsInputData
     * @param numToGet number of stories to get
     */
    public GlsInputData(Integer numToGet) {
        this.numToGet = numToGet;
    }

    /**
     * Getter for GlsInputData
     * @return corresponding maximum number of stories to get
     */
    public int getNumToGet() {
        return numToGet;
    }

}
