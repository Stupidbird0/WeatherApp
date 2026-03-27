package com.weather.spring.config;

import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CachingProvider provider = Caching.getCachingProvider();
        javax.cache.CacheManager cacheManager = provider.getCacheManager();
        
        // Configurazione della cache per WeatherData
        javax.cache.configuration.Configuration<String, Object> weatherCacheConfig = 
            Eh107Configuration.fromEhcacheCacheConfiguration(
                CacheConfigurationBuilder.newCacheConfigurationBuilder(
                    String.class, 
                    Object.class,
                    ResourcePoolsBuilder.heap(1000))
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMinutes(10)))
                .build()
            );
        
        // Crea la cache se non esiste già
        try {
            cacheManager.createCache("weatherCache", weatherCacheConfig);
        } catch (javax.cache.CacheException e) {
            // Cache già esistente, la recupero
            cacheManager.getCache("weatherCache", String.class, Object.class);
        }
        
        return new JCacheCacheManager(cacheManager);
    }
}