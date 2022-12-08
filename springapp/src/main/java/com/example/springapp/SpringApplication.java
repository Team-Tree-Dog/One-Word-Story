package com.example.springapp;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SpringApplication {

	public static org.springframework.context.ConfigurableApplicationContext startServer(String[] args) {
		return org.springframework.boot.SpringApplication.run(SpringApplication.class, args);
	}

	@GetMapping("/hello")
	public String helloworld() {
		System.out.println("/hello has been gotten");
		return "HEEEYYYYYYY";
	}

}
