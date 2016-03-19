package com.initfusion.volley;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


public class GsonRequest<ResponseType> extends JsonRequest<ResponseType> {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Type responseClass;

    public GsonRequest(String url, JSONObject parameterObject,
                       Class<ResponseType> responseClass,
                       Response.Listener<ResponseType> listener,
                       Response.ErrorListener errorListener) {

        this(Method.POST, url, null, parameterObject, responseClass, listener,
                errorListener);
    }

    public GsonRequest(int method, String url, Map<String, String> headers,
                       JSONObject parameterObject, Type responseClass,
                       Response.Listener<ResponseType> listener,
                       Response.ErrorListener errorListener) {

        super(method, url, parameterObject.toString(), listener, errorListener);

        this.responseClass = responseClass;
    }

    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        return headers;
    }

    @Override
    protected Response<ResponseType> parseNetworkResponse(
            NetworkResponse response) {


        try {

            String json = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));

            Log.e("Response", json);

            ResponseType result;
            if (JSONObject.class == responseClass) {
                result = (ResponseType) new JSONObject(json);
            }
            else {
                result = new Gson().fromJson(json, responseClass);
            }
            return Response.success(result,
                    HttpHeaderParser.parseCacheHeaders(response));

        } catch (JsonParseException e) {

            Log.i("Gson Error", e.getMessage());

            return Response.error(new ParseError(e));
        } catch (IOException e) {

            Log.i("Gson Error", e.getMessage());
            return Response.error(new ParseError(e));
        } catch (JSONException e) {

            Log.i("Json Error", e.getMessage());
            return Response.error(new ParseError(e));
        }

    }
}