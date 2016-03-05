package com.initfusion.volleydmp;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by dharmesh.prajapati on 12/21/2015.
 */
public class Response {

    @JsonProperty("Code")
    private String code;
    @JsonProperty("Message")
    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
