package com.initfusion.volley;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class JsonRequest<ResponseType> {

    private RequestQueue queue;
    private ResponseListener callback;
    ProgressDialog dialog;
    private Context context;
    SharedPreferences pref;
    int socketTimeout = 60000;
    private Class<ResponseType> responseClass;

    @Deprecated
    public JsonRequest(Context context, RequestQueue queue, Class<ResponseType> responseClass) {
        super();
        this.queue = queue;
        dialog = new ProgressDialog(context);
        this.context = context;
        this.responseClass = responseClass;
    }

    public JsonRequest(Context context, RequestQueue queue) {
        super();
        this.queue = queue;
        dialog = new ProgressDialog(context);
        this.context = context;
    }

    public void loadData(String url, String loadingMassage, ResponseListener callback) {
        this.callback = callback;

        Log.e("URL", url);

        JacksonRequest<ResponseType> jsonObjReq = new JacksonRequest<ResponseType>(
                Method.GET, url, null, new JSONObject(), responseClass,
                createMyReqSuccessListener(), createMyReqErrorListener()) {
        };

        if (dialog != null) {
            dialog.show();
            dialog.setMessage(loadingMassage);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
        }

        // RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeout,
        // DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
        // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        // jsonObjReq.setRetryPolicy(retryPolicy);

        queue.add(jsonObjReq);
    }

    public void loadData(int method, JSONObject data, String url,
                         ResponseListener callback) {
        this.callback = callback;

        Log.e("URL", url);
        Log.e("Request", data.toString());

        JacksonRequest<ResponseType> jsonObjReq = new JacksonRequest<ResponseType>(
                method, url, null, data, responseClass,
                createMyReqSuccessListener(), createMyReqErrorListener()) {
        };

        if (dialog != null) {
            dialog.show();
            dialog.setMessage("Loading....");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
        }

        // RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeout,
        // DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
        // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        // jsonObjReq.setRetryPolicy(retryPolicy);

        queue.add(jsonObjReq);
    }

    public void loadData(boolean isdialog, String url, ResponseListener callback) {
        this.callback = callback;
        Log.e("URL", url);

        JacksonRequest<ResponseType> jsonObjReq = new JacksonRequest<ResponseType>(
                Method.GET, url, null, new JSONObject(), responseClass,
                createMyReqSuccessListener(), createMyReqErrorListener()) {
        };

        if (dialog != null) {
            if (isdialog) {
                dialog.show();
                dialog.setMessage("Loading....");
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
            }
        }


        queue.add(jsonObjReq);
    }

    public void loadData(int method, JSONObject data, String url, Class responseClass,
                         ResponseListener callback) {
        this.callback = callback;

        Log.e("URL", url);
        Log.e("Request", data.toString());

        JacksonRequest<ResponseType> jsonObjReq = new JacksonRequest<ResponseType>(
                method, url, null, data, responseClass,
                createMyReqSuccessListener(), createMyReqErrorListener()) {
        };

        if (dialog != null) {
            dialog.show();
            dialog.setMessage("Loading....");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
        }

        // RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeout,
        // DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
        // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        // jsonObjReq.setRetryPolicy(retryPolicy);

        queue.add(jsonObjReq);
    }

    private Response.Listener<ResponseType> createMyReqSuccessListener() {
        return new Response.Listener<ResponseType>() {
            @Override
            public void onResponse(ResponseType response) {

                ActivityHelper.dismissDialog(dialog);
                if (callback != null)
                    callback.onSuccess(response);
            }
        };
    }

    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                ActivityHelper.dismissDialog(dialog);

                if (callback != null)
                    callback.onError(error);
            }
        };
    }

    public interface ResponseListener<T> {
        void onSuccess(T result);
        void onError(VolleyError error);
    }

    public static class Builder {

        private final Context context;
        private RequestQueue queue;
        private String dialogTitle;

        public Builder(Context context) {
            if (context == null) {
                throw new IllegalArgumentException("Context must not be null.");
            }
            this.context = context;
        }

        public JsonRequest build() {
            Context context = this.context;

            if (queue == null)
                queue = Volley.newRequestQueue(context);

            if (dialogTitle == null)
                dialogTitle = "loading..";

            return new JsonRequest<>(context, queue);
        }
    }

    static JsonRequest singleton = null;

    public static JsonRequest with(Context context) {
        if (singleton == null) {
            synchronized (JsonRequest.class) {
                if (singleton == null) {
                    singleton = new Builder(context).build();
                }
            }
        }
        return singleton;
    }

    public RequestCreator<ResponseType> load(String path) {

        if (path.trim().length() == 0) {
            throw new IllegalArgumentException("Path must not be empty.");
        }

        return new RequestCreator<>(path, queue);
    }
}