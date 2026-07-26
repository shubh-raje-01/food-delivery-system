package com.food_delivery_system.auth_service.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private Instant timestamp;

    private int status;

    private String error;       // HTTP reason phrase, e.g. "Bad Request"

    private String message;

    private String path;

    // populated only for bean-validation failures: fieldName -> message
    private Map<String, String> fieldErrors;
}