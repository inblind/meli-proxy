package com.meli.proxy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meli.proxy.model.ApiResponse;
import com.meli.proxy.repository.RedisRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
public class MeliService {

    @Value("${meli.api.url}")
    private String url;
    private RedisRepository redisRepo;
    private ObjectMapper mapper;

    public MeliService(@Autowired RedisRepository redisRepo){
        this.redisRepo = redisRepo;
        this.mapper = new ObjectMapper();
    }

    public ApiResponse getApiInformation(List<String> paths) throws ExecutionException, InterruptedException, JsonProcessingException {

        String uri = StringUtils.EMPTY;
        for (String x : paths) {
            uri = String.format("%s/%s", uri, x);
        }
        String redisKey = uri.replace("https://", "");
        ApiResponse response = getRedisData(redisKey);
        if(response != null)
            return response;

        RestTemplate restTemplate = new RestTemplate();
        String payload = restTemplate.getForObject(url + uri, String.class);
        response = ApiResponse.builder().payLoad(mapper.readTree(payload)).build();
        this.setRedisData(redisKey, response);

        return response;
    }

    private ApiResponse getRedisData(String key) throws JsonProcessingException {
        HashMap<String, String> data = this.redisRepo.get(key);
        if(data.get("payload") == null) return null;

        JsonNode payload = mapper.readTree(data.get("payload"));
        return ApiResponse.builder().payLoad(payload).build();
    }

    private void setRedisData(String key, ApiResponse response){
        HashMap<String, String> data = new HashMap<>();
        data.put("payload", response.payLoad.toString());
        this.redisRepo.save(key, data);
    }

}
