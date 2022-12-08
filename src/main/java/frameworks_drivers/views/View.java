package frameworks_drivers.views;

import adapters.controllers.*;

import java.io.IOException;

/**
 * TODO: ADD DOC
 */
public abstract class View {

    protected final CagController cagController;
    protected final DcController dcController;
    protected final GatController gatController;
    protected final GlsController glsController;
    protected final GmlsController gmlsController;
    protected final GscController gscController;
    protected final JplController jplController;
    protected final LsController lsController;
    protected final SsController ssController;
    protected final UtController utController;
    protected final SwController swController;
    protected final StController stController;

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
            UtController utController
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
    }

    /**
     * TODO ADD DOC
     */
    public abstract void start ();

    /**
     * TODO: ADD DOC
     */
    public abstract void run ();

    /**
     * TODO: ADD DOC
     */
    public abstract void end ();

    /**
     * TODO: ADD DOC
     */
    public void runApplicationLoop() {
        start();
        run();
        end();
    };
}
