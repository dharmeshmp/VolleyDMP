package com.initfusion.volley;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadAsync extends AsyncTask<Void, Integer, String> {

    private String fileUrl;
    private String fileDirectory;
    private String fileName;
    private DownloadProgressListener downloadProgressListener;
    private ResponseListener responseListener;

    public DownloadAsync(String fileUrl, String fileDirectory, String fileName, DownloadProgressListener downloadProgressListener, ResponseListener responseListener) {
        this.fileUrl = fileUrl;
        this.fileDirectory = fileDirectory;
        this.fileName = fileName;
        this.downloadProgressListener = downloadProgressListener;
        this.responseListener = responseListener;

        Log.e(getClass().getSimpleName(),fileUrl);
        Log.e(getClass().getSimpleName(),fileDirectory);
        Log.e(getClass().getSimpleName(),fileName);
    }

    @Override
    protected String doInBackground(Void... f_url) {
        int count;
        String resp = "Error";

        Log.e(getClass().getSimpleName(),"doInBackground");

        try {
            URL url = new URL(fileUrl);
            URLConnection conection = url.openConnection();
            conection.connect();
            // getting file length
            int lenghtOfFile = conection.getContentLength();

            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            // Output stream to write file
            OutputStream output = new FileOutputStream(fileDirectory + fileName);

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress((int) ((total * 100) / lenghtOfFile));

                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();

            resp = "success";

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
            resp = "Error";
        }

        return resp;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (downloadProgressListener != null)
            downloadProgressListener.onProgress(values[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        Log.e(getClass().getSimpleName(), "onPostExcecute");

        if (s.equals("success")) {
            String path = fileDirectory + fileName;
            responseListener.onResponse(path);
        } else {
            Log.e("Network Error", "net not found");
            responseListener.onResponse(s);
        }
    }

    public interface DownloadProgressListener {
        void onProgress(int progress);
    }

    public interface ResponseListener {
        void onResponse(String progress);
    }
}
