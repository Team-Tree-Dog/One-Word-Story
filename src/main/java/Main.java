import adapters.controllers.*;
import adapters.presenters.*;
import com.example.springapp.SpringappApplication;
import adapters.view_models.PdViewModel;
import adapters.view_models.PgeViewModel;
import entities.LobbyManager;
import entities.PlayerFactory;
import entities.comment_checkers.CommentChecker;
import entities.comment_checkers.CommentCheckerBasic;
import entities.suggested_title_checkers.SuggestedTitleChecker;
import entities.suggested_title_checkers.SuggestedTitleCheckerBasic;
import entities.display_name_checkers.DisplayNameChecker;
import entities.display_name_checkers.DisplayNameCheckerBasic;
import entities.games.GameFactory;
import entities.games.GameFactoryRegular;
import frameworks_drivers.views.SpringBootView;
import usecases.RepoRes;
import usecases.ThreadRegister;
import usecases.comment_as_guest.CagInteractor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.ConfigurableApplicationContext;
import usecases.*;
import usecases.disconnecting.DcInteractor;
import usecases.get_all_titles.GatInteractor;
import usecases.get_latest_stories.GlsInteractor;
import usecases.get_most_liked_stories.GmlsInteractor;
import usecases.get_story_comments.GscInteractor;
import usecases.join_public_lobby.JplInteractor;
import usecases.like_story.LsInteractor;
import usecases.pull_data.PdInteractor;
import usecases.pull_game_ended.PgeInteractor;
import usecases.shutdown_server.SsInteractor;
import usecases.sort_players.SpInteractor;
import usecases.submit_word.SwInteractor;
import usecases.suggest_title.StGateway;
import usecases.suggest_title.StInteractor;
import usecases.upvote_title.UtInteractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
        PdViewModel pdViewM = new PdViewModel();
        PgeViewModel pgeViewM = new PgeViewModel();

        ThreadRegister register = new ThreadRegister();

        PdPresenter pdPresenter = new PdPresenter(pdViewM);
        PgePresenter pgePresenter = new PgePresenter(pgeViewM);

        // Create desired display name checker for injection
        DisplayNameChecker displayChecker = new DisplayNameCheckerBasic();

        // Create desired comment checker for injection
        CommentChecker commentChecker = new CommentCheckerBasic();

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
        CagInteractor cag = new CagInteractor(cagPresenter, (storyId, displayName, comment) -> null,
                commentChecker, displayChecker, register); // TODO: Inject repo
        GatInteractor gat = new GatInteractor(gatPresenter,
                storyId -> new RepoRes<TitleRepoData>(Response.getFailure("Dummy Lambda, Always failure"))
                ,register);
        GscInteractor gsc = new GscInteractor(gscPresenter, storyId -> null, register); // TODO: Inject repo
        DcInteractor dc = new DcInteractor(manager, register);
        GlsInteractor gls = new GlsInteractor(
                () -> new RepoRes<StoryRepoData>(Response.getFailure("Dummy Lambda, Always failure")),
                register); // TODO: Inject repo
        GmlsInteractor gmls = new GmlsInteractor(
                () -> new RepoRes<StoryRepoData>(Response.getFailure("Dummy Lambda, Always failure")),
                register); // TODO: Inject repo
        JplInteractor jpl = new JplInteractor(manager, register);
        LsInteractor ls = new LsInteractor(
                storyId -> Response.getSuccessful("Dummy Lambda, Always successful"),
                register); // TODO: Inject repo
        SsInteractor ss = new SsInteractor(register);
        SwInteractor sw = new SwInteractor(manager, register);
        StInteractor st = new StInteractor(new StGateway() {
            @Override
            public @NotNull Response suggestTitle(int storyId, @Nullable String titleSuggestion) {
                return null;
            }

            @Override
            public @NotNull RepoRes<TitleRepoData> getAllTitles(int storyId) {
                return null;
            }
        }, titleChecker, register);
        UtInteractor ut = new UtInteractor(utPresenter,
                ((storyId, titleToUpvote) -> Response.getSuccessful("Dummy Lambda, Always successful")),
                register);


        // Controllers
        CagController cagController = new CagController(cag);
        DcController dcController = new DcController(dc);
        GatController gatController = new GatController(gat);
        GlsController glsController = new GlsController(gls);
        GmlsController gmlsController = new GmlsController(gmls);
        GscController gscController = new GscController(gsc);
        JplController jplController = new JplController(jpl);
        LsController lsController = new LsController(ls);
        SsController ssController = new SsController(ss);
        SwController swController = new SwController(sw);
        StController stController = new StController(st);
        UtController utController = new UtController(ut);

        System.out.println("Main: Before Spring Init");

        // TODO: Setup and run the view
        SpringBootView.init(
          cagController, dcController, gatController, glsController, gmlsController,
          gscController, jplController, lsController, ssController, stController,
          swController, utController
        );

        SpringBootView.getInstance().runApplicationLoop();
    }
}
