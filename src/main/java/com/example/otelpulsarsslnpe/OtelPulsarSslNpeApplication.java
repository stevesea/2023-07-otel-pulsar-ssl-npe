package com.example.otelpulsarsslnpe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OtelPulsarSslNpeApplication {
	private static final Logger logger = LoggerFactory.getLogger(OtelPulsarSslNpeApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(OtelPulsarSslNpeApplication.class, args);
	}

}
