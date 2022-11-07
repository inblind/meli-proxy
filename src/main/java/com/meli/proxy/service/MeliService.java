package com.meli.proxy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meli.proxy.model.ApiResponse;
import com.meli.proxy.repository.RedisRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;


@Service
public class MeliService {

    @Value("${meli.api.url}")
    private String url;
    private RedisRepository redisRepo;
    private ObjectMapper mapper;

    private String QUERY_STRING = "%s?%s";
    private String HTTP_PATH_STRING = "https://";
    private String PAYLOAD_STRING = "payload";


    public MeliService(@Autowired RedisRepository redisRepo){
        this.redisRepo = redisRepo;
        this.mapper = new ObjectMapper();
    }

    public ApiResponse getApiInformation(HttpServletRequest request, Object body) throws JsonProcessingException {
        String uri = request.getRequestURI();
        String payload = null;

        if(StringUtils.isNotEmpty(request.getQueryString()))
            uri = String.format(QUERY_STRING, uri, request.getQueryString());

        String redisKey = uri.replace(HTTP_PATH_STRING, StringUtils.EMPTY);

        ApiResponse response = getRedisData(redisKey);
        if(response != null)
            return response;

        RestTemplate restTemplate = new RestTemplate();
        if(request.getMethod().equalsIgnoreCase(HttpMethod.GET.toString()))
            payload = restTemplate.getForObject(url + uri, String.class);

        if(request.getMethod().equalsIgnoreCase(HttpMethod.POST.toString()))
            payload = restTemplate.postForObject(url + uri, body, String.class);

        response = ApiResponse.builder().payLoad(mapper.readTree(payload)).build();
        this.setRedisData(redisKey, response);

        return response;
    }

    private ApiResponse getRedisData(String key) throws JsonProcessingException {
        HashMap<String, String> data = this.redisRepo.get(key);
        if(data.get(PAYLOAD_STRING) == null) return null;

        JsonNode payload = mapper.readTree(data.get(PAYLOAD_STRING));
        return ApiResponse.builder().payLoad(payload).build();
    }

    private void setRedisData(String key, ApiResponse response){
        HashMap<String, String> data = new HashMap<>();
        data.put(PAYLOAD_STRING, response.payLoad.toString());
        this.redisRepo.save(key, data);
    }

}
