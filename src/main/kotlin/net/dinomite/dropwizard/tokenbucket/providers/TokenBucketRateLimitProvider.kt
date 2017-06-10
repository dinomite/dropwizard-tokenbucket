package net.dinomite.dropwizard.tokenbucket.providers

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheBuilderSpec
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Bucket4j
import io.github.bucket4j.Refill
import net.dinomite.dropwizard.tokenbucket.RateLimitProvider

class TokenBucketRateLimitProvider constructor(cacheBuilderSpec: CacheBuilderSpec, overdraft: Long,
                                               refill: Refill) : RateLimitProvider {
    private val buckets: LoadingCache<String, Bucket> = CacheBuilder.from(cacheBuilderSpec)
            .build(object : CacheLoader<String, Bucket>() {
                override fun load(key: String): Bucket {
                    return Bucket4j.builder()
                            .addLimit(Bandwidth.classic(overdraft, refill))
                            .build()
                }
            })

    override fun isOverLimit(id: String, cost: Long): Boolean {
        return !buckets.get(id).tryConsume(cost)
    }
}
