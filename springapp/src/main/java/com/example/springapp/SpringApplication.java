package com.example.springapp;
import frameworks_drivers.views.SpringBootView;
import frameworks_drivers.views.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@Controller
public class SpringApplication {

	private final View viewRef = SpringBootView.getInstance();

	public static org.springframework.context.ConfigurableApplicationContext startServer(String[] args) {
		return org.springframework.boot.SpringApplication.run(SpringApplication.class, args);
	}

	@GetMapping("/hello")
	public String helloworld() {
		System.out.println("/hello has been gotten");
		return "HEEEYYYYYYY";
	}

}
