package com.example.rsocket

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SubscriptionMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import java.time.Duration
import java.time.Instant
import java.util.stream.Stream


@SpringBootApplication
class RsocketApplication

fun main(args: Array<String>) {
    runApplication<RsocketApplication>(*args)
}

@Controller
internal class GreetingsController {
    @SubscriptionMapping
    fun greetings(): Flux<Greeting> {
        return Flux
            .fromStream(Stream.generate { Greeting("Hello, world @ ${Instant.now()}!") })
            .delayElements(Duration.ofSeconds(1))
            .take(10)
    }

    @QueryMapping
    fun greeting(): Greeting {
        return Greeting("Hello, world!")
    }
}

data class Greeting(val greeting: String)
