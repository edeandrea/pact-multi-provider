package com.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslRootValue;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit.MockServerConfig;
import au.com.dius.pact.consumer.junit5.ForProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import io.restassured.RestAssured;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(pactVersion = PactSpecVersion.V4)
@MockServerConfig(providerName = "rest-heroes", port = PactProviderTest.HEROES_PORT, hostInterface = "localhost")
@MockServerConfig(providerName = "rest-villains", port = PactProviderTest.VILLAINS_PORT, hostInterface = "localhost")
public class PactProviderTest {
	static final String HEROES_PORT = "51234";
	static final String VILLAINS_PORT = "51235";

	@Pact(consumer = "rest-fights", provider = "rest-heroes")
  public V4Pact helloHeroesPact(PactDslWithProvider builder) {
    return builder
      .uponReceiving("A hello request")
        .path("/hello/hero")
        .method("GET")
      .willRespondWith()
        .headers(Map.of("Content-Type", "text/plain"))
        .status(200)
        .body(PactDslRootValue.stringMatcher(".+", "Hello Heroes!"))
      .toPact(V4Pact.class);
  }

  @Pact(consumer = "rest-fights", provider = "rest-villains")
  public V4Pact helloVillainsPact(PactDslWithProvider builder) {
    return builder
      .uponReceiving("A hello request")
        .path("/hello/villain")
        .method("GET")
      .willRespondWith()
        .headers(Map.of("Content-Type", "text/plain"))
        .status(200)
        .body(PactDslRootValue.stringMatcher(".+", "Hello Villains!"))
      .toPact(V4Pact.class);
  }

	@Test
	@PactTestFor(pactMethods = { "helloHeroesPact", "helloVillainsPact" })
	public void helloHeroesAndVillains(@ForProvider("rest-heroes") MockServer heroesMockServer, @ForProvider("rest-villains") MockServer villainsMockServer) {
		assertThat(heroesMockServer.getPort())
			.isEqualTo(Integer.parseInt(HEROES_PORT));

		assertThat(villainsMockServer.getPort())
			.isEqualTo(Integer.parseInt(VILLAINS_PORT));

		testHelloHeroes();
		testHelloVillains();
	}

	@Test
	@PactTestFor(pactMethod = "helloHeroesPact")
	public void helloHeroes(@ForProvider("rest-heroes") MockServer mockServer) {
		assertThat(mockServer.getPort())
			.isEqualTo(Integer.parseInt(HEROES_PORT));

		testHelloHeroes();
	}

	@Test
	@PactTestFor(pactMethod = "helloVillainsPact")
	public void helloVillains(@ForProvider("rest-villains") MockServer mockServer) {
		assertThat(mockServer.getPort())
			.isEqualTo(Integer.parseInt(VILLAINS_PORT));

		testHelloVillains();
	}

	private void testHelloHeroes() {
		RestAssured.given()
			.port(Integer.parseInt(HEROES_PORT))
			.when().get("/hello/hero").then()
			.statusCode(200)
			.contentType("text/plain")
			.body(is("Hello Heroes!"));
	}

	private void testHelloVillains() {
		RestAssured.given()
			.port(Integer.parseInt(VILLAINS_PORT))
			.when().get("/hello/villain").then()
			.statusCode(200)
			.contentType("text/plain")
			.body(is("Hello Villains!"));
	}
}
