package frameworks_drivers.views;

/**
 * Defines a rough structure for a view. Call runApplicationLoop
 * to run the view. This will trigger 3 abstract method in order:
 * start, run, end.
 * Start should typically be used for initialization. Run should
 * have some form of loop to keep the program running until it is closed.
 * End should run teardown procedures.
 */
public abstract class View {

    private final CoreAPI api;

    /**
     * @param api access to core API of the clean arch program
     */
    public View(CoreAPI api) {
        this.api = api;
    }

    /**
     * Initialize your view
     */
    public abstract void start (CoreAPI coreAPI);

    /**
     * Run the core logic of your view
     */
    public abstract void run (CoreAPI coreAPI);

    /**
     * Teardown your view's resources
     */
    public abstract void end (CoreAPI coreAPI);

    /**
     * Start and run the application.
     */
    public void runApplicationLoop() {
        start(api);
        run(api);
        end(api);
    };
}
