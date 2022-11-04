package com.meli.proxy.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Statistics {
    private String path;
    private String ip;
    private LocalDateTime dateTime;
}
