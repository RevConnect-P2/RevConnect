package com.revconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// LOGGER IMPORTS
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SpringBootApplication
public class

RevconnectApplication {

	private static final Logger logger =
			LogManager.getLogger(RevconnectApplication.class);

	public static void main(String[] args) {

		logger.info("Starting RevConnect Application...");

		SpringApplication.run(RevconnectApplication.class, args);

		logger.info("RevConnect Application started successfully");
	}

}