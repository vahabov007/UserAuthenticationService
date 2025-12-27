package com.vahabvahabov.LoginDemo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@SpringBootApplication
@ComponentScan(basePackages = "com.vahabvahabov.LoginDemo")
@EntityScan(basePackages = "com.vahabvahabov.LoginDemo")
@EnableJpaRepositories(basePackages = "com.vahabvahabov.LoginDemo")
@EnableAsync
public class LoginDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoginDemoApplication.class, args);
	}
}

@Component
class DatabaseSchemaInitializer implements CommandLineRunner {

	private final JdbcTemplate jdbcTemplate;

	public DatabaseSchemaInitializer(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void run(String... args) throws Exception {
		String schemaSql = "CREATE SCHEMA IF NOT EXISTS login_page";
		jdbcTemplate.execute(schemaSql);
		System.out.println("Schema 'login_page' created successfully.");
	}
}