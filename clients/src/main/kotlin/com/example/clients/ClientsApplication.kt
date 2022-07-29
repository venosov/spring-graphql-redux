package com.example.clients

import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.graphql.client.HttpGraphQlClient
import org.springframework.graphql.client.RSocketGraphQlClient

@SpringBootApplication
class ClientsApplication {
	@Bean
	fun httpGraphQlClient(): HttpGraphQlClient {
		return HttpGraphQlClient.builder().url("http://127.0.0.1:8080/graphql").build()
	}

	@Bean
	fun rSocketGraphQlClient(builder: RSocketGraphQlClient.Builder<*>): RSocketGraphQlClient {
		return builder.tcp("127.0.0.1", 9191).route("graphql").build()
	}

	@Bean
	fun applicationRunner(
		rsocket: RSocketGraphQlClient,
		http: HttpGraphQlClient
	): ApplicationRunner {
		return ApplicationRunner {
			val httpRequestDocument: String = """query {
					 customerById(id:1){ 
					  id, name
					 }
					}"""
			http.document(httpRequestDocument).retrieve("customerById").toEntity(Customer::class.java)
				.subscribe(System.out::println)
			val rsocketRequestDocument = """subscription {
					 greetings { greeting } 
					}"""
			rsocket.document(rsocketRequestDocument)
				.retrieveSubscription("greetings")
				.toEntity(Greeting::class.java)
				.subscribe(System.out::println)
		}
	}

}

fun main(args: Array<String>) {
	runApplication<ClientsApplication>(*args)
}

data class Greeting(val greeting: String)
data class Customer(val id: Int, val name: String)
