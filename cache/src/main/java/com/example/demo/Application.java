package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
@Slf4j
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner runner(PostRepository posts) {
        return args -> {
            posts.saveAll(
                            List.of(
                                    Post.of("What is new in Spring 6.1", "An introduction to new features from Spring 6.1"),
                                    Post.of("Spring Boot 3.2", "An introduction to new features in Spring Boot 3.2")
                            )
                    )
                    .thenMany(posts.findAll())
                    .subscribe(post -> log.debug("get the initialized data: {}", post));
        };
    }
}
