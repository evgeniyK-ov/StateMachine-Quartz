package ru.kazachkov.config;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;
import java.net.MalformedURLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//@TestConfiguration
public class DockerConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public PostgreSQLContainer jdbcDatabaseContainer() {
        return (PostgreSQLContainer) new PostgreSQLContainer(
                "postgres:11.1")
                .withDatabaseName("integration-tests-db")
                .withUsername("sa")
                .withPassword("sa")
                .withNetworkAliases("localhost");
    }

//    @Bean(initMethod = "start", destroyMethod = "stop")
//    public LocalStackContainer localStack() {
//        return new LocalStackContainer(
//                DockerImageName.parse("localstack/localstack:0.11.3"))
//                .withServices(S3);
//    }

    @Bean
    @Primary
    public DataSource dataSource(JdbcDatabaseContainer<?> jdbcDatabaseContainer) throws MalformedURLException {

        final String regex = "jdbc:postgresql:\\/\\/(.*):";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);

        System.out.println("=====" + jdbcDatabaseContainer.getJdbcUrl());

        final Matcher matcher = pattern.matcher(jdbcDatabaseContainer.getJdbcUrl());
        matcher.find();
        var host = matcher.group(1);

        var hikariConfig = new HikariConfig();
        // hikariConfig.setJdbcUrl(jdbcDatabaseContainer.getJdbcUrl().replace(host, "localhost"));
        hikariConfig.setJdbcUrl(jdbcDatabaseContainer.getJdbcUrl());
        hikariConfig.setUsername(jdbcDatabaseContainer.getUsername());
        hikariConfig.setPassword(jdbcDatabaseContainer.getPassword());

        return new HikariDataSource(hikariConfig);
    }



//    @Bean
//    @Primary
//    public AmazonS3 amazonS3(LocalStackContainer localStack) {
//
//        return AmazonS3ClientBuilder
//                .standard()
//                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(localStack.getEndpointConfiguration(S3).getServiceEndpoint(),
//                        localStack.getEndpointConfiguration(S3).getSigningRegion()))
//                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(localStack.getAccessKey(), localStack.getSecretKey())))
//                .withPathStyleAccessEnabled(false)
//                .build();
//
//    }

}