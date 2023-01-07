import adapters.controllers.*;
import adapters.presenters.*;
import adapters.view_models.PdViewModel;
import adapters.view_models.PgeViewModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.LobbyManager;
import entities.PlayerFactory;
import entities.comment_checkers.CommentChecker;
import entities.comment_checkers.CommentCheckerBasic;
import entities.statistics.AverageTurnDurationPlayerStatistic;
import entities.statistics.LettersUsedByPlayerStatistic;
import entities.statistics.PerPlayerIntStatistic;
import entities.statistics.WordCountPlayerStatistic;
import entities.suggested_title_checkers.SuggestedTitleChecker;
import entities.suggested_title_checkers.SuggestedTitleCheckerBasic;
import entities.display_name_checkers.DisplayNameChecker;
import entities.display_name_checkers.DisplayNameCheckerBasic;
import entities.games.GameFactory;
import entities.games.GameFactoryRegular;
import frameworks_drivers.repository.in_memory.InMemoryCommentsRepo;
import frameworks_drivers.repository.in_memory.InMemoryStoryRepo;
import frameworks_drivers.repository.in_memory.InMemoryTitlesRepo;
import frameworks_drivers.views.CoreAPI;
import usecases.comment_as_guest.CagInteractor;
import usecases.ThreadRegister;
import usecases.disconnecting.DcInteractor;
import usecases.get_all_titles.GatInteractor;
import usecases.get_latest_stories.GlsInteractor;
import usecases.get_most_liked_stories.GmlsInteractor;
import usecases.get_story_by_id.GsbiInteractor;
import usecases.get_story_comments.GscInteractor;
import usecases.join_public_lobby.JplInteractor;
import usecases.like_story.LsInteractor;
import usecases.pull_data.PdInteractor;
import usecases.pull_game_ended.PgeInteractor;
import usecases.shutdown_server.SsInteractor;
import usecases.sort_players.SpInteractor;
import usecases.submit_word.SwInteractor;
import usecases.suggest_title.StInteractor;
import usecases.upvote_title.UtInteractor;
import util.RecursiveSymboledIntegerHashMap;
import util.SymboledInteger;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Orchestrator. Contains only a main method which initializes the clean architecture
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

        // Create desired checkers for injection
        CommentChecker commentChecker = new CommentCheckerBasic();
        DisplayNameChecker displayChecker = new DisplayNameCheckerBasic();
        SuggestedTitleChecker titleChecker = new SuggestedTitleCheckerBasic();

        // Create desired Story, Titles, and Comments repos
        InMemoryTitlesRepo titlesRepo = new InMemoryTitlesRepo();
        InMemoryCommentsRepo commentsRepo = new InMemoryCommentsRepo();
        InMemoryStoryRepo storyRepo = new InMemoryStoryRepo();

        // Populate dummy data for testing
        // -----------------------
        Set<String> firstAuthors = new HashSet<>();
        firstAuthors.add("Andrew Serdiuk");
        Set<String> secondAuthors = new HashSet<>();
        secondAuthors.add("Andrew Serdiuk");
        secondAuthors.add("Aleksey Panas");
        Set<String> thirdAuthors = new HashSet<>();
        thirdAuthors.add("Suspect");

        storyRepo.saveStory("ONCE upon a time billy went to milk some cows",
                (new Date().getTime() / 1000.0) - 86400, firstAuthors);

        storyRepo.saveStory("Once we were able to finish the assignment in time, but then we woke up"
                , (new Date().getTime() / 1000.0) - 2 * 86400, secondAuthors);

        storyRepo.saveStory("The cause of all evil is: "
                , (new Date().getTime() / 1000.0) - 3 * 86400, thirdAuthors);

        titlesRepo.suggestTitle(0, "When the cows come home");
        titlesRepo.upvoteTitle(0, "When the cows come home");
        titlesRepo.upvoteTitle(0, "When the cows come home");
        titlesRepo.suggestTitle(0, "When the deadline is due");

        storyRepo.likeStory(1);
        storyRepo.likeStory(1);
        storyRepo.likeStory(1);

        storyRepo.likeStory(0);
        storyRepo.likeStory(0);

        storyRepo.likeStory(2);

        titlesRepo.suggestTitle(1, "Very sad story");
        for(int i = 0; i  < 100; i++) {
            titlesRepo.upvoteTitle(1, "Very sad story");
        }

        commentsRepo.commentAsGuest(0, "Andrew", "This is my best piece of work");
        commentsRepo.commentAsGuest(1, "Aleksey", "This is very true");
        commentsRepo.commentAsGuest(2, "Aleksey", "Is this some testing data?");
        // -----------------------

        // Create desired per-player statistics for injection
        PerPlayerIntStatistic[] statistics = {
                new AverageTurnDurationPlayerStatistic(), new WordCountPlayerStatistic(),
                new LettersUsedByPlayerStatistic()
        };

        // Factory which accepts ALL display names with at least 3 characters (temporary)
        PlayerFactory playerFac = new PlayerFactory(displayChecker);
        GameFactory gameFac = new GameFactoryRegular(statistics);

        // Inject particular factories into LobbyManager
        LobbyManager manager = new LobbyManager(playerFac, gameFac);

        // Start up sort players
        PdInteractor pd = new PdInteractor(pdPresenter);
        PgeInteractor pge = new PgeInteractor(pgePresenter, storyRepo);
        SpInteractor sp = new SpInteractor(manager, pge, pd);
        sp.startTimer();

        // Use cases called by users
        CagInteractor cag = new CagInteractor(commentsRepo, commentChecker, displayChecker, register);
        DcInteractor dc = new DcInteractor(manager, register);
        GlsInteractor gls = new GlsInteractor(storyRepo, titlesRepo, register);
        GmlsInteractor gmls = new GmlsInteractor(storyRepo, titlesRepo, register);
        GsbiInteractor gsbi = new GsbiInteractor(storyRepo, titlesRepo, register);
        GscInteractor gsc = new GscInteractor(commentsRepo, register);
        GatInteractor gat = new GatInteractor(titlesRepo, register);
        JplInteractor jpl = new JplInteractor(manager, register);
        LsInteractor ls = new LsInteractor(storyRepo, register);
        SsInteractor ss = new SsInteractor(register);
        SwInteractor sw = new SwInteractor(manager, register);
        StInteractor st = new StInteractor(titlesRepo, titleChecker, register);
        UtInteractor ut = new UtInteractor(titlesRepo, register);

        // Controllers
        CagController cagController = new CagController(cag);
        DcController dcController = new DcController(dc);
        GatController gatController = new GatController(gat);
        GlsController glsController = new GlsController(gls);
        GmlsController gmlsController = new GmlsController(gmls);
        GsbiController gsbiController = new GsbiController(gsbi);
        GscController gscController = new GscController(gsc);
        JplController jplController = new JplController(jpl);
        LsController lsController = new LsController(ls);
        SsController ssController = new SsController(ss);
        SwController swController = new SwController(sw);
        StController stController = new StController(st);
        UtController utController = new UtController(ut);

        System.out.println("Main: Before Spring Init");

        // Setup and run the view
        new SpringBootView(new CoreAPI(
          cagController, dcController, gatController, glsController, gmlsController, gsbiController,
          gscController, jplController, lsController, ssController, stController,
          swController, utController, pgeViewM, pdViewM
        )).runApplicationLoop();
    }
}
