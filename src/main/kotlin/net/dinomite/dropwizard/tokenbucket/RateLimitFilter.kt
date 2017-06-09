package net.dinomite.dropwizard.tokenbucket

import java.io.IOException
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.container.ResourceInfo
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@RateLimited
class RateLimitFilter(private val provider: RateLimitProvider) : ContainerRequestFilter {
    @Context
    private val resourceInfo: ResourceInfo? = null

    @Context
    private val request: HttpServletRequest? = null

    @Throws(IOException::class)
    override fun filter(context: ContainerRequestContext) {
        val id = "ip::" + request!!.remoteAddr
        val method = resourceInfo!!.resourceMethod
        val cost: Long
        if (method != null) {
            cost = method.getAnnotation(RateLimited::class.java).cost
        } else {
            cost = 1L
        }

        if (provider.isOverLimit(id, cost)) {
            context.abortWith(
                    Response
                            .status(429)
                            .entity("{\"error\":\"Too Many Requests\"}")
                            .type(MediaType.APPLICATION_JSON)
                            .build()
            )
        }
    }
}
