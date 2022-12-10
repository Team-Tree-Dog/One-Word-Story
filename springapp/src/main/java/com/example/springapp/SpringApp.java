package com.example.springapp;

import frameworks_drivers.views.View;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@SpringBootApplication
public class SpringApp {
	/**
	 * Reference to abstract View class in core project which gives access
	 * to all the use case controllers, as well as the PGE and PD view models
	 * which contain game data
	 */
	public static View viewRef;

	/**
	 * Start the spring web application
	 * @param viewRef Reference to the view object containing all use case controllers and PD/PGE view models
	 * @param args Optional String arguments to pass into the spring run call
	 * @return The top interface of the newly created spring application; the spring Application Context
	 */
	public static org.springframework.context.ConfigurableApplicationContext startServer(View viewRef, String[] args) {
		SpringApp.viewRef = viewRef;
		return org.springframework.boot.SpringApplication.run(SpringApp.class, args);
	}

	/**
	 * Configures BEANNNSSSS for the websocket.
	 * That is, tells spring the necessary information
	 * to plug websockets into the application. In this case, we configure the class that will
	 * be handling our websocket logic
	 */
	@Configuration
	@EnableWebSocket
	public static class WebSocketConfig implements WebSocketConfigurer {
		public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
			registry.addHandler(new SocketTextHandler(viewRef), "/game");
		}
	}
}