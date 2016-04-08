package com.initfusion.volley;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.lang.reflect.Type;


/**
 * Created by dharmesh.prajapati on 3/4/2016.
 */

public class RequestCreator<ResponseType> {

    private JsonRequest.ResponseListener callback;
    private Type responseClass;
    private RequestQueue queue;
    private JSONObject rowJsonBody;
    private String url;
    private int method = -2;
    private boolean isJackson = false;
    private Context mContext;
    private Dialog mDialog;
    private String loadingMassage;

    public RequestCreator(Context context, String url, RequestQueue queue) {
        mContext = context;
        this.url = url;
        this.queue = queue;
    }

    public RequestCreator setCallback(JsonRequest.ResponseListener callback) {
        this.callback = callback;
        return this;
    }

    public RequestCreator setResponseClass(Type responseClass) {
        this.responseClass = responseClass;
        return this;
    }

    public RequestCreator setRowJsonBody(JSONObject rowJsonBody) {
        this.rowJsonBody = rowJsonBody;
        return this;
    }

    public RequestCreator setMethod(int method) {
        this.method = method;
        return this;
    }

    public RequestCreator setJackson() {
        isJackson = true;
        return this;
    }

    public RequestCreator setDialog() {

        if (mDialog == null) {

            loadingMassage = "Loading..";

            ProgressDialog dialog = new ProgressDialog(mContext);
            dialog.setMessage(loadingMassage);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);

            mDialog = dialog;
        }

        return this;
    }

    public RequestCreator setDialog(String dialogTitle) {

        if (mDialog == null) {

            loadingMassage = dialogTitle;

            ProgressDialog dialog = new ProgressDialog(mContext);
            dialog.setMessage(loadingMassage);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);

            mDialog = dialog;
        }

        return this;
    }

    public RequestCreator setDialog(Dialog dialog) {

        if (mDialog == null) {
            mDialog = dialog;
        }

        return this;
    }

    public Request execute() {
        if (responseClass == null)
            setResponseClass(Object.class);

        if (rowJsonBody == null)
            rowJsonBody = new JSONObject();

        if (method == -2)
            method = Request.Method.POST;

        Log.e("Request", url);

        if(mDialog!=null)
            mDialog.show();

        if (isJackson) {
            JacksonRequest<ResponseType> jsonObjReq = new JacksonRequest<ResponseType>(method, url, null, rowJsonBody, (Class) responseClass,
                    createMyReqSuccessListener(), createMyReqErrorListener()) {
            };
            queue.add(jsonObjReq);

            return jsonObjReq;
        } else {
            GsonRequest<ResponseType> jsonObjReq = new GsonRequest<ResponseType>(method, url, null, rowJsonBody, responseClass,
                    createMyReqSuccessListener(), createMyReqErrorListener()) {
            };
            queue.add(jsonObjReq);
            return jsonObjReq;
        }
    }

    private Response.Listener<ResponseType> createMyReqSuccessListener() {
        return new Response.Listener<ResponseType>() {
            @Override
            public void onResponse(ResponseType response) {

                ActivityHelper.dismissDialog(mDialog);

                if (callback != null)
                    callback.onSuccess(response);
            }
        };
    }

    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                ActivityHelper.dismissDialog(mDialog);

                if (callback != null)
                    callback.onError(error);
            }
        };
    }
}
