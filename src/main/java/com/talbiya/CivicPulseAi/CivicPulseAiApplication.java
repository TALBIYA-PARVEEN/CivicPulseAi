package com.talbiya.CivicPulseAi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CivicPulseAiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CivicPulseAiApplication.class, args);
	}

}
