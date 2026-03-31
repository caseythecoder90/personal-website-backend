package com.caseyquinn.personal_website.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Map;

import static com.caseyquinn.personal_website.constants.CacheConstants.*;

/**
 * Redis cache configuration with per-cache TTLs and JSON serialization.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Creates a Redis cache manager with per-cache TTL configurations.
     *
     * @param connectionFactory the Redis connection factory
     * @return configured Redis cache manager
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.activateDefaultTyping(
                objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        GenericJackson2JsonRedisSerializer jsonSerializer =
                new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer))
                .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> cacheConfigurations = Map.of(
                CACHE_PROJECTS, defaultConfig.entryTtl(Duration.ofMinutes(10)),
                CACHE_TECHNOLOGIES, defaultConfig.entryTtl(Duration.ofMinutes(30)),
                CACHE_CERTIFICATIONS, defaultConfig.entryTtl(Duration.ofMinutes(30)),
                CACHE_BLOG_POSTS, defaultConfig.entryTtl(Duration.ofMinutes(20)),
                CACHE_BLOG_CATEGORIES, defaultConfig.entryTtl(Duration.ofMinutes(30)),
                CACHE_BLOG_TAGS, defaultConfig.entryTtl(Duration.ofMinutes(30)),
                CACHE_RESUME, defaultConfig.entryTtl(Duration.ofMinutes(60))
        );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
