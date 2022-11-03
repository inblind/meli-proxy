package com.meli.proxy.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meli.proxy.redis.RedisConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;

@Repository
public class RedisRepository {
    private JedisPool jedis;

    public RedisRepository(@Autowired RedisConfiguration configuration){
        this.jedis = configuration.getJedis();
    }

    public void save(String key, HashMap<String, String> data){
        try (Jedis jedis = this.jedis.getResource()) {
            jedis.hmset(key, data);
        }
    }

    public HashMap<String, String> get(String key){
        try (Jedis jedis = this.jedis.getResource()) {
            return (HashMap<String, String>) jedis.hgetAll(key);
        }
    }

}
