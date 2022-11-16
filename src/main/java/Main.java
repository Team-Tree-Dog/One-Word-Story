import adapters.Controller;
import adapters.Presenter;
import adapters.ViewModel;
import entities.LobbyManager;
import entities.Player;
import entities.PlayerFactory;
import entities.games.GameFactory;
import entities.games.GameFactoryRegular;
import usecases.disconnecting.DcInteractor;
import usecases.join_public_lobby.JplInteractor;
import usecases.join_public_lobby.JplOutputBoundary;
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
        Presenter pres = new Presenter(viewM);

        // Factory which accepts ALL display names with at least 3 characters (temporary)
        PlayerFactory playerFac = new PlayerFactory(name -> name.length() > 2);
        GameFactory gameFac = new GameFactoryRegular();
        LobbyManager manager = new LobbyManager(playerFac, gameFac);

        // Start up sort players
        PdInteractor pd = new PdInteractor(pres);
        PgeInteractor pge = new PgeInteractor(pres);
        SpInteractor sp = new SpInteractor(manager, pge, pd);
        sp.startTimer();

        // Use cases called by users
        JplInteractor jpl = new JplInteractor(manager, pres);
        DcInteractor dc = new DcInteractor(manager, pres);
        SwInteractor sw = new SwInteractor(pres, manager);

        Controller controller = new Controller(jpl, dc, sw);

        // TODO: Setup and run the view
    }
}
