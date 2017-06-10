package net.dinomite.dropwizard.tokenbucket

import io.dropwizard.Bundle
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment

class RateLimitBundle(internal var provider: RateLimitProvider) : Bundle {
    override fun initialize(bootstrap: Bootstrap<*>) {}

    override fun run(environment: Environment) {
        environment.jersey().register(RateLimitFilter(provider))
    }
}
