package com.sylleryum.spotifyhelper.controller;

import com.sylleryum.spotifyhelper.helper.TraceIdGenerator;
import com.sylleryum.spotifyhelper.model.ApiErrorEntity;
import com.sylleryum.spotifyhelper.model.exception.MissingArgumentException;
import com.sylleryum.spotifyhelper.model.exception.MissingTokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Optional;
import java.util.UUID;

import static com.sylleryum.spotifyhelper.helper.TraceIdGenerator.METHOD_NAME_NOT_FOUND;
import static com.sylleryum.spotifyhelper.helper.TraceIdGenerator.generate;

@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

    private static Logger logger = LoggerFactory.getLogger(ExceptionHandlerController.class);

    @ExceptionHandler(RestClientResponseException.class)
    public final ResponseEntity<ApiErrorEntity> handleResourceNotFoundException(
            RestClientResponseException e) {
        String traceId = TraceIdGenerator.writeError(RestClientResponseException.class, e);

        return new ResponseEntity<>(new ApiErrorEntity(traceId, e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingArgumentException.class)
    public final ResponseEntity<ApiErrorEntity> MissingArgumentExceptionException(
            MissingArgumentException e) {

        String traceId = TraceIdGenerator.writeError(MissingArgumentException.class, e);
        return new ResponseEntity<>(new ApiErrorEntity(e.getMessage(), traceId), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingTokenException.class)
    public final ResponseEntity<ApiErrorEntity> MissingTokenException(
            MissingTokenException e) {

        String traceId = TraceIdGenerator.writeError(MissingTokenException.class, e);
        return new ResponseEntity<>(new ApiErrorEntity(e.getMessage(), traceId), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ApiErrorEntity> handleOtherException(
            Exception e) {

        String traceId = TraceIdGenerator.writeError(this.getClass(), e);
        return new ResponseEntity<>(new ApiErrorEntity(e.getMessage(), traceId), HttpStatus.BAD_REQUEST);
    }

}
