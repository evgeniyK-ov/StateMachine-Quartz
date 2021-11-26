package ru.kazachkov.statemachinedemo;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.statemachine.boot.autoconfigure.StateMachineAutoConfiguration;
import org.springframework.statemachine.boot.autoconfigure.StateMachineJpaRepositoriesAutoConfiguration;
import org.springframework.statemachine.data.jpa.JpaRepositoryState;
import org.springframework.statemachine.data.jpa.JpaStateMachineRepository;

@SpringBootApplication(scanBasePackages = {
		"ru.kazachkov.statemachinedemo"
}
//,exclude = {StateMachineJpaRepositoriesAutoConfiguration.class}
)
@EntityScan(basePackages ={"ru.kazachkov.statemachinedemo.models.entities"}
)
@EnableJpaRepositories(basePackages ={
		"ru.kazachkov.statemachinedemo.models.repository"
})
public class StatemachineDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(StatemachineDemoApplication.class, args);
	}

}
