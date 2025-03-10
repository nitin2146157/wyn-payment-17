package com.wyn.payment.bean;

import java.util.Map;

import com.google.common.collect.Maps;


public class RESTfulResponse {

    private String status;
    private String responseType;
    private String message;
    private Map<String, Object> data = Maps.newHashMap();

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getResponseType() {
        return responseType;
    }
    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public Map<String, Object> getData() {
        return data;
    }
    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
