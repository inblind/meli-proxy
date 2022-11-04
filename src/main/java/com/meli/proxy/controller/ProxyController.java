package com.meli.proxy.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.meli.proxy.model.ApiResponse;
import com.meli.proxy.service.MeliService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/")
public class ProxyController {

    @Autowired
    MeliService server;

    @GetMapping("/{first}")
    private ApiResponse getResource(@PathVariable String first) throws ExecutionException, InterruptedException, JsonProcessingException {
        return server.getApiInformation(new ArrayList<>() {
            {add(first); }
        });
    }

    @GetMapping("/{first}/{second}")
    private ApiResponse getResourceSecond(@PathVariable String first, @PathVariable String second) throws ExecutionException, InterruptedException, JsonProcessingException {
        List<String> paths = new ArrayList<>();
        paths.add(first);
        paths.add(second);
        return server.getApiInformation(paths);
    }

    @GetMapping("/{first}/{second}/{third}")
    private ApiResponse getResourceThird(@PathVariable String first, @PathVariable String second, @PathVariable String third) throws ExecutionException, InterruptedException, JsonProcessingException {
        List<String> paths = new ArrayList<>();
        paths.add(first);
        paths.add(second);
        paths.add(third);
        return server.getApiInformation(paths);
    }

    @GetMapping("/{first}/{second}/{third}/{fourth}")
    private ApiResponse getResourceThird(@PathVariable String first, @PathVariable String second, @PathVariable String third, @PathVariable String fourth) throws ExecutionException, InterruptedException, JsonProcessingException {
        List<String> paths = new ArrayList<>();
        paths.add(first);
        paths.add(second);
        paths.add(third);
        paths.add(fourth);
        return server.getApiInformation(paths);
    }
}