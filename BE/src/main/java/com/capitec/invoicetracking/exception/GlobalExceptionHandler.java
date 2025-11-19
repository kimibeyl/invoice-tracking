package com.capitec.invoicetracking.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(ResourceNotFoundException.class)
    ProblemDetail handleConflicts(ResourceNotFoundException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler({HttpServerErrorException.InternalServerError.class})
    public ProblemDetail handleInternalServerErrorExceptions(
            HttpServerErrorException.InternalServerError ex) {

        log.error("Internal server error {}", ex.getMessage(), ex);

        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());

    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ProblemDetail handleException(Exception e) {
        log.error("Invald argument error {}", e.getMessage(), e);
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }
}


