package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {DemoApplication.class, ContainersConfig.class})
@ActiveProfiles("test")
@Slf4j
public class PostRepositoryCypherTest {

    @Autowired
    PostRepository posts;

    @BeforeEach
    public void setup() {
        log.debug("running setup for cypher test...");
        this.posts.deleteAll()
                .then()
                .thenMany(testSaveMethod())
                .then()
                .thenMany(testFoundMethod())
                .blockLast(Duration.ofSeconds(10));
    }

    private Flux<Post> testSaveMethod() {
        var data = Stream.of("Post one", "Post two")
                .map(title -> Post.builder().title(title).content("The content of " + title).build())
                .collect(Collectors.toList());
        return Flux.fromIterable(data)
                .flatMap(it -> this.posts.save(it));
    }

    private Flux<Post> testFoundMethod() {
        return this.posts.findAll();
    }

    @AfterEach
    void teardown() {
        // cleanup if needed
    }

    @Test
    void testAllPosts() {
        posts.findAll().sort(Comparator.comparing(Post::getTitle))
                .as(StepVerifier::create)
                .consumeNextWith(p -> assertEquals("Post one", p.getTitle()))
                .consumeNextWith(p -> assertEquals("Post two", p.getTitle()))
                .verifyComplete();
    }

}
