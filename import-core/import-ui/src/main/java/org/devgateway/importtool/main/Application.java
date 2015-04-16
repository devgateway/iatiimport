package org.devgateway.importtool.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.MultipartConfigElement;

@ComponentScan({ "org.devgateway.importtool.main", "org.devgateway.importtool.services", "org.devgateway.importtool.rest" })
@Configuration
@EnableAutoConfiguration
public class Application extends WebMvcAutoConfiguration {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Bean
    MultipartConfigElement multipartConfigElement() {
        return new MultipartConfigElement("");
    }

} 