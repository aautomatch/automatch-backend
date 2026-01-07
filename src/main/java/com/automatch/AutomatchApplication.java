package com.automatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.automatch",
		"com.automatch.portal",
		"com.automatch.portal.infra.security",
		"com.automatch.portal.service",
		"com.automatch.portal.config"
})
public class AutomatchApplication {
	public static void main(String[] args) {
		SpringApplication.run(AutomatchApplication.class, args);
	}
}