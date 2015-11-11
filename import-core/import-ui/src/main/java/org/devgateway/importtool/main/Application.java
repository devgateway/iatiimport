package org.devgateway.importtool.main;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SessionCookieConfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan({ "org.devgateway.importtool.main",
		"org.devgateway.importtool.services", "org.devgateway.importtool.rest" })
@Configuration
@EnableAutoConfiguration
public class Application extends WebMvcAutoConfiguration {
	public static void main(String[] args) {
		SpringApplication.run(Application.class);
	}

	@Bean
	public SessionTrackingConfigListener sessionTrackingConfigListener() {
		SessionTrackingConfigListener listener = new SessionTrackingConfigListener();
		return listener;
	}

	@Bean
    MultipartConfigElement multipartConfigElement() {	    
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("2Mb");
        factory.setMaxRequestSize("10Mb");
        return factory.createMultipartConfig();
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