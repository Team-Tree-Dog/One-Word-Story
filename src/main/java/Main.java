import adapters.ViewModel;
import adapters.controllers.*;
import adapters.presenters.*;
import entities.LobbyManager;
import entities.PlayerFactory;
import entities.suggested_title_checkers.SuggestedTitleChecker;
import entities.suggested_title_checkers.SuggestedTitleCheckerBasic;
import entities.display_name_checkers.DisplayNameChecker;
import entities.display_name_checkers.DisplayNameCheckerBasic;
import entities.games.GameFactory;
import entities.games.GameFactoryRegular;
<<<<<<< HEAD
=======
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
>>>>>>> main
import usecases.*;
import usecases.disconnecting.DcInteractor;
import usecases.get_all_titles.GatInteractor;
import usecases.get_latest_stories.GlsInteractor;
import usecases.get_most_liked_stories.GmlsInteractor;
import usecases.join_public_lobby.JplInteractor;
import usecases.like_story.LsInteractor;
import usecases.pull_data.PdInteractor;
import usecases.pull_game_ended.PgeInteractor;
import usecases.shutdown_server.SsInteractor;
import usecases.sort_players.SpInteractor;
import usecases.submit_word.SwInteractor;
import usecases.suggest_title.StGateway;
import usecases.suggest_title.StInteractor;

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
        GatPresenter gatPresenter = new GatPresenter(viewM);
        GlsPresenter glsPresenter = new GlsPresenter(viewM);
        GmlsPresenter gmlsPresenter = new GmlsPresenter(viewM);
        JplPresenter jplPresenter = new JplPresenter(viewM);
        LsPresenter lsPresenter = new LsPresenter(viewM);
        PdPresenter pdPresenter = new PdPresenter(viewM);
        PgePresenter pgePresenter = new PgePresenter(viewM);
        SsPresenter ssPresenter = new SsPresenter(viewM);
        SwPresenter swPresenter = new SwPresenter(viewM);
        StPresenter stPresenter = new StPresenter(viewM);


        // Create desired display name checker for injection
        DisplayNameChecker displayChecker = new DisplayNameCheckerBasic();

        // Create desired story title checker for injection
        SuggestedTitleChecker titleChecker = new SuggestedTitleCheckerBasic();

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
        GatInteractor gat = new GatInteractor(gatPresenter,
                storyId -> new RepoRes<TitleRepoData>(Response.getFailure("Dummy Lambda, Always failure"))
                ,register);
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
        StInteractor st = new StInteractor(stPresenter, new StGateway() {
            @Override
            public @NotNull Response suggestTitle(int storyId, @Nullable String titleSuggestion) {
                return null;
            }

            @Override
            public @NotNull RepoRes<TitleRepoData> getAllTitles(int storyId) {
                return null;
            }
        }, titleChecker, register);


        // Controllers
        DcController dcController = new DcController(dc);
        GatController gatController = new GatController();
        GlsController glsController = new GlsController(gls);
        GmlsController gmlsController = new GmlsController(gmls);
        JplController jplController = new JplController(jpl);
        LsController lsController = new LsController(ls);
        SsController ssController = new SsController(ss);
        SwController swController = new SwController(sw);
        StController stController = new StController(st);

        // TODO: Setup and run the view
    }
}
