package com.nurujjamanpollob.contextseven.eazeeditorplugin.core.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponse {
    private String error;
    private String message;
}
