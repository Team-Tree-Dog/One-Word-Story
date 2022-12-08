import adapters.controllers.*;
import adapters.view_models.SsViewModel;
import com.example.springapp.SpringApp;
import frameworks_drivers.views.View;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SpringBootView extends View {

    private static SpringBootView singeltonInstance = null;

    /**
     * Initialize singleton static instance
     */
    public static void init(CagController cagController, DcController dcController, GatController gatController,
                       GlsController glsController, GmlsController gmlsController, GscController gscController,
                       JplController jplController, LsController lsController, SsController ssController,
                       StController stController, SwController swController, UtController utController) {
        SpringBootView.singeltonInstance = new SpringBootView(cagController, dcController, gatController, glsController, gmlsController, gscController,
                jplController, lsController, ssController, stController, swController, utController);
    }

    /**
     * @return the static singleton instance
     */
    public static SpringBootView getInstance() {
        if(singeltonInstance == null) {
            throw new IllegalStateException("Invalid state! View is uninitialized");
        }

        return singeltonInstance;
    }

    private BufferedReader reader;
    private ConfigurableApplicationContext app;

    /**
     * SMELLY CODE
     */
    private SpringBootView(CagController cagController, DcController dcController, GatController gatController,
                          GlsController glsController, GmlsController gmlsController, GscController gscController,
                          JplController jplController, LsController lsController, SsController ssController,
                          StController stController, SwController swController, UtController utController) {
        super(cagController, dcController, gatController, glsController, gmlsController, gscController,
                jplController, lsController, ssController, stController, swController, utController);
    }

    @Override
    public void start() {
        reader = new BufferedReader(new InputStreamReader(System.in));
        app = SpringApp.startServer(this, new String[0]);
    }

    @Override
    public void run() {
        while (true) {
            try {
                String inp = reader.readLine();

                if (inp.equals("shutdown")) {
                    break;
                } else {
                    System.out.println("\"" + inp + "\" is not a valid command");
                }
            } catch (IOException e) {
                break;
            }
        }
    }

    @Override
    public void end() {
        // Calls shutdown
        SsViewModel ssViewM = ssController.shutdownServer();
        //System.out.println("Success of Shutdown: " + ssViewM.getShutdown());

        app.close();
        try {
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
