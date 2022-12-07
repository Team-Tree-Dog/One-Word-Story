package com.example.springapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SpringappApplication {

	public static org.springframework.context.ConfigurableApplicationContext startServer(String[] args) {
		return SpringApplication.run(SpringappApplication.class, args);
	}

	@GetMapping("/hello")
	public String helloworld() {
		System.out.println("/hello has been gotten");
		return "HEEEYYYYYYY";
	}

}
