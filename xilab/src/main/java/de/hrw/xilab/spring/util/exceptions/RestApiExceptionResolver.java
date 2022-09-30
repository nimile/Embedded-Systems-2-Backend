package de.hrw.xilab.spring.util.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.*;
import java.util.function.Function;

@ControllerAdvice
public final class RestApiExceptionResolver {

    @ExceptionHandler(GenericObjectException.class)
    public ResponseEntity<ErrorResponse> handleGenericObjectException(GenericObjectException ex){
        HttpStatus status = HttpStatus.I_AM_A_TEAPOT;
        return ResponseEntity.status(status).body(buildResponse(ex, status, Optional.of(ex.getData())));
    }

    @ExceptionHandler(ElementAlreadyExist.class)
    public ResponseEntity<ErrorResponse> handleElementAlreadyExist(ElementAlreadyExist ex){
        HttpStatus status = HttpStatus.ALREADY_REPORTED;
        return ResponseEntity.status(status).body(buildResponse(ex, status, Optional.empty()));
    }

    @ExceptionHandler({ElementDoesNotExist.class, NoSuchElementException.class})
    public ResponseEntity<ErrorResponse> handleElementNotFound(Exception ex){
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(buildResponse(ex, status, Optional.empty()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex){
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status).body(buildResponse(ex, status, Optional.empty()));
    }



    private ErrorResponse buildResponse(Exception ex, HttpStatus status, Optional<Object> data){
        ErrorResponse result = new ErrorResponse();
        result.setCode(status.value());
        result.setMessage(ex.getMessage());
        result.setStatus(status.getReasonPhrase());
        result.setStacktrace(sanitizeStackTrace(ex));
        data.ifPresent(result::setData);
        return result;
    }

    private List<String> sanitizeStackTrace(Exception ex){
        Function<StackTraceElement, String> convert =
                stackTraceElement -> String.format("%s#%s(%s) | Full class name: %s",
                        stackTraceElement.getFileName(),
                        stackTraceElement.getMethodName(),
                        stackTraceElement.getLineNumber(),
                        stackTraceElement.getClassName());

        return Arrays.stream(ex.getStackTrace())
                .filter(element->element.getClassName().contains("xilab"))
                .map(convert)
                .toList();
    }
}