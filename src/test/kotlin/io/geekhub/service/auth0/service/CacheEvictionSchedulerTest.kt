package io.geekhub.service.auth0.service

import assertk.assertThat
import assertk.assertions.isEqualByComparingTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import io.geekhub.service.shared.annotation.IntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.CacheManager
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

@IntegrationTest
internal class CacheEvictionSchedulerTest(
    @Autowired val cacheEvictionScheduler: CacheEvictionScheduler,
    @Autowired val cacheManager: CacheManager
) {

    @Value("\${app.cache.evictionInterval}")
    private lateinit var evictionInterval: Duration

    @Test
    fun testCacheMechanism() {

        println("Eviction interval: $evictionInterval")

        assertThat(cacheEvictionScheduler.getTestCache()).isEqualByComparingTo("test")

        val cache = cacheManager.getCache("test")?.nativeCache as ConcurrentHashMap<*, *>

        assertThat(cache.containsKey("getTestCache")).isTrue()

        cacheEvictionScheduler.evictTestCache()

        assertThat(cache.containsKey("getTestCache")).isFalse()

        cacheEvictionScheduler.getTestCache()

        assertThat(cache.containsKey("getTestCache")).isTrue()

        cacheManager.getCache("test")?.clear()

        assertThat(cache.containsKey("getTestCache")).isFalse()
    }
}