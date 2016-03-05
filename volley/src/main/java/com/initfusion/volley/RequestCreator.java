package com.initfusion.volley;

import android.app.ProgressDialog;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by dharmesh.prajapati on 3/4/2016.
 */

public class RequestCreator<ResponseType> {

    private JsonRequest.ResponseListener callback;
    private Class responseClass;
    private RequestQueue queue;

    private String url;

    public RequestCreator(String url,RequestQueue queue) {
        this.url = url;
        this.queue = queue;
    }

    public RequestCreator setCallback(JsonRequest.ResponseListener callback) {
        this.callback = callback;
        return this;
    }

    public RequestCreator setResponseClass(Class responseClass) {
        this.responseClass = responseClass;
        return this;
    }

    public void execute()
    {
        if(responseClass==null)
            responseClass = Object.class;

        JacksonRequest<ResponseType> jsonObjReq = new JacksonRequest<ResponseType>(
                Request.Method.GET, url, null, new JSONObject(), responseClass,
                createMyReqSuccessListener(), createMyReqErrorListener()) {
        };

        queue.add(jsonObjReq);
    }


    private Response.Listener<ResponseType> createMyReqSuccessListener() {
        return new Response.Listener<ResponseType>() {
            @Override
            public void onResponse(ResponseType response) {

                if(callback!=null)
                    callback.onResult(response, "Success");
            }
        };
    }

    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if(callback!=null)
                    callback.onResult(null, "Network Problem");
            }
        };
    }
}
