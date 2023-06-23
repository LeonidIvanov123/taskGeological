package ru.leonid.taskGeological;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;

@SpringBootApplication
public class TaskGeologicalApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskGeologicalApplication.class, args);

	}
}