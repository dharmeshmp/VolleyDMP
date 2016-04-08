package com.initfusion.volley;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.util.Map;
import java.util.Set;


public class UploadAsync extends AsyncTask<Void, Integer, String> {
    Map<String, Object> map;
    Context context;
    String url;
    boolean isProgressDialog;

    public UploadAsync(Context context, Map<String, Object> map, String url) {
        this.map = map;
        this.context = context;
        this.url = url;
    }

    public UploadAsync(Context context, Map<String, Object> map, String url, ProgressListener progressListener, ResponseListener responseListener) {
        this.progressListener = progressListener;
        this.responseListener = responseListener;
        this.map = map;
        this.context = context;
        this.url = url;
    }

    public UploadAsync(Context context, Map<String, Object> map, String url, ProgressListener progressListener, ResponseListener responseListener, boolean isProgressDialog) {
        this.progressListener = progressListener;
        this.responseListener = responseListener;
        this.map = map;
        this.context = context;
        this.url = url;
        this.isProgressDialog = isProgressDialog;
    }

    ProgressDialog dialog;
    long totalSize;

    @Override
    protected void onPreExecute() {

        if (isProgressDialog) {
            dialog = new ProgressDialog(context);
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.setIndeterminate(true);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.show();
        }
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(final String result) {
        super.onPostExecute(result);

        ActivityHelper.dismissDialog(dialog);

        Message msg = new Message();
        msg.what = 0;

        msg.obj = result;

        if (responseListener != null) {
            responseListener.onResponse(result);
        }

    }

    @Override
    protected String doInBackground(Void... params) {

        HttpClient httpClient = new DefaultHttpClient();
        String ret = "error";

        try {

            HttpPost httPost = new HttpPost(url);

            Log.e("URL", url);

            //httPost.setHeader("Content-Type","multipart/form-data; boundary= --*****--");

            CustomMultiPartEntity entity = new CustomMultiPartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, new CustomMultiPartEntity.ProgressListener() {

                @Override
                public void transferred(long num) {
                    publishProgress((int) ((num / (float) totalSize) * 100));

                }
            });

            Set<String> set = map.keySet();
            for (String key : set) {
                Object object = map.get(key);
                if (object instanceof File) {
                    //ContentBody body = new FileBody((File) object,"photo.jpg","image/jpeg","");   //image/jpeg             video/mp4

                    ContentBody body = new FileBody((File) object);
                    FormBodyPart fbp = new FormBodyPart(key, body);

                    Log.e(key, object.toString());

                    entity.addPart(fbp);
                } else if (object instanceof String) {
                    Log.e(key, (String) object);
                    entity.addPart(key, new StringBody((String) object, ContentType.TEXT_PLAIN));
                }
            }

            totalSize = entity.getContentLength();

            httPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httPost);

            ret = EntityUtils.toString(response.getEntity());

            Log.e("Upload Response", ret);

        } catch (Exception exception) {
            exception.printStackTrace();
            Log.e("Upload Error ", exception.getMessage());
        }
        return ret;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        if (dialog != null)
            dialog.setProgress((values[0]));

        if (progressListener != null)
            progressListener.onProgress((values[0]));
    }

    private ProgressListener progressListener;
    private ResponseListener responseListener;

    public interface ProgressListener {
        void onProgress(int progress);
    }

    public interface ResponseListener {
        void onResponse(String result);
    }
}