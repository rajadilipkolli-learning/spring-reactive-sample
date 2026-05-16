package com.example.demo;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfiguration {

    @Bean
    PostgreSQLContainer postgresContainer() {
        return new PostgreSQLContainer(DockerImageName.parse("postgres:18.4-alpine"))
                .withCopyFileToContainer(
                        MountableFile.forClasspathResource("init.sql"),
                        "/docker-entrypoint-initdb.d/init.sql");
    }

    @Bean
    DynamicPropertyRegistrar dynamicPropertyRegistrar(PostgreSQLContainer postgres) {
        return registry -> {
            registry.add("r2dbc.host", postgres::getHost);
            registry.add("r2dbc.port", postgres::getFirstMappedPort);
            registry.add("r2dbc.username", postgres::getUsername);
            registry.add("r2dbc.password", postgres::getPassword);
            registry.add("r2dbc.database", postgres::getDatabaseName);
        };
    }

}
