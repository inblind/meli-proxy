package com.meli.proxy.redis;

//import io.netty.util.internal.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
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
        if(StringUtils.isNotEmpty(this.user) && StringUtils.isNotEmpty(this.password))
            return new JedisPool(this.host, this.port, this.user, this.password);

        return new JedisPool(this.host, this.port);
    }
}
