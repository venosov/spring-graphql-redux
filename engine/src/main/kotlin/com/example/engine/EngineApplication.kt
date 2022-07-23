package com.example.engine

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.graphql.execution.RuntimeWiringConfigurer
import org.springframework.stereotype.Service


@SpringBootApplication
class EngineApplication {
	@Bean
	fun runtimeWiringConfigurer(crm: CrmService): RuntimeWiringConfigurer {
		return RuntimeWiringConfigurer { builder ->
			builder.type("Customer") {
				it.dataFetcher("profile") { e -> crm.getProfileFor(e.getSource()) }
			}
			builder.type("Query") {
				it.dataFetcher("customerById") {
						env -> crm.getCustomerById(env.getArgument<String>("id").toInt())
				}.dataFetcher("customers") { crm.getCustomers() }
			}
		}
	}
}

fun main(args: Array<String>) {
	runApplication<EngineApplication>(*args)
}

data class Customer(val id: Int, val name: String)

data class Profile(val id: Int, val customerId: Int)

@Service
class CrmService {
	fun getProfileFor(customer: Customer): Profile {
		return Profile(customer.id, customer.id)
	}

	fun getCustomerById(id: Int): Customer {
		return Customer(id, if (Math.random() > .5) "A" else "B")
	}

	fun getCustomers(): Collection<Customer> {
		return listOf(
			Customer(1, "A"),
			Customer(2, "B")
		)
	}
}
