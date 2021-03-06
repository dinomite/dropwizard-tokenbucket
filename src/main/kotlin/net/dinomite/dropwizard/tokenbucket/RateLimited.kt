package net.dinomite.dropwizard.tokenbucket

import javax.ws.rs.NameBinding

@NameBinding
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class RateLimited(val cost: Long = 1)
