## Dropwizard Tokenbucket

Add rate limiting to [Dropwizard](http://www.dropwizard.io) using token buckets.  The buckets are stored in memory using a Guava [LoadingCache](https://google.github.io/guava/releases/21.0/api/docs/com/google/common/cache/LoadingCache.html).

This project uses [bucket4j](https://github.com/vladimir-bukhtoyarov/bucket4j) and [Hazelcast](https://hazelcast.org).

#### How to use

In your Dropwizard initialize method add the following:
```
@Override
public void initialize(Bootstrap<Configuration> bootstrap) {
    CacheBuilderSpec cacheBuilderSpec = new CacheBuilderSpec.parse("expireAfterWrite=1m")
    long overdraft = 50L;
    Refill refill = Refill.smooth(10, Duration.ofSeconds(5));

	RateLimitProvider rateLimitProvider = new TokenBucketRateLimitProvider(
	        cacheBuilderSpec, overdraft, refill);

	bootstrap.addBundle(new RateLimitBundle(rateLimitProvider));
}
```
Finally, add the following annotation to your rate limited routes:
```
@GET
@Path("index")
@RateLimited(cost = 1)
Response getIndex() {
	// etc.
}
```

#### Copyright and License
Adapted from ZIVVER B.V.'s [dropwizard-ratelimit](https://github.com/zivver/dropwizard-ratelimit)

Licensed under [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)
