package com.example.demo;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.test.util.TestPropertyValues;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfiguration implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Bean
    PostgreSQLContainer postgresContainer() {
        return createContainer();
    }

    private static PostgreSQLContainer createContainer() {
        return new PostgreSQLContainer(DockerImageName.parse("postgres:18.4-alpine"))
                .withCopyFileToContainer(
                        MountableFile.forClasspathResource("init.sql"),
                        "/docker-entrypoint-initdb.d/init.sql");
    }

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        PostgreSQLContainer postgres = createContainer();
        postgres.start();
        TestPropertyValues.of(
                "r2dbc.host=" + postgres.getHost(),
                "r2dbc.port=" + postgres.getFirstMappedPort(),
                "r2dbc.username=" + postgres.getUsername(),
                "r2dbc.password=" + postgres.getPassword(),
                "r2dbc.database=" + postgres.getDatabaseName()
        ).applyTo(context.getEnvironment());
    }
}
