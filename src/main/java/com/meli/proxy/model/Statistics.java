package com.meli.proxy.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Builder
@Jacksonized
@ToString
public class Statistics {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String path;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String ip;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;
}
