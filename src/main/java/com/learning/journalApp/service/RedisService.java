package com.learning.journalApp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Service class for interacting with Redis.
 */
@Service
@Slf4j
public class RedisService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * Retrieves an object from Redis by its key.
     *
     * @param key the key of the object to retrieve
     * @param entityClass the class of the object to retrieve
     * @param <T> the type of the object to retrieve
     * @return the object retrieved from Redis, or null if not found or an error occurs
     */
    public <T> T get(String key, Class<T> entityClass) {
        try {
            Object o = redisTemplate.opsForValue().get(key);
            if (o == null) {
                log.error("No value found in Redis for key: {}", key);
                return null;
            }
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(o.toString(), entityClass);
        } catch (JsonProcessingException e) {
            log.error("Error while fetching data from redis", e);
            return null;
        }
    }

    /**
     * Stores an object in Redis with a specified time-to-live (TTL).
     *
     * @param key the key under which the object will be stored
     * @param o the object to store
     * @param ttl the time-to-live for the object in seconds
     */
    public void set(String key,Object o, Long ttl) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String value = objectMapper.writeValueAsString(o);
            redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Error while setting data in redis", e);
        }
    }
}
