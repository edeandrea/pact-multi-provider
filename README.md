Test project demonstrating that setting mock server ports isn't working correctly.

If you look at [`PactProviderTest.java`](src/test/java/com/test/PactProviderTest.java) you'll see that 2 mock servers are configured, similar to https://github.com/pact-foundation/pact-jvm/blob/master/consumer/junit5/src/test/java/au/com/dius/pact/consumer/junit5/MultiProviderWithStaticPortsTest.java#L26.

The problem is that the mock server configuration only seems to apply when a single test invokes multiple providers. In this case, the `PactProviderTest.helloHeroesAndVillains` test runs successfully.

The other tests which only use a single provider (`PactProviderTest.helloHeroes` and `PactProviderTest.helloVillains`) do not work because the `MockServer` does not respect the port provided in the `@MockServerConfig`.