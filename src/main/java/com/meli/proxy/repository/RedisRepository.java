//package com.meli.proxy.repository;
//
//import com.meli.proxy.redis.RedisConfiguration;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Repository;
//
//import java.util.HashMap;
//
//@Repository
//public class RedisRepository {
//
//    private RedisTemplate connection;
//
//    public RedisRepository(@Autowired RedisConfiguration configuration){
//        this.connection = configuration.redisTemplate();
//    }
//
//    public HashMap<String, String> getData(){
//        connection.opsForHash().has
//    }
//
//    public void saveData(String key, String value){
//
//    }
//
//}
