package com.duongtd.scannerdocument.asynctask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.duongtd.scannerdocument.MyNDK;

/**
 * Created by duongtd on 16/02/2017.
 */

public class ImageProcessingTask extends AsyncTask<Void, Void, Bitmap> {

    private ProgressDialog progressDialog;
    private Bitmap _bitmap;
    public AsyncResponse delegate = null;

    private static final String PROGRESS_MESSAGE = "Filter";

    public ImageProcessingTask(Bitmap _bitmap, Activity activity, AsyncResponse delegate){
        super();
        this._bitmap = _bitmap;
        this.delegate = delegate;
        progressDialog = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute(){
        progressDialog.setMessage(PROGRESS_MESSAGE);
        progressDialog.show();
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        Bitmap result = MyNDK.autoFilter(_bitmap);
        return result;
    }

    @Override
    protected void onPostExecute(Bitmap _bitmap){
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
//        setImage(_bitmap);
        delegate.processFinish(_bitmap);
    }
}