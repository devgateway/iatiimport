package org.devgateway.importtool.services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAutoConfiguration
@EnableAsync
public class Application {

	public static void main(String[] args) {
	    SpringApplication.run(Application.class);
	}
}
