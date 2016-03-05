package com.initfusion.volleydmp;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by dharmesh.prajapati on 12/21/2015.
 */
public class LoginResponse extends Response {

    @JsonProperty("Data")
    private ArrayList<Login> data;

    public ArrayList<Login> getData() {
        return data;
    }

    public void setData(ArrayList<Login> data) {
        this.data = data;
    }
}
