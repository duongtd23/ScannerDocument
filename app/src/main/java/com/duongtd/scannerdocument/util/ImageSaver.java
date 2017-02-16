package com.duongtd.scannerdocument.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.duongtd.scannerdocument.asynctask.AsyncResponse;
import com.duongtd.scannerdocument.asynctask.CompressImageTask;
import com.duongtd.scannerdocument.asynctask.CropImageAsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by duongtd on 13/02/2017.
 */

public class ImageSaver {

    private String directoryName = "images";
    private String fileName = "image.png";
    private Activity activity;
    private boolean external = true;
    private static final String MESSAGE_SAVE_SUCCESS = "Export success";


    public ImageSaver(Activity activity) {
        this.activity = activity;
    }

    public ImageSaver setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public ImageSaver setExternal(boolean external) {
        this.external = external;
        return this;
    }

    public ImageSaver setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
        return this;
    }

    public void save(Bitmap bitmapImage) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(createFile());
            CompressImageTask compressImageTask = new CompressImageTask(bitmapImage, activity, new AsyncResponse() {
                @Override
                public void processFinish(Object output) {
                    Toast.makeText(activity, MESSAGE_SAVE_SUCCESS + " " + fileName, Toast.LENGTH_LONG).show();
                }
            });
            compressImageTask.execute(fileOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @NonNull
    private File createFile() {
        File directory;
        if(external){
            directory = getAlbumStorageDir(directoryName);
        }
        else {
            directory = activity.getDir(directoryName, Context.MODE_PRIVATE);
        }

        return new File(directory, fileName);
    }

    public Bitmap load() {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(createFile());
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("ImageSaver", "Directory not created");
        }
        return file;
    }

//    private class CompressImageTask extends AsyncTask<Void, Void, Void>{
//
//        private Bitmap bitmapImage;
//        private ProgressDialog progressDialog;
//        public CompressImageTask(Bitmap bitmap){
//            this.bitmapImage = bitmap;
//            progressDialog = new ProgressDialog(activity);
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            progressDialog.setMessage(MESSAGE_COMPRESS_IMAGE);
//            progressDialog.show();
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            FileOutputStream fileOutputStream = null;
//            try {
//                fileOutputStream = new FileOutputStream(createFile());
//                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    if (fileOutputStream != null) {
//                        fileOutputStream.close();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            if (progressDialog.isShowing())
//                progressDialog.dismiss();
//            Toast.makeText(activity, MESSAGE_SAVE_SUCCESS + " " + fileName, Toast.LENGTH_LONG).show();
//        }
//    }
}