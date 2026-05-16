package com.example.demo


import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.beans.factory.annotation.Autowired
import reactor.test.StepVerifier

@SpringBootTest(classes = [DemoApplication::class, TestcontainersConfiguration::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTests {

    @Autowired
    private lateinit var client: WebClient

    @Test
    fun `get all posts`() {
        client.get()
            .uri("/posts")
            .accept(MediaType.APPLICATION_JSON)
            .exchangeToFlux {
                assertThat(it.statusCode()).isEqualTo(HttpStatus.OK)
                it.bodyToFlux(Post::class.java)
            }
            .`as` { StepVerifier.create(it) }
            .expectNextCount(2)
            .verifyComplete()
    }

}
