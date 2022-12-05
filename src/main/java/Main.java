import adapters.ViewModel;
import adapters.controllers.*;
import adapters.presenters.*;
import entities.LobbyManager;
import entities.PlayerFactory;
import entities.display_name_checkers.DisplayNameChecker;
import entities.display_name_checkers.DisplayNameCheckerBasic;
import entities.games.GameFactory;
import entities.games.GameFactoryRegular;
import usecases.RepoRes;
import usecases.Response;
import usecases.StoryRepoData;
import usecases.ThreadRegister;
import usecases.disconnecting.DcInteractor;
import usecases.get_latest_stories.GlsInteractor;
import usecases.get_most_liked_stories.GmlsInteractor;
import usecases.join_public_lobby.JplInteractor;
import usecases.like_story.LsGatewayStory;
import usecases.like_story.LsInteractor;
import usecases.pull_data.PdInteractor;
import usecases.pull_game_ended.PgeInteractor;
import usecases.shutdown_server.SsInteractor;
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

        ThreadRegister register = new ThreadRegister();

        // Create all presenters
        DcPresenter dcPresenter = new DcPresenter(viewM);
        GlsPresenter glsPresenter = new GlsPresenter(viewM);
        GmlsPresenter gmlsPresenter = new GmlsPresenter(viewM);
        JplPresenter jplPresenter = new JplPresenter(viewM);
        LsPresenter lsPresenter = new LsPresenter(viewM);
        PdPresenter pdPresenter = new PdPresenter(viewM);
        PgePresenter pgePresenter = new PgePresenter(viewM);
        SsPresenter ssPresenter = new SsPresenter(viewM);
        SwPresenter swPresenter = new SwPresenter(viewM);


        // Create desired display name checker for injection
        DisplayNameChecker displayChecker = new DisplayNameCheckerBasic();

        // Factory which accepts ALL display names with at least 3 characters (temporary)
        PlayerFactory playerFac = new PlayerFactory(displayChecker);
        GameFactory gameFac = new GameFactoryRegular();

        // Inject particular factories into LobbyManager
        LobbyManager manager = new LobbyManager(playerFac, gameFac);

        // Start up sort players
        PdInteractor pd = new PdInteractor(pdPresenter);
        PgeInteractor pge = new PgeInteractor(pgePresenter);
        SpInteractor sp = new SpInteractor(manager, pge, pd);
        sp.startTimer();

        // Use cases called by users

        DcInteractor dc = new DcInteractor(manager, dcPresenter, register);
        GlsInteractor gls = new GlsInteractor(glsPresenter,
                () -> new RepoRes<StoryRepoData>(Response.getFailure("Dummy Lambda, Always failure")),
                register); // TODO: Inject repo
        GmlsInteractor gmls = new GmlsInteractor(gmlsPresenter,
                () -> new RepoRes<StoryRepoData>(Response.getFailure("Dummy Lambda, Always failure")),
                register); // TODO: Inject repo
        JplInteractor jpl = new JplInteractor(manager, jplPresenter, register);
        LsInteractor ls = new LsInteractor(lsPresenter,
                storyId -> Response.getSuccessful("Dummy Lambda, Always successful"),
                register); // TODO: Inject repo
        SsInteractor ss = new SsInteractor(register, ssPresenter);
        SwInteractor sw = new SwInteractor(swPresenter, manager, register);


        // Controllers
        DcController dcController = new DcController(dc);
        GlsController glsController = new GlsController(gls);
        GmlsController gmlsController = new GmlsController(gmls);
        JplController jplController = new JplController(jpl);
        LsController lsController = new LsController(ls);
        SsController ssController = new SsController(ss);
        SwController swController = new SwController(sw);

        // TODO: Setup and run the view
    }
}
