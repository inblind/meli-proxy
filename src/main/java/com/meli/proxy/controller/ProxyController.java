package com.meli.proxy.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.meli.proxy.model.ApiResponse;
import com.meli.proxy.service.MeliService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/proxy")
public class ProxyController {

    @Autowired
    MeliService server;

    @GetMapping("/{resource}/{id}")
    private ApiResponse getEmployeeById(@PathVariable String resource, @PathVariable String id) throws ExecutionException, InterruptedException, JsonProcessingException {
        return server.getApiInformation(resource, id, null);
    }


}