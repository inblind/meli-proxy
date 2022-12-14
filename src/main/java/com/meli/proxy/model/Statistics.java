package com.meli.proxy.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Builder
@Jacksonized
@ToString
public class Statistics {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String path;
    @JsonFormat(shape = JsonFormat.Shape.BOOLEAN)
    private Boolean success;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String ip;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private double msTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String exception;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String identifier;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;
}
