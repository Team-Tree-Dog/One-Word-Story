import adapters.ViewModel;
import adapters.controllers.DcController;
import adapters.controllers.JplController;
import adapters.controllers.SwController;
import adapters.presenters.*;
import entities.LobbyManager;
import entities.PlayerFactory;
import entities.games.GameFactory;
import entities.games.GameFactoryRegular;
import usecases.disconnecting.DcInteractor;
import usecases.join_public_lobby.JplInteractor;
import usecases.pull_data.PdInteractor;
import usecases.pull_game_ended.PgeInteractor;
import usecases.sort_players.SpInteractor;
import usecases.submit_word.SwInteractor;

/**
 * Orchestrator. Contains only a main method which boots up
 * the server.
 */
public class Main {

    /**
     * Builds the clean arch structure and initializes it.
     * @param args Command line arguments (currently none necessary)
     */
    public static void main (String[] args) {
        ViewModel viewM = new ViewModel();

        // Create all presenters
        DcPresenter dcPresenter = new DcPresenter(viewM);
        JplPresenter jplPresenter = new JplPresenter(viewM);
        PdPresenter pdPresenter = new PdPresenter(viewM);
        PgePresenter pgePresenter = new PgePresenter(viewM);
        SwPresenter swPresenter = new SwPresenter(viewM);

        // Factory which accepts ALL display names with at least 3 characters (temporary)
        PlayerFactory playerFac = new PlayerFactory(name -> name.length() > 2);
        GameFactory gameFac = new GameFactoryRegular();
        // Inject particular factories into LobbyManager
        LobbyManager manager = new LobbyManager(playerFac, gameFac);

        // Start up sort players
        PdInteractor pd = new PdInteractor(pdPresenter);
        PgeInteractor pge = new PgeInteractor(pgePresenter);
        SpInteractor sp = new SpInteractor(manager, pge, pd);
        sp.startTimer();

        // Use cases called by users
        JplInteractor jpl = new JplInteractor(manager, jplPresenter);
        DcInteractor dc = new DcInteractor(manager, dcPresenter);
        SwInteractor sw = new SwInteractor(swPresenter, manager);

        // Controllers
        JplController jplController = new JplController(jpl);
        DcController dcController = new DcController(dc);
        SwController swController = new SwController(sw);

        // TODO: Setup and run the view
    }
}
