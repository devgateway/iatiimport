package org.devgateway.importtool.rest;

import javax.servlet.MultipartConfigElement;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@EnableAutoConfiguration
@ComponentScan
@EnableConfigurationProperties
public class RestApplication {
	public static void main(String[] args) {
	//    SpringApplication.run(Application.class);
	}
	
	@Bean
    MultipartConfigElement multipartConfigElement() {
        return new MultipartConfigElement("");
    }

} 