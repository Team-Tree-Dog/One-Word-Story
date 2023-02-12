package net.onewordstory.spring;

import net.onewordstory.core.adapters.controllers.*;
import net.onewordstory.core.adapters.presenters.PdPresenter;
import net.onewordstory.core.adapters.presenters.PgePresenter;
import net.onewordstory.core.adapters.view_models.PdViewModel;
import net.onewordstory.core.adapters.view_models.PgeViewModel;
import net.onewordstory.core.entities.LobbyManager;
import net.onewordstory.core.entities.PlayerFactory;
import net.onewordstory.core.entities.comment_checkers.CommentChecker;
import net.onewordstory.core.entities.comment_checkers.CommentCheckerBasic;
import net.onewordstory.core.entities.display_name_checkers.DisplayNameChecker;
import net.onewordstory.core.entities.display_name_checkers.DisplayNameCheckerBasic;
import net.onewordstory.core.entities.games.GameFactory;
import net.onewordstory.core.entities.games.GameFactoryRegular;
import net.onewordstory.core.entities.statistics.AverageTurnDurationPlayerStatistic;
import net.onewordstory.core.entities.statistics.LettersUsedByPlayerStatistic;
import net.onewordstory.core.entities.statistics.PerPlayerIntStatistic;
import net.onewordstory.core.entities.statistics.WordCountPlayerStatistic;
import net.onewordstory.core.entities.suggested_title_checkers.SuggestedTitleChecker;
import net.onewordstory.core.entities.suggested_title_checkers.SuggestedTitleCheckerBasic;
import net.onewordstory.core.frameworks_drivers.repository.in_memory.InMemoryCommentsRepo;
import net.onewordstory.core.frameworks_drivers.repository.in_memory.InMemoryStoryRepo;
import net.onewordstory.core.frameworks_drivers.repository.in_memory.InMemoryTitlesRepo;
import net.onewordstory.core.frameworks_drivers.views.SpringBootView;
import net.onewordstory.core.usecases.ThreadRegister;
import net.onewordstory.core.usecases.comment_as_guest.CagGatewayComments;
import net.onewordstory.core.usecases.comment_as_guest.CagInteractor;
import net.onewordstory.core.usecases.disconnecting.DcInteractor;
import net.onewordstory.core.usecases.get_all_titles.GatGatewayTitles;
import net.onewordstory.core.usecases.get_all_titles.GatInteractor;
import net.onewordstory.core.usecases.get_latest_stories.GlsGatewayStory;
import net.onewordstory.core.usecases.get_latest_stories.GlsGatewayTitles;
import net.onewordstory.core.usecases.get_latest_stories.GlsInteractor;
import net.onewordstory.core.usecases.get_most_liked_stories.GmlsGatewayStory;
import net.onewordstory.core.usecases.get_most_liked_stories.GmlsGatewayTitles;
import net.onewordstory.core.usecases.get_most_liked_stories.GmlsInteractor;
import net.onewordstory.core.usecases.get_story_by_id.GsbiGatewayStories;
import net.onewordstory.core.usecases.get_story_by_id.GsbiGatewayTitles;
import net.onewordstory.core.usecases.get_story_by_id.GsbiInteractor;
import net.onewordstory.core.usecases.get_story_comments.GscGatewayComments;
import net.onewordstory.core.usecases.get_story_comments.GscInteractor;
import net.onewordstory.core.usecases.join_public_lobby.JplInteractor;
import net.onewordstory.core.usecases.like_story.LsGatewayStory;
import net.onewordstory.core.usecases.like_story.LsInteractor;
import net.onewordstory.core.usecases.pull_data.PdInteractor;
import net.onewordstory.core.usecases.pull_game_ended.PgeGatewayStory;
import net.onewordstory.core.usecases.pull_game_ended.PgeInteractor;
import net.onewordstory.core.usecases.shutdown_server.SsInteractor;
import net.onewordstory.core.usecases.sort_players.SpInteractor;
import net.onewordstory.core.usecases.submit_word.SwInteractor;
import net.onewordstory.core.usecases.suggest_title.StGatewayTitles;
import net.onewordstory.core.usecases.suggest_title.StInteractor;
import net.onewordstory.core.usecases.upvote_title.UtGatewayTitles;
import net.onewordstory.core.usecases.upvote_title.UtInteractor;
import net.onewordstory.spring.db.PostgresCommentsRepo;
import net.onewordstory.spring.db.PostgresStoryRepo;
import net.onewordstory.spring.db.PostgresTitlesRepo;
import net.onewordstory.spring.guest_management.GuestAccountManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.Locale;

@SpringBootApplication
@ComponentScan(basePackages = "net.onewordstory.core")
public class SpringApp {

	static SpringBootView parentView;

	/**
	 * Start the spring web application
	 * @param view The view class that starts this application
	 * @param args Optional String arguments to pass into the spring run call
	 * @return The top interface of the newly created spring application; the spring Application Context
	 */
	public static org.springframework.context.ConfigurableApplicationContext startServer(
			SpringBootView view, String[] args) {
		parentView = view;
		return org.springframework.boot.SpringApplication.run(SpringApp.class, args);
	}

	/**
	 * to plug websockets into the application. In this case, we configure the class that will
	 * be handling our websocket logic. We do this by injecting use cases into a socket handler
	 * which then gets injected into the websocket handler configuration
	 */
	@Configuration
	@EnableWebSocket
	@ComponentScan(basePackages = "net.onewordstory.spring")
	public static class WebSocketConfig implements WebSocketConfigurer {
		@Autowired
		SocketTextHandler handler;

		@Override
		public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
			registry
					.addHandler(handler, "/game")
					.setAllowedOriginPatterns("*");
		}
	}

	/**
	 * Takes care of autowiring the parent view class. This is done in case
	 * the parent view needs access to any use cases. In our case, the parent
	 * view needs to shut down the server.
	 */
	@Component
	public static class SpringBootViewInit implements InitializingBean {
		@Autowired
		private AutowireCapableBeanFactory beanFactory;

		@Override
		public void afterPropertiesSet() {
			beanFactory.autowireBean(parentView);
		}
	}

	/**
	 * Builds clean architecture and exposes controllers, and the PD/PGE view models
	 * as beans which can be autowired across spring where necessary
	 */
	@Configuration
	@ComponentScan(basePackages = "net.onewordstory.spring")
	public static class UseCaseApiConfig {

		private final CagInteractor cag;
		private final DcInteractor dc;
		private final GlsInteractor gls;
		private final GmlsInteractor gmls;
		private final GsbiInteractor gsbi;
		private final GscInteractor gsc;
		private final GatInteractor gat;
		private final JplInteractor jpl;
		private final LsInteractor ls;
		private final SsInteractor ss;
		private final SwInteractor sw;
		private final StInteractor st;
		private final UtInteractor ut;
		private final PdViewModel pdViewM;
		private final PgeViewModel pgeViewM;

		@Autowired
		public UseCaseApiConfig(PostgresStoryRepo postgresStoryRepo,
								PostgresTitlesRepo postgresTitlesRepo,
								PostgresCommentsRepo postgresCommentsRepo
								//GuestAccountManager accountManager
		) {
			this.pdViewM = new PdViewModel();
			this.pgeViewM = new PgeViewModel();

			GuestAccountManager accountManager = new GuestAccountManager();

			ThreadRegister register = new ThreadRegister();

			PdPresenter pdPresenter = new PdPresenter(pdViewM);
			PgePresenter pgePresenter = new PgePresenter(pgeViewM);

			// Create desired checkers for injection
			CommentChecker commentChecker = new CommentCheckerBasic();
			DisplayNameChecker displayChecker = new DisplayNameCheckerBasic();
			SuggestedTitleChecker titleChecker = new SuggestedTitleCheckerBasic();

			// Create desired Story, Titles, and Comments repos (currently uses in-memory
			// if PROD!=true, otherwise postgres
			Object storyRepo;
			Object titlesRepo;
			Object commentsRepo;
			if (System.getenv("PROD") == null ||
					!System.getenv("PROD").toLowerCase(Locale.ENGLISH).equals("true")) {
				titlesRepo = new InMemoryTitlesRepo();
				commentsRepo = new InMemoryCommentsRepo();
				storyRepo = new InMemoryStoryRepo();
			} else {
				storyRepo = postgresStoryRepo;
				titlesRepo = postgresTitlesRepo;
				commentsRepo = postgresCommentsRepo;
			}

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
			PgeInteractor pge = new PgeInteractor(pgePresenter, (PgeGatewayStory) storyRepo);
			SpInteractor sp = new SpInteractor(manager, pge, pd);
			sp.startTimer();

			// Use cases called by users
			this.cag = new CagInteractor((CagGatewayComments) commentsRepo, commentChecker, displayChecker, register);
			this.dc = new DcInteractor(manager, register);
			this.gls = new GlsInteractor((GlsGatewayStory) storyRepo, (GlsGatewayTitles) titlesRepo, register);
			this.gmls = new GmlsInteractor((GmlsGatewayStory) storyRepo, (GmlsGatewayTitles) titlesRepo, register);
			this.gsbi = new GsbiInteractor((GsbiGatewayStories) storyRepo, (GsbiGatewayTitles) titlesRepo, register);
			this.gsc = new GscInteractor((GscGatewayComments) commentsRepo, register);
			this.gat = new GatInteractor((GatGatewayTitles) titlesRepo, register);
			this.jpl = new JplInteractor(manager, register);
			this.ls = new LsInteractor((LsGatewayStory) storyRepo, accountManager, register);
			this.ss = new SsInteractor(register);
			this.sw = new SwInteractor(manager, register);
			this.st = new StInteractor((StGatewayTitles) titlesRepo, titleChecker, register);
			this.ut = new UtInteractor((UtGatewayTitles) titlesRepo, accountManager, register);
		}

		@Bean PdViewModel pdViewModel() {return pdViewM;}
		@Bean PgeViewModel pgeViewModel() {return pgeViewM;}
		@Bean public CagController cagController() {return new CagController(cag);}
		@Bean public DcController dcController() {return new DcController(dc);}
		@Bean public GatController gatController() {return new GatController(gat);}
		@Bean public GlsController glsController() {return new GlsController(gls);}
		@Bean public GmlsController gmlsController() {return new GmlsController(gmls);}
		@Bean public GsbiController gsbiController() {return new GsbiController(gsbi);}
		@Bean public GscController gscController() {return new GscController(gsc);}
		@Bean public JplController jplController() {return new JplController(jpl);}
		@Bean public LsController lsController() {return new LsController(ls);}
		@Bean public SsController ssController() {return new SsController(ss);}
		@Bean public SwController swController() {return new SwController(sw);}
		@Bean public StController stController() {return new StController(st);}
		@Bean public UtController utController() {return new UtController(ut);}
	}

	/**
	 * Configures the guest account manager
	 */
	@Configuration
	public static class WebConfig implements WebMvcConfigurer {

		@Autowired
		GuestAccountManager accountManager;

		@Override
		public void addInterceptors(InterceptorRegistry registry) {
			registry.addInterceptor(accountManager);
		}
	}

}
