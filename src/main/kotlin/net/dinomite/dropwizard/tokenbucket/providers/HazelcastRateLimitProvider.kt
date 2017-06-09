package net.dinomite.dropwizard.tokenbucket.providers

import com.hazelcast.config.Config
import com.hazelcast.core.Hazelcast
import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket4j
import io.github.bucket4j.BucketConfiguration
import io.github.bucket4j.grid.GridBucketState
import io.github.bucket4j.grid.ProxyManager
import io.github.bucket4j.grid.jcache.JCache
import io.github.bucket4j.grid.jcache.JCacheBucketBuilder
import net.dinomite.dropwizard.tokenbucket.RateLimitProvider
import java.time.Duration
import java.util.concurrent.TimeUnit
import java.util.function.Supplier

class HazelcastRateLimitProvider @JvmOverloads constructor(permitsPerPeriod: Long, period: Long, timeUnit: TimeUnit, config: Config? = null) : RateLimitProvider {
    companion object {
        val BUCKET_CACHE = "my_buckets"
    }

    private val bucketManager: ProxyManager<String>
    private val configSupplier: Supplier<BucketConfiguration>

    constructor(permitsPerMinute: Long) : this(permitsPerMinute, 1, TimeUnit.MINUTES)

    init {
        val periodDuration = Duration.ofNanos(timeUnit.toNanos(period))
        val rateLimit = Bandwidth.simple(permitsPerPeriod, periodDuration)
        val configuration = Bucket4j.configurationBuilder()
                .addLimit(rateLimit)
                .buildConfiguration()
        this.configSupplier = Supplier { configuration }

        val hazelcastInstance = Hazelcast.newHazelcastInstance(config)
        val cacheManager = hazelcastInstance.cacheManager
        val cache = cacheManager.getCache<String, GridBucketState>(BUCKET_CACHE)
        this.bucketManager = Bucket4j.extension<JCacheBucketBuilder, JCache>(JCache::class.java).proxyManagerForCache(cache)
    }

    override fun isOverLimit(id: String, cost: Long): Boolean {
        val bucket = bucketManager.getProxy(id, configSupplier)
        return !bucket.tryConsume(cost)
    }
}
