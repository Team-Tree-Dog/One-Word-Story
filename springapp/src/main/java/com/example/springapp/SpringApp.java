package com.example.springapp;

import adapters.display_data.not_ended_display_data.GameDisplayData;
import adapters.display_data.not_ended_display_data.PlayerDisplayData;
import adapters.view_models.JplViewModel;
import adapters.view_models.SwViewModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import frameworks_drivers.views.View;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import usecases.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
public class SpringApp {
	public static View viewRef;

	public static org.springframework.context.ConfigurableApplicationContext startServer(View viewRef, String[] args) {
		SpringApp.viewRef = viewRef;
		return org.springframework.boot.SpringApplication.run(SpringApp.class, args);
	}

	@Configuration
	@EnableWebSocket
	public class WebSocketConfig implements WebSocketConfigurer {
		public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
			registry
					.addHandler(new SocketTextHandler(), "/game")
					.setAllowedOriginPatterns("*");
		}
	}

	public sealed interface ClientCommand {
		final String CMD_TRY_JOIN = "try_join";
		final String CMD_STATE_UPDATE = "state_update";
		final String CMD_SEND_WORD = "send_word";
		final String CMD_LEAVE = "leave";

		final char SEPARATOR = 30;

		record TryJoin(String playerName) implements ClientCommand {}
		record StateUpdate() implements ClientCommand {}
		record SendWord(String word) implements ClientCommand {}
		record Leave() implements ClientCommand {}

		static ClientCommand parseCommand(String payload) {
			String[] payloadBlocks = payload.split(Character.toString((char)SEPARATOR));
			return switch (payloadBlocks[0]) {
				case CMD_TRY_JOIN -> new TryJoin(payloadBlocks[1]);
				case CMD_STATE_UPDATE -> new StateUpdate();
				case CMD_SEND_WORD -> new SendWord(payloadBlocks[1]); //, payloadBlocks[2]);
				case CMD_LEAVE -> new Leave();
				default -> throw new UnsupportedOperationException("Invalid parameter: " + payloadBlocks[0]);
			};
		}
	}

	public sealed interface ServerResponse {
		final String RESPONSE_JOIN = "join_response";
		final String RESPONSE_STATE = "current_state";

		final char SEPARATOR = 30;

		abstract String pack() throws Exception;

		record JoinResponse(boolean response) implements ServerResponse {
			@Override
			public String pack() {
				return RESPONSE_JOIN + SEPARATOR + (response ? "true" : "false");
			}
		}
		record CurrentState(GameDisplayData data) implements ServerResponse {
			private static final ObjectMapper mapper = new ObjectMapper();	// thread-safe

			@Override
			public String pack() throws Exception {
				return RESPONSE_STATE + SEPARATOR + mapper.writeValueAsString(data);
			}
		}
	}

	@Component
	public class SocketTextHandler extends TextWebSocketHandler {

		private static class PlayerState {
			public String playerId;
			public String displayName = null;
			public JplViewModel jplViewM = null;

			public PlayerState(String playerId) {
				this.playerId = playerId;
			}
		}

		private final static Map<String, PlayerState> sessionToPlyId = new ConcurrentHashMap<>();

		@Override
		public void afterConnectionEstablished(WebSocketSession session) throws Exception {
			sessionToPlyId.put(session.getId(), new PlayerState(UUID.randomUUID().toString()));
		}

		@Override
		public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
			// Calls disconnect on a thread, ignores response
			viewRef.dcController.disconnect(sessionToPlyId.get(session.getId()).playerId);

			sessionToPlyId.remove(session.getId());
		}

		@Override
		public void handleTextMessage(WebSocketSession session, TextMessage message)
				throws InterruptedException, IOException {
			try {
				String payload = message.getPayload();

				if (payload.isEmpty()) {
					return;
				}

				ClientCommand incomingCmd = ClientCommand.parseCommand(payload);
				ServerResponse response = null;

				if(incomingCmd instanceof ClientCommand.TryJoin) {
					boolean result = tryJoin(session.getId(), ((ClientCommand.TryJoin) incomingCmd).playerName);
					response = new ServerResponse.JoinResponse(result);
				} else if(incomingCmd instanceof ClientCommand.StateUpdate) {
					GameDisplayData data = getCurrentGameState(session.getId());
					response = new ServerResponse.CurrentState(data);
				} else if(incomingCmd instanceof ClientCommand.SendWord) {
					newWord(session.getId (), ((ClientCommand.SendWord) incomingCmd).word);
				} else if(incomingCmd instanceof ClientCommand.Leave) {
					leave();
				}

				System.out.println("Got command: " + incomingCmd.toString());

				if(response != null) {
					String msg = response.pack();

					System.out.println("Sending response: " + msg);

					session.sendMessage(new TextMessage(msg));
				}

			} catch (Exception e) {
				System.out.println("Error occurred: " + e.toString());
			}
		}

		/**
		 * JPL
		 */
		boolean tryJoin(String sessionId, String playerName) throws InterruptedException {

			PlayerState dat = sessionToPlyId.get(sessionId);

			// Calls JPL. JPL will output further things but we wont care
			JplViewModel jplViewM = viewRef.jplController.joinPublicLobby(
					dat.playerId, playerName);

			while (jplViewM.getResponseCode() == null) {
				Thread.sleep(20);
			}

			if (jplViewM.getResponseCode() == Response.ResCode.SUCCESS) {
				// Player name was approved, move forward
				dat.displayName = playerName;
				dat.jplViewM = jplViewM;
				return true;
			} else {
				// Tell frontend to reload page and disconnect, display name was bad
				return false;
			}
		}

		/**
		 * READ PD
		 */
		GameDisplayData getCurrentGameState(String sessionId) {
//			PlayerState dat = sessionToPlyId.get(sessionId);
//			if (dat.jplViewM.getGameState() != null) {
//
//			}
			return viewRef.pdViewM.getCurrentGameState();
		}

		void newWord(String sessionId, String word) throws InterruptedException {
			PlayerState dat = sessionToPlyId.get(sessionId);

			if (dat.displayName != null && dat.jplViewM.getGameState() != null) {
				SwViewModel viewM = viewRef.swController.submitWord(dat.playerId, word);

				while (viewM.getResponseCode() == null) {
					Thread.sleep(20);
				}
			}

			// TODO: Ideally, return response code
		}

		void leave() {

			// TODO: Remove, not needed, taken care of in connection closure callback

		}

	}
}
