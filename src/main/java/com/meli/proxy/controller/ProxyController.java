package com.meli.proxy.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.meli.proxy.model.ApiResponse;
import com.meli.proxy.service.MeliService;
import com.meli.proxy.service.StatisticsService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/")
public class ProxyController {

    final
    MeliService service;

    final
    StatisticsService statisticsService;

    public ProxyController(MeliService service, StatisticsService statisticsService) {
        this.service = service;
        this.statisticsService = statisticsService;
    }

    @GetMapping("/**")
    @PostMapping("/**")
    public Object allRequests(@RequestBody(required = false) Object body, HttpServletRequest request) throws JsonProcessingException {
        Object response = null;
        long startTime = System.currentTimeMillis();
        try{

            if(request.getRequestURI().equals("/statistics"))
                response = statisticsService.getStatistics();
            else
                response = service.getApiInformation(request, body);

            statisticsService.saveStatistics(request, System.currentTimeMillis() - startTime, true, null);

        }catch (Exception ex){
            statisticsService.saveStatistics(request, System.currentTimeMillis() - startTime, false, ex.getMessage());
        }

        return response;

    }
}