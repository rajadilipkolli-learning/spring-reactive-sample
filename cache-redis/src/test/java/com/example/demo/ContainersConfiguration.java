package com.example.demo;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.test.util.TestPropertyValues;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;
import com.redis.testcontainers.RedisContainer;

import static com.redis.testcontainers.RedisContainer.DEFAULT_IMAGE_NAME;
import static com.redis.testcontainers.RedisContainer.DEFAULT_TAG;

@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfiguration implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Bean
    public PostgreSQLContainer postgresContainer() {
        return createPostgresContainer();
    }

    private static PostgreSQLContainer createPostgresContainer() {
        return new PostgreSQLContainer(DockerImageName.parse("postgres:18.4-alpine"))
                .withCopyFileToContainer(
                        MountableFile.forClasspathResource("init.sql"),
                        "/docker-entrypoint-initdb.d/init.sql");
    }

    @Bean
    public RedisContainer redisContainer() {
        return createRedisContainer();
    }

    private static RedisContainer createRedisContainer() {
        return new RedisContainer(DEFAULT_IMAGE_NAME.withTag(DEFAULT_TAG));
    }

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        PostgreSQLContainer postgres = createPostgresContainer();
        postgres.start();
        RedisContainer redis = createRedisContainer();
        redis.start();

        TestPropertyValues.of(
                "r2dbc.host=" + postgres.getHost(),
                "r2dbc.port=" + postgres.getFirstMappedPort(),
                "r2dbc.username=" + postgres.getUsername(),
                "r2dbc.password=" + postgres.getPassword(),
                "r2dbc.database=" + postgres.getDatabaseName(),
                "redis.host=" + redis.getHost(),
                "redis.port=" + redis.getFirstMappedPort()
        ).applyTo(context.getEnvironment());
    }

}
