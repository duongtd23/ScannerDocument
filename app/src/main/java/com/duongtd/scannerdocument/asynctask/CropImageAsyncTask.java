package com.duongtd.scannerdocument.asynctask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.AsyncTask;

import com.duongtd.scannerdocument.MyNDK;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by duongtd on 16/02/2017.
 */

public class CropImageAsyncTask extends AsyncTask<Void, Bitmap, Bitmap> {

    private Map<Integer, PointF> points;
    private int sWidth, sHeight;
    private Bitmap bitmap;
    private Activity activity;
    private ProgressDialog progressDialog;
    private static final String PROGRESS_MESSAGE = "Cropping";
    public AsyncResponse delegate = null;

    public CropImageAsyncTask(Activity activity, Bitmap bitmap, Map<Integer, PointF> points,
                              int sWidth, int sHeight, AsyncResponse delegate) {
        this.activity = activity;
        this.bitmap = bitmap;
        this.points = points;
        this.sWidth = sWidth;
        this.sHeight = sHeight;
        this.delegate = delegate;

        progressDialog = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        System.gc();

        progressDialog.setMessage(PROGRESS_MESSAGE);
        progressDialog.show();
    }

    @Override
    protected void onProgressUpdate(Bitmap... values) {
        super.onProgressUpdate(values);
        delegate.processFinish(values[0]);
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        Map<Integer, PointF> aPoints = new HashMap<>();
        PointF pointF;
//            for (float i = 0, j = sWidth, k = 0, l = sWidth,
//                 a = 0, b = 0, c = sHeight, d = sHeight;
//                 i < points.get(0).x || j > points.get(1).x || k < points.get(2).x || l > points.get(3).x ||
//                         a < points.get(0).y || b < points.get(1).y || c > points.get(2).y || d > points.get(3).y;
//                 i += points.get(0).x / STEPS, j -= (sWidth - points.get(1).x) / STEPS, k += points.get(2).x / STEPS, l -= (sWidth - points.get(3).x) / STEPS,
//                         a += points.get(0).y / STEPS, b += points.get(1).y / STEPS, c -= (sHeight - points.get(2).y) / STEPS, d -= (sHeight - points.get(3).y) / STEPS) {
//
//                pointF = new PointF();
//                pointF.set(i, a);
//                aPoints.put(0, pointF);
//
//                pointF = new PointF();
//                pointF.set(j, b);
//                aPoints.put(1, pointF);
//
//                pointF = new PointF();
//                pointF.set(k, c);
//                aPoints.put(2, pointF);
//
//                pointF = new PointF();
//                pointF.set(l, d);
//                aPoints.put(3, pointF);
//                publishProgress(getCropBitmap(bitmap, aPoints, sWidth, sHeight));
//            }
        bitmap = getCropBitmap(bitmap, points, sWidth, sHeight);
        // Matrix m = new Matrix();
        //m.setRectToRect(new RectF(0, 0, mBitmap.getWidth(), mBitmap.getHeight()),
        //       new RectF(0, 0, mBitmap.getWidth() * 1.5f, mBitmap.getHeight() * 1.5f), Matrix.ScaleToFit.FILL);
        // mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), m, true);
        //mUnProcessedBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), m, true);

//            bitmap = getScaledBitmap(sWidth, sHeight, bitmap);
        return bitmap;
    }


    private Bitmap getScaledBitmap(int maxWidth, int maxHeight, Bitmap image) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > 1) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        delegate.processFinish(bitmap);
    }

    private Bitmap getCropBitmap(Bitmap original, Map<Integer, PointF> points, int sWidth, int sHeight) {

        float xRatio = (float) original.getWidth() / sWidth;
        float yRatio = (float) original.getHeight() / sHeight;

        float x1 = (points.get(0).x) * xRatio;
        float x2 = (points.get(1).x) * xRatio;
        float x3 = (points.get(2).x) * xRatio;
        float x4 = (points.get(3).x) * xRatio;
        float y1 = (points.get(0).y) * yRatio;
        float y2 = (points.get(1).y) * yRatio;
        float y3 = (points.get(2).y) * yRatio;
        float y4 = (points.get(3).y) * yRatio;
        return MyNDK.cropBitmap(original, x1, y1, x2, y2, x3, y3, x4, y4);
    }
}