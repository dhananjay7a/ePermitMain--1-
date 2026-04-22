package com.organisation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication

@EntityScan(basePackages = { "com.organisation.model", "com.register.model", "com.organisation.entity" })
@EnableJpaRepositories(basePackages = { "com.organisation.repository" })

public class ePermitMainApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(ePermitMainApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(ePermitMainApplication.class, args);
	}

}
