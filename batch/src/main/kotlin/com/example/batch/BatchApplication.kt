package com.example.batch

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.graphql.data.method.annotation.BatchMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller


@SpringBootApplication
class BatchApplication

fun main(args: Array<String>) {
	runApplication<BatchApplication>(*args)
}

@Controller
internal class BatchController {
	@QueryMapping
	fun customers(): Collection<Customer> {
		return listOf(Customer(1, "A"), Customer(2, "B"))
	}

	@BatchMapping
	fun account(customers: List<Customer>): Map<Customer, Account> {
		println("calling account for " + customers.size + " customers.")

		return customers.associateWith { Account(it.id) }
	}
}

data class Account(val id: Int)
data class Customer(val id: Int, val name: String)
