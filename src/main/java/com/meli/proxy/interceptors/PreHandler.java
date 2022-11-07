package com.meli.proxy.interceptors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.meli.proxy.model.Tracking;
import com.meli.proxy.repository.RedisRepository;
import com.meli.proxy.security.Security;
import com.meli.proxy.service.StatisticsService;
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
public class PreHandler implements HandlerInterceptor {

    private ObjectMapper mapper;
    @Value("${rate.limit}")
    private int rateLimit;

    @Value("${rate.path-limit}")
    private int pathLimit;

    @Value("${rate.auth.limit}")
    private int authRateLimit;

    @Value("${rate.auth.path-limit}")
    private int authPathLimit;

    @Value("${rate.key-requests-today}")
    private String keyRequestsToday;

    private Security security;
    private String TRACKING_KEY_STRING = "tacking_%s";

    final
    RedisRepository repo;

    final
    StatisticsService statService;

    @Autowired
    public PreHandler(Security security, RedisRepository repo, StatisticsService statService){
            this.mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            this.security = security;
            this.repo = repo;
            this.statService = statService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String key =  String.format(TRACKING_KEY_STRING, request.getRemoteAddr().replace(":", "-"));
        HashMap<String, String> dataSet = repo.get(key);

        int requests = getRequests(dataSet);
        int pathRequests = getPathRequests(request.getRequestURI(), dataSet);
        boolean isAuth = security.isAuthenticated(request);
        if((!isAuth && (requests > rateLimit || pathRequests > pathLimit)) ||
                (isAuth && (requests > authRateLimit || pathRequests > authPathLimit))){
            handleError(request, response);
            return false;
        }

        repo.save(key, createTrackingMap(request, dataSet, requests));
        return true;
    }

    private int getPathRequests(String path, HashMap<String, String> dataSet) throws JsonProcessingException {
        if(dataSet.get(path) != null){
            List<Tracking> list = mapper.readValue(dataSet.get(path), new TypeReference<ArrayList<Tracking>>(){});
            return list.size();
        }

        return 0;
    }

    private HashMap<String, String> createTrackingMap(HttpServletRequest request, HashMap<String, String> dataSet, int requests) throws JsonProcessingException {
        List<Tracking> list = new ArrayList<>();

        Tracking statics = Tracking.builder()
                .ip(request.getRemoteAddr())
                .path(request.getRequestURI())
                .dateTime(LocalDateTime.now())
                .build();
        HashMap<String, String> map = new HashMap<>();
        if(dataSet.get(request.getRequestURI()) != null){
            list = mapper.readValue(dataSet.get(request.getRequestURI()), new TypeReference<ArrayList<Tracking>>(){});
        }
        list.add(statics);
        map.put(request.getRequestURI(), mapper.writeValueAsString(list));
        map.put(keyRequestsToday, mapper.writeValueAsString(requests + 1));
        return map;
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.getWriter().write("{error: 'limit maxed out'");
        response.setContentType("application/json");
        response.sendError(HttpStatus.UNAUTHORIZED.value(), "Rate limit maxed out");
        statService.saveStatistics(request, 0, false, "Rate limit maxed out");
    }

    private int getRequests(HashMap<String, String> dataSet){
        String requestsToday = dataSet.get(keyRequestsToday);
        if(requestsToday != null)
            return Integer.parseInt(requestsToday);

        return 0;
    }
}