package com.wyn.payment.exception;

import com.google.common.collect.Maps;
import com.wyn.payment.bean.RESTfulResponse;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.Map;

@RestControllerAdvice
public class DefaultExceptionHandler {

    public static final String FAILURE = "FAILURE";

    /**
     * Handles MissingServletRequestParameterException and similar exceptions
     * @return RESTfulResponse
     */
    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            UnsatisfiedServletRequestParameterException.class,
            HttpRequestMethodNotSupportedException.class,
            ServletRequestBindingException.class
    })
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public RESTfulResponse handleRequestException() {
        RESTfulResponse responseError = new RESTfulResponse();
        responseError.setStatus(FAILURE);
        responseError.setMessage("Request Error");
        responseError.setResponseType(HttpStatus.BAD_REQUEST.toString());

        return responseError;
    }

    /**
     * Handles HttpMediaTypeNotSupportedException
     * @param ex
     * @return RESTfulResponse
     * @throws IOException
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(value = HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public RESTfulResponse handleUnsupportedMediaTypeException(HttpMediaTypeNotSupportedException ex) throws IOException {
        RESTfulResponse responseError = new RESTfulResponse();
        responseError.setStatus(FAILURE);
        responseError.setMessage(ex.getLocalizedMessage());
        responseError.setResponseType(HttpStatus.UNSUPPORTED_MEDIA_TYPE.toString());

        return responseError;
    }

    /**
     * Handles MethodArgumentNotValidException
     * @param ex
     * @return RESTfulResponse
     * @throws IOException
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public RESTfulResponse handleValidationException(MethodArgumentNotValidException ex) throws IOException {
        RESTfulResponse responseError = new RESTfulResponse();
        responseError.setStatus(FAILURE);
        responseError.setMessage("Invalid request");
        responseError.setResponseType(HttpStatus.BAD_REQUEST.toString());
        responseError.setData(convertConstraintViolation(ex));

        return responseError;
    }

    /**
     * Handles generic Exception
     * @param ex
     * @return RESTfulResponse
     * @throws IOException
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public RESTfulResponse handleUncaughtException(Exception ex) throws IOException {
        RESTfulResponse responseError = new RESTfulResponse();
        responseError.setStatus(FAILURE);
        responseError.setMessage("Unknown Error");
        responseError.setResponseType(HttpStatus.INTERNAL_SERVER_ERROR.toString());

        Map<String, Object> map = Maps.newHashMap();
        if (ex.getCause() != null) {
            map.put("cause", ex.getCause().getMessage());
        } else {
            map.put("cause", ex.getMessage());
        }
        responseError.setData(map);
        return responseError;
    }

    /**
     * Build a RESTfulResponse from validation errors
     * @param ex
     * @return Map<String, Object>
     */
    private Map<String, Object> convertConstraintViolation(MethodArgumentNotValidException ex) {
        Map<String, Object> result = Maps.newHashMap();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            result.put(error.getField(), error.getDefaultMessage());
        }
        return result;
    }
}