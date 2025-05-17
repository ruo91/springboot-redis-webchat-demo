package com.demo.webchat;

import org.springframework.web.bind.annotation.*;
import org.springframework.data.redis.core.StringRedisTemplate;

@RestController
@RequestMapping("/kv")
public class KeyValueController {

    private final StringRedisTemplate redisTemplate;

    public KeyValueController(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/{key}")
    public String get(@PathVariable String key) {
        String val = redisTemplate.opsForValue().get(key);
        if (val == null) throw new RuntimeException("Not found");
        return val;
    }

    @PutMapping("/{key}")
    public void set(@PathVariable String key, @RequestBody String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @DeleteMapping("/{key}")
    public void del(@PathVariable String key) {
        redisTemplate.delete(key);
    }
}