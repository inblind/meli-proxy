package com.meli.proxy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meli.proxy.model.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutionException;

@Component
public class MeliService {

    @Value("${meli.api.url}")
    private String url;


    public ApiResponse getApiInformation(String resource, String value, String queryString) throws ExecutionException, InterruptedException, JsonProcessingException {

        queryString = queryString != null ? queryString : "";

        String uriRequest = String.format("%s/%s/%s/%s", url, resource, value, queryString);

        RestTemplate restTemplate = new RestTemplate();
        String payload = restTemplate.getForObject(uriRequest, String.class);
        ObjectMapper mapper = new ObjectMapper();
        return ApiResponse.builder().payLoad(mapper.readTree(payload)).build();
    }

}
