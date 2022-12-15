import adapters.controllers.*;
import adapters.view_models.PdViewModel;
import adapters.view_models.PgeViewModel;
import adapters.view_models.SsViewModel;
import com.example.springapp.SpringApp;
import frameworks_drivers.views.CoreAPI;
import frameworks_drivers.views.View;
import org.example.ANSI;
import org.example.Log;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SpringBootView extends View {

    private BufferedReader reader;
    private ConfigurableApplicationContext app;

    public SpringBootView(CoreAPI api) {
        super(api);
    }

    /**
     * Start spring application and console input reader
     */
    @Override
    public void start(CoreAPI coreAPI) {
        reader = new BufferedReader(new InputStreamReader(System.in));
        app = SpringApp.startServer(coreAPI, new String[0]);
    }

    /**
     * read console for commands on loop. Spring is already running on its own threads.
     * Terminate loop if "shutdown" command issues
     */
    @Override
    public void run(CoreAPI coreAPI) {
        while (true) {
            try {
                String inp = reader.readLine();

                if (inp.equals("s")) {
                    break;
                } else {
                    System.out.println("\"" + inp + "\" is not a valid command");
                }
            } catch (IOException e) {
                break;
            }
        }
    }

    /**
     * Call shutdown use case which runs synchronously. Then close
     * the spring application and console reader.
     */
    @Override
    public void end(CoreAPI coreAPI) {
        // Calls shutdown
        Log.sendMessage("SPRING VIEW", ANSI.PURPLE, "Initiating Shutdown Use Case...");
        SsViewModel ssViewM = coreAPI.ssController.shutdownServer();
        Log.sendMessage("SPRING VIEW", ANSI.PURPLE, "Use Cases have been shut down!");

        app.close();
        try {
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Log.sendMessage("SPRING VIEW", ANSI.PURPLE, "Spring has closed");
    }
}
