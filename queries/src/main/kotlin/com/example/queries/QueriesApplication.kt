package com.example.queries

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.List


@SpringBootApplication
class QueriesApplication

fun main(args: Array<String>) {
	runApplication<QueriesApplication>(*args)
}

@Controller
internal class GreetingsController {
	@QueryMapping
	fun customerById(@Argument id: Int): Customer {
		return Customer(id, if (Math.random() > .5) "A" else "B")
	}

	private val customerList = listOf(Customer(1, "A"), Customer(2, "B"))

	@QueryMapping
	fun helloWithName(@Argument name: String): String {
		return "Hello, $name!"
	}

	@QueryMapping
	//    @SchemaMapping(typeName = "Query", field = "hello")
	fun  hello(): String {
		return "Hello, world!"
	}

	@QueryMapping
	fun customers(): Flux<Customer> {
		return Flux.fromIterable(customerList)
	}

	@SchemaMapping(typeName = "Customer")
	fun account(customer: Customer): Mono<Account> {
		return Mono.just(Account(customer.id))
	}
}

data class Account(val id: Int)
data class Customer(val id: Int, val name: String)