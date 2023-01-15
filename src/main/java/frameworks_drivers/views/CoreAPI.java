package frameworks_drivers.views;

import adapters.controllers.*;
import adapters.view_models.PdViewModel;
import adapters.view_models.PgeViewModel;

/**
 * This object is the API for accessing the core application. That is,
 * it contains all of the controller instances as well as the PD and PGE
 * view models
 */
public class CoreAPI {
    public final CagController cagController;
    public final DcController dcController;
    public final GatController gatController;
    public final GlsController glsController;
    public final GmlsController gmlsController;
    public final GsbiController gsbiController;
    public final GscController gscController;
    public final JplController jplController;
    public final LsController lsController;
    public final SsController ssController;
    public final UtController utController;
    public final SwController swController;
    public final StController stController;
    public final PgeViewModel pgeViewM;
    public final PdViewModel pdViewM;

    public CoreAPI(
            CagController cagController,
            DcController dcController,
            GatController gatController,
            GlsController glsController,
            GmlsController gmlsController,
            GsbiController gsbiController,
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
        this.gsbiController = gsbiController;
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
}
