package net.onewordstory.core;

import net.onewordstory.core.frameworks_drivers.views.SpringBootView;

/**
 * Orchestrator. Contains only a main method which initializes the clean architecture
 */
public class Main {

    /**
     * Builds the clean arch structure and initializes it.
     * @param args Command line arguments (currently none necessary)
     */
    public static void main (String[] args) {
        // Setup and run the view
        new SpringBootView().runApplicationLoop();
    }
}
