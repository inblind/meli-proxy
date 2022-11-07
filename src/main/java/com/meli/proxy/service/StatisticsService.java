package com.meli.proxy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.meli.proxy.model.Statistics;
import com.meli.proxy.repository.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;

@Service
public class StatisticsService {

    private RedisRepository redisRepo;
    private ObjectMapper mapper;

    public StatisticsService(@Autowired RedisRepository redisRepo){
        this.redisRepo = redisRepo;
        this.mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    public HashMap<String, String> getStatistics() {
        return this.redisRepo.get("statistics");
    }

    public void saveStatistics(HttpServletRequest request, double msTime, Boolean success, String exceptionMessage) throws JsonProcessingException {
        Statistics statistics = Statistics.builder()
                .ip(request.getRemoteAddr())
                .msTime(msTime)
                .success(success)
                .exception(exceptionMessage)
                .path(request.getRequestURI())
                .dateTime(LocalDateTime.now())
                .build();
        HashMap<String, String> data = new HashMap<String, String>();
        data.put(String.valueOf(java.util.UUID.randomUUID()), this.mapper.writeValueAsString(statistics));
        redisRepo.save("statistics", data);
    }
}
