package com.example.demo;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfiguration {

	@Bean
	@ServiceConnection
	PostgreSQLContainer postgresContainer() {
		return new PostgreSQLContainer(DockerImageName.parse("postgres").withTag("18.4-alpine"))
                .withCopyFileToContainer(
                        MountableFile.forClasspathResource("init.sql"),
                        "/docker-entrypoint-initdb.d/init.sql"
                );
	}

    @Bean
    DynamicPropertyRegistrar dynamicPropertySource(PostgreSQLContainer postgresContainer) {
        return registry -> {
            registry.add("r2dbc.host", postgresContainer::getHost);
            registry.add("r2dbc.port", postgresContainer::getFirstMappedPort);
            registry.add("r2dbc.username", postgresContainer::getUsername);
            registry.add("r2dbc.password", postgresContainer::getPassword);
            registry.add("r2dbc.database", postgresContainer::getDatabaseName);
        };
    }

}