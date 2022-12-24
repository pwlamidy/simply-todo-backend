package com.deepbluestudio.todobackend.exception;

import com.deepbluestudio.todobackend.payload.response.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = TokenRefreshException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<?> handleTokenRefreshException(TokenRefreshException exception, WebRequest request) {
        return ResponseHandler.generateResponse(
                exception.getMessage(),
                HttpStatus.FORBIDDEN,
                request.getDescription(false));
    }

    @ExceptionHandler({AuthenticationException.class})
    @ResponseBody
    public ResponseEntity<?> handleAuthenticationException(Exception exception, WebRequest request) {
        return ResponseHandler.generateResponse(
                exception.getMessage(),
                HttpStatus.UNAUTHORIZED,
                request.getDescription(false));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> globalExceptionHandling(Exception exception, WebRequest request) {
        logger.error(exception.getMessage());

        return ResponseHandler.generateResponse(
                exception.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request.getDescription(false));
    }
}
