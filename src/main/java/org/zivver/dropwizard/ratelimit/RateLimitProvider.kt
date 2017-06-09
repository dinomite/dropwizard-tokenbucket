package org.zivver.dropwizard.ratelimit

interface RateLimitProvider {
    fun isOverLimit(id: String, cost: Long): Boolean
}
