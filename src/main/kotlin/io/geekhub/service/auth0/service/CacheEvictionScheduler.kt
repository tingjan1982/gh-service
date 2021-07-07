package io.geekhub.service.auth0.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Duration

/**
 * Spring @Value:
 *
 * https://www.baeldung.com/spring-value-annotation
 */
@Component
class CacheEvictionScheduler(val cacheManager: CacheManager, @Value("\${app.cache.evictionInterval}") val evictionInterval: Duration) {

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(CacheEvictionScheduler::class.java)
    }

    /**
     * Reference current bean by bean name in SpEL.
     *
     * https://stackoverflow.com/questions/59771574/how-to-reference-a-bean-by-type-in-a-spel
     */
    @Scheduled(fixedDelayString = "#{@cacheEvictionScheduler.evictionInterval.toMillis()}")
    fun evictManagementTokenCache() {
        LOGGER.info("Evicting management token cache after the specified delay")

        cacheManager.getCache("managementToken")?.clear()
    }

    @Cacheable(value = ["test"], key = "#root.methodName")
    fun getTestCache(): String {
        return "test"
    }

    @CacheEvict(value = ["test"], allEntries = true)
    fun evictTestCache() {

    }

}