package com.vaticano.paroquia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ParoquiaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParoquiaApplication.class, args);
	}

}
