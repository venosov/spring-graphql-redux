package com.example.testing

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.graphql.test.tester.HttpGraphQlTester
import org.springframework.test.web.reactive.server.WebTestClient


@SpringBootTest
class QueriesApplicationTests {
    @Test
    fun contextLoads() {
        val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:8080/graphql")
            .build()
        val tester = HttpGraphQlTester
            .create(client)
        val document = """
                {
                  customerById (id: 1) {
                   id, name
                  } 
                }
                """
        tester
            .document(document)
            .execute()
            .path("customerById")
            .entity(Customer::class.java)
            .matches { customer: Customer -> customer.name == "A" }
    }
}
