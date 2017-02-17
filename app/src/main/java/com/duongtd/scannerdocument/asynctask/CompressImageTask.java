package com.duongtd.scannerdocument.asynctask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by duongtd on 16/02/2017.
 */

public class CompressImageTask extends AsyncTask<OutputStream, Void, OutputStream> {

    private Bitmap bitmapImage;
    private ProgressDialog progressDialog;
    private Activity activity;
    private boolean showGUI = true;

    public boolean isShowGUI() {
        return showGUI;
    }

    public void setShowGUI(boolean showGUI) {
        this.showGUI = showGUI;
    }

    public AsyncResponse delegate = null;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message = "Compressing image. It may take some seconds with large image!";

    public CompressImageTask(Bitmap bitmap, Activity activity, AsyncResponse delegate){
        this.bitmapImage = bitmap;
        this.activity = activity;
        this.delegate = delegate;
        progressDialog = new ProgressDialog(activity);
    }

    public CompressImageTask(Bitmap bitmap, Activity activity) {
        this.bitmapImage = bitmap;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (showGUI) {
            progressDialog.setMessage(message);
            progressDialog.show();
        }
    }

    @Override
    protected OutputStream doInBackground(OutputStream... params) {
        OutputStream outputStream = params[0];
        //FileOutputStream fileOutputStream = null;
        try {
//            fileOutputStream = new FileOutputStream(createFile());
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return outputStream;
    }

    @Override
    protected void onPostExecute(OutputStream result) {
        super.onPostExecute(result);
        if (showGUI) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            delegate.processFinish(result);
        }
// Toast.makeText(activity, MESSAGE_SAVE_SUCCESS + " " + fileName, Toast.LENGTH_LONG).show();
    }
}