package com.wyn.payment.controller;

import com.wyn.payment.serviceImpl.TransactionServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.nio.file.AccessDeniedException;

public class BaseController {


    private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<String> genericExeptionHandler(Throwable t) {
        LOGGER.error("Generic controller error", t);
        return new ResponseEntity<>("An unexpected error occurred. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> accessDeniedExceptionHandler() {
        return new ResponseEntity<>("Access denied. You do not have permission to access this resource.", HttpStatus.FORBIDDEN);
    }

    @RequestMapping("/error")
    public ResponseEntity<String> webExceptionHandler() {
        return new ResponseEntity<>("Resource not found.", HttpStatus.NOT_FOUND);
    }
}
