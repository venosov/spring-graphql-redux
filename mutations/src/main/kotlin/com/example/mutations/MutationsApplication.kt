package com.example.mutations

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import java.util.concurrent.atomic.AtomicInteger


@SpringBootApplication
class MutationsApplication

fun main(args: Array<String>) {
	runApplication<MutationsApplication>(*args)
}

@Controller
internal class MutationsController {
	private val db: MutableMap<Int, Customer> = mutableMapOf()
	private val id: AtomicInteger = AtomicInteger()

	@MutationMapping
	fun addCustomer(@Argument name: String): Customer {
		val id: Int = id.incrementAndGet()
		val value = Customer(id, name)
		db[id] = value

		return value
	}

	@QueryMapping
	fun customerById(@Argument id: Int): Customer? {
		return db[id]
	}
}

data class Customer(val id: Int, val name: String)
