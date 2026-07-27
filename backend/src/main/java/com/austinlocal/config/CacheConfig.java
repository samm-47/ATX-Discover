package com.austinlocal.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * In-memory cache manager. This is a drop-in swap for Redis if the
 * project later needs a shared cache across multiple instances —
 * for a single-instance MVP, ConcurrentMapCache gives the same
 * "avoid recomputing hot queries" benefit with zero infra cost.
 */
@Configuration
public class CacheConfig {

    public static final String NEARBY_PLACES_CACHE = "nearbyPlaces";

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(NEARBY_PLACES_CACHE);
    }
}
