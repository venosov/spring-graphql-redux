package com.example.security

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.access.annotation.Secured
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.atomic.AtomicInteger

@EnableReactiveMethodSecurity
@SpringBootApplication
class SecurityApplication {
	@Bean
	fun authentication(): MapReactiveUserDetailsService {
		val users = mapOf("jlong" to arrayOf("USER"),
			"rwinch" to arrayOf("ADMIN", "USER"))
			.map { e ->
				User.withDefaultPasswordEncoder()
					.username(e.key)
					.password("pw")
					.roles(*e.value)
					.build()
			}.toList()

		return MapReactiveUserDetailsService(users)
	}

	@Bean
	fun authorization(http: ServerHttpSecurity): SecurityWebFilterChain? {
		return http
			.csrf(CsrfSpec::disable)
			.authorizeExchange { ae: AuthorizeExchangeSpec ->
				ae.anyExchange().permitAll()
			}
			.httpBasic(Customizer.withDefaults())
			.build()
	}
}

fun main(args: Array<String>) {
	runApplication<SecurityApplication>(*args)
}

@Controller
internal class SecureGraphqlController(private val crm: CrmService) {
	@MutationMapping
	fun insert(@Argument name: String): Mono<Customer> {
		return crm.insert(name)
	}

	@QueryMapping
	fun customerById(@Argument id: Int): Mono<Customer> {
		return crm.getCustomerById(id)
	}
}

@Service
internal class CrmService {
	private val db = mutableMapOf<Int, Customer>()
	private val id = AtomicInteger()

	@Secured("ROLE_USER")
	fun getCustomerById(id: Int): Mono<Customer> {
		val customer = db[id]

		return if(customer == null) return Mono.empty() else Mono.just(customer)
	}

	@PreAuthorize("hasRole('ADMIN')")
	fun insert(name: String?): Mono<Customer> {
		val newCustomer = Customer(id.incrementAndGet(), name!!)
		db[newCustomer.id] = newCustomer
		return Mono.just(newCustomer)
	}
}

data class Customer(val id: Int, val name: String)
