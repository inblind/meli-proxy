package com.meli.proxy.interceptors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.meli.proxy.model.Statistics;
import com.meli.proxy.repository.RedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.HashMap;

@Slf4j
@Component
public class Interceptor implements HandlerInterceptor {

    @Autowired
    RedisRepository repo;
    private ObjectMapper mapper;

    @Value("${rate.free-limit}")
    private int freeRateLimit;

    public Interceptor(){
            this.mapper = new ObjectMapper()
                    .registerModule(new JavaTimeModule());
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        repo.save(String.format("statistics_%s", request.getRemoteAddr().replace(":", "-")), createStatisticMap(request));
        log.info("Request intercepted: " + request.getHeader("Authorization"));
        return true;
    }

    private HashMap<String, String> createStatisticMap(HttpServletRequest request) throws JsonProcessingException {
        Statistics statics = Statistics.builder()
                .ip(request.getRemoteAddr())
                .path(request.getRequestURI())
                .dateTime(LocalDateTime.now())
                .build();
        HashMap<String, String> map = new HashMap<>();
        map.put(String.valueOf(java.util.UUID.randomUUID()), mapper.writeValueAsString(statics));
        return map;
    }

}