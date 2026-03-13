package com.project.sonica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SonicaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SonicaApplication.class, args);
	}
}
