package de.hrw.xilab.spring.util.exceptions;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
public class ErrorResponse {
    private String message;
    private int code;
    private String status;
    private List<String> stacktrace;
    private Object data;

    public ErrorResponse(){

    }

    public ErrorResponse(HttpStatus status, String message){
        this();
        this.code = status.value();
        this.message = message;
    }

    public ErrorResponse(HttpStatus status, String message, List<String> stacktrace){
        this(status, message);
        this.stacktrace = stacktrace;
    }

    public ErrorResponse(HttpStatus status, String message, List<String> stacktrace, Object data){
        this(status, message, stacktrace);
        this.data = data;
    }
}
