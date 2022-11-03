package com.meli.proxy.redis;

import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;

@Component
public class RedisConfiguration {

    @Value("${redis.host}")
    private String host;

    @Value("${redis.port}")
    private int port;

    @Value("${redis.user}")
    private String user;

    @Value("${redis.password}")
    private String password;


    public JedisPool getJedis(){
        if(!StringUtil.isNullOrEmpty(this.user) && !StringUtil.isNullOrEmpty(this.password))
            return new JedisPool(this.host, this.port, this.user, this.password);

        return new JedisPool(this.host, this.port);
    }
//    JedisPool jedisConnectionFactory() {
//        JedisPool pool = new JedisPool("localhost", 6379);
//        return jedisConFactory;
//    }
//
//    public RedisTemplate<String, Object> redisTemplate() {
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(jedisConnectionFactory());
//        return template;
//    }

}
