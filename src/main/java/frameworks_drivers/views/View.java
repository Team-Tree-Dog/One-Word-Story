package frameworks_drivers.views;

import adapters.controllers.*;
import adapters.view_models.PdViewModel;
import adapters.view_models.PgeViewModel;
import entities.LobbyManager;

import java.io.IOException;

/**
 * TODO: ADD DOC
 */
public abstract class View {

    public final CagController cagController;
    public final DcController dcController;
    public final GatController gatController;
    public final GlsController glsController;
    public final GmlsController gmlsController;
    public final GscController gscController;
    public final JplController jplController;
    public final LsController lsController;
    public final SsController ssController;
    public final UtController utController;
    public final SwController swController;
    public final StController stController;
    public final PgeViewModel pgeViewM;
    public final PdViewModel pdViewM;

    /**
     * SMELLY CODE
     */
    public View (
            CagController cagController,
            DcController dcController,
            GatController gatController,
            GlsController glsController,
            GmlsController gmlsController,
            GscController gscController,
            JplController jplController,
            LsController lsController,
            SsController ssController,
            StController stController,
            SwController swController,
            UtController utController,
            PgeViewModel pgeViewM,
            PdViewModel pdViewM
    ) {
        this.cagController = cagController;
        this.dcController = dcController;
        this.gatController = gatController;
        this.glsController = glsController;
        this.gmlsController = gmlsController;
        this.gscController = gscController;
        this.jplController = jplController;
        this.lsController = lsController;
        this.ssController = ssController;
        this.utController = utController;
        this.swController = swController;
        this.stController = stController;
        this.pgeViewM = pgeViewM;
        this.pdViewM = pdViewM;
    }

    /**
     * TODO ADD DOC
     */
    public abstract void start ();

    /**
     * TODO: ADD DOC
     */
    public abstract void run (LobbyManager lm);

    /**
     * TODO: ADD DOC
     */
    public abstract void end ();

    /**
     * TODO: ADD DOC
     */
    public void runApplicationLoop(LobbyManager lm) {
        start();
        run(lm);
        end();
    };
}
