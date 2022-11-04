package com.meli.proxy.interceptors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.meli.proxy.model.Statistics;
import com.meli.proxy.repository.RedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class Interceptor implements HandlerInterceptor {

    @Autowired
    RedisRepository repo;
    private ObjectMapper mapper;
    @Value("${rate.limit}")
    private int rateLimit;

    @Value("${rate.path-limit}")
    private int pathLimit;

    @Value("${rate.key-requests-today}")
    private String keyRequestsToday;

    public Interceptor(){
            this.mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String key =  String.format("statistics_%s", request.getRemoteAddr().replace(":", "-"));
        HashMap<String, String> dataSet = repo.get(key);

        int requests = getRequests(dataSet);
        int pathRequests = getPathRequests(request.getRequestURI(), dataSet);
        if(requests > rateLimit || pathRequests > pathLimit){
            handleError(response);
            return false;
        }

        repo.save(key, createStatisticMap(request, dataSet, requests));
        return true;
    }

    private int getPathRequests(String path, HashMap<String, String> dataSet) throws JsonProcessingException {
        if(dataSet.get(path) != null){
            List<Statistics> list = mapper.readValue(dataSet.get(path), new TypeReference<ArrayList<Statistics>>(){});
            return list.size();
        }

        return 0;
    }

    private HashMap<String, String> createStatisticMap(HttpServletRequest request, HashMap<String, String> dataSet, int requests) throws JsonProcessingException {
        List<Statistics> list = new ArrayList<>();

        Statistics statics = Statistics.builder()
                .ip(request.getRemoteAddr())
                .path(request.getRequestURI())
                .dateTime(LocalDateTime.now())
                .build();
        HashMap<String, String> map = new HashMap<>();
        if(dataSet.get(request.getRequestURI()) != null){
            list = mapper.readValue(dataSet.get(request.getRequestURI()), new TypeReference<ArrayList<Statistics>>(){});
        }
        list.add(statics);
        map.put(request.getRequestURI(), mapper.writeValueAsString(list));
        map.put(keyRequestsToday, mapper.writeValueAsString(requests + 1));
        return map;
    }

    private void handleError(HttpServletResponse response) throws IOException {
        response.getWriter().write("{error: 'limit maxed out'");
        response.setContentType("application/json");
        response.sendError(HttpStatus.UNAUTHORIZED.value(), "Rate limit maxed out");
    }

    private int getRequests(HashMap<String, String> dataSet){
        String requestsToday = dataSet.get(keyRequestsToday);
        if(requestsToday != null)
            return Integer.parseInt(requestsToday);

        return 0;
    }
}