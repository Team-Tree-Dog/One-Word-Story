package net.onewordstory.core.frameworks_drivers.views;

/**
 * Defines a rough structure for a view. Call runApplicationLoop
 * to run the view. This will trigger 3 abstract method in order:
 * start, run, end.
 * Start should typically be used for initialization. Run should
 * have some form of loop to keep the program running until it is closed.
 * End should run teardown procedures.
 */
public abstract class View {

    /**
     * Initialize your view. This is the method which should boot up all the clean architecture
     */
    public abstract void start ();

    /**
     * Run the core logic of your view
     */
    public abstract void run ();

    /**
     * Teardown your view's resources
     */
    public abstract void end ();

    /**
     * Start and run the application.
     */
    public void runApplicationLoop() {
        start();
        run();
        end();
    };
}
