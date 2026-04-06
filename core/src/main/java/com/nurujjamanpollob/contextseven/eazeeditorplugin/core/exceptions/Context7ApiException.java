package com.nurujjamanpollob.contextseven.eazeeditorplugin.core.exceptions;

import lombok.Getter;

@Getter
public class Context7ApiException extends RuntimeException {
    private final int statusCode;
    private final String error;

    public Context7ApiException(int statusCode, String error, String message) {
        super(message);
        this.statusCode = statusCode;
        this.error = error;
    }
}
