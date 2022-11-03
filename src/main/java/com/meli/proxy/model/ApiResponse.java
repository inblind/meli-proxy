package com.meli.proxy.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;
import java.io.Serializable;

@Data
@Builder
public class ApiResponse implements Serializable {
    public JsonNode payLoad;
}
