package org.devgateway.importtool.main;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SessionCookieConfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@ComponentScan({ "org.devgateway.importtool.main",
		"org.devgateway.importtool.services", "org.devgateway.importtool.rest," +
		"org.devgateway.importtool.scheduler" })
@Configuration
@EnableAutoConfiguration
@EnableScheduling
public class Application extends WebMvcAutoConfiguration {
	public static void main(String[] args) {
        System.setProperty("server.tomcat.max-threads","200");
        System.setProperty("server.connection-timeout","60000");
		SpringApplication.run(Application.class, args).start();
	}

	@Bean
	public SessionTrackingConfigListener sessionTrackingConfigListener() {
		SessionTrackingConfigListener listener = new SessionTrackingConfigListener();
		return listener;
	}

	@Bean
	MultipartConfigElement multipartConfigElement() {
		return new MultipartConfigElement("");
	}

	public class SessionTrackingConfigListener implements
			ServletContextInitializer {

		@Override
		public void onStartup(ServletContext servletContext)
				throws ServletException {
			SessionCookieConfig sessionCookieConfig = servletContext
					.getSessionCookieConfig();
			sessionCookieConfig.setName("TOMCATSESSIONID");
		}

	}

}