package com.demo.webchat;

import org.springframework.web.bind.annotation.*;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnection;

import java.util.*;

@RestController
public class InfoController {

    private final RedisConnectionFactory redisConnectionFactory;

    public InfoController(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @GetMapping("/info")
    public Map<String, Object> getInfo() {
        Map<String, Object> map = new LinkedHashMap<>();
        try (RedisConnection conn = redisConnectionFactory.getConnection()) {
            Properties props = conn.info();
            props.forEach((k, v) -> map.put(String.valueOf(k), String.valueOf(v)));
            map.put("redis", true);
        } catch (Exception e) {
            map.put("redis", false);
            map.put("error", e.getMessage());
        }
        return map;
    }
}
