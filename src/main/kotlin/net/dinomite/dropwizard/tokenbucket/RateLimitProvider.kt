package net.dinomite.dropwizard.tokenbucket

interface RateLimitProvider {
    fun isOverLimit(id: String, cost: Long): Boolean
}
