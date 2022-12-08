package com.example.springapp;
import frameworks_drivers.views.View;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringApp {
	public static View viewRef;

	public static org.springframework.context.ConfigurableApplicationContext startServer(View viewRef, String[] args) {
		SpringApp.viewRef = viewRef;
		return org.springframework.boot.SpringApplication.run(SpringApp.class, args);
	}
}
