package adapters;

/**
 * Implements output boundaries of all use cases and passes information
 * to the view model through the corresponding output methods. Acts as
 * a bridge between the view model and use cases
 */
public class Presenter {

    private final ViewModel viewM;

    /**
     * @param viewM Instance of the view model to write to
     */
    public Presenter (ViewModel viewM) {
        this.viewM = viewM;
    }
}
