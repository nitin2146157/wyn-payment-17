package com.wyn.payment.controller;

import com.wyn.payment.bean.RESTfulResponse;
import com.wyn.payment.entity.ClientDetail;
import com.wyn.payment.service.ClientDetailService;
import com.wyn.payment.service.TransactionService;
import com.wyn.payment.serviceImpl.TransactionServiceImpl;
import com.wyn.payment.util.Gentools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class TransactionWSController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionWSController.class);

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ClientDetailService clientDetailService;

    @Value("${CREATE_CC_URL}")
    private String createCardUrl;

    @GetMapping(value = "/createtransaction/{clientId}/{userName}/{referenceNumber}", headers = "Accept=application/json")
    public ResponseEntity<RESTfulResponse> getTransactionHashKey(@PathVariable String clientId, @PathVariable String userName, @PathVariable String referenceNumber) {

        LOGGER.info("createtransaction call with params : {}/{}/{}", clientId, userName, referenceNumber);

        RESTfulResponse rESTfulResponse = new RESTfulResponse();

        if (Gentools.isEmptyString(clientId) || Gentools.isEmptyString(userName) || Gentools.isEmptyString(referenceNumber)) {
            return buildErrorResponse(rESTfulResponse, "Parameters are incorrect. Must be /createtransaction/{clientId}/{userName}/{referenceNumber}", HttpStatus.FORBIDDEN);
        }

        Integer iClientId;
        try {
            iClientId = Integer.parseInt(clientId);
        } catch (NumberFormatException e) {
            return buildErrorResponse(rESTfulResponse, "Client Id is not valid. Must be a number", HttpStatus.FORBIDDEN);
        }

        Optional<ClientDetail> clientDetail = clientDetailService.findById(iClientId);

        if (!clientDetail.isPresent()) {
            return buildErrorResponse(rESTfulResponse, "Client Id is not valid. Must be an existing client", HttpStatus.FORBIDDEN);
        }

        String hashKeyMd5 = transactionService.authenticateClient(clientDetail.get(), userName, referenceNumber);

        if (!Gentools.isEmptyString(hashKeyMd5)) {
            Map<String, Object> data = new HashMap<>();
            data.put("returnURL", createCardUrl + hashKeyMd5);

            rESTfulResponse.setStatus("SUCCESS");
            rESTfulResponse.setResponseType(HttpStatus.OK.toString());
            rESTfulResponse.setData(data);
            LOGGER.info("createtransaction call response with success: {}", data);
            return new ResponseEntity<>(rESTfulResponse, HttpStatus.OK);
        } else {
            return buildErrorResponse(rESTfulResponse, "Could not generate Hashkey. Please try again later", HttpStatus.FORBIDDEN);
        }
    }

    private ResponseEntity<RESTfulResponse> buildErrorResponse(RESTfulResponse response, String message, HttpStatus status) {
        response.setStatus("FAILURE");
        response.setResponseType(status.toString());
        response.setMessage(message);
        LOGGER.info("createtransaction call response with error: {}", message);
        return new ResponseEntity<>(response, status);
    }
}

