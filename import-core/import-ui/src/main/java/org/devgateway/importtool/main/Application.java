package org.devgateway.importtool.main;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.SessionCookieConfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableAspectJAutoProxy
@SpringBootApplication(scanBasePackages ={ "org.devgateway.importtool.main",
		"org.devgateway.importtool.services", "org.devgateway.importtool.rest," +
		"org.devgateway.importtool.scheduler","org.devgateway.importtool.rest.error" } )
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
		public void onStartup(ServletContext servletContext) {
			SessionCookieConfig sessionCookieConfig = servletContext
					.getSessionCookieConfig();
			sessionCookieConfig.setName("TOMCATSESSIONID");
		}

	}

}