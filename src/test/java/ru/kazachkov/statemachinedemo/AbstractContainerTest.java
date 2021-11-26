package ru.kazachkov.statemachinedemo;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;
import java.util.stream.Stream;

@ContextConfiguration(
        initializers = AbstractContainerTest.Initializer.class)
@SpringBootTest
public abstract class AbstractContainerTest {

    private static final String IMAGE_VERSION = "postgres:11.1";
    private static final String DB_NAME = "postgres";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "postgres";

    private static DockerImageName getDockerImageName() {
        return DockerImageName.parse(IMAGE_VERSION).asCompatibleSubstituteFor("postgres");
    }

    static class Initializer implements
            ApplicationContextInitializer<ConfigurableApplicationContext> {

        //static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>();
    public static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer<>(getDockerImageName())
            .withDatabaseName(DB_NAME)
            .withUsername(USERNAME)
            .withPassword(PASSWORD)
                .withUrlParam("currentSchema","test");
        private static void startContainers() {
            POSTGRES.start();
        }

        private static Map<String, String> createConnectionConfiguration() {
            return Map.of(
                    "spring.datasource.url", POSTGRES.getJdbcUrl(),
                    "spring.datasource.username", POSTGRES.getUsername(),
                    "spring.datasource.password", POSTGRES.getPassword(),
                    "spring.flyway.schemas","test",
                    "spring.quartz.jdbc.schema","test"
            );
        }

        @Override
        public void initialize(
                ConfigurableApplicationContext applicationContext) {

            startContainers();

            ConfigurableEnvironment environment =
                    applicationContext.getEnvironment();

            MapPropertySource testcontainers = new MapPropertySource(
                    "testcontainers",
                    (Map) createConnectionConfiguration()
            );

            environment.getPropertySources().addFirst(testcontainers);
        }
    }
}