package com.duongtd.scannerdocument.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by duongtd on 08/02/2017.
 */

public class BitmapUtil {
    public static Bitmap getBitmapFromUri(Context mContext, Uri uri) throws FileNotFoundException {
        InputStream inputStream = null;
        inputStream = mContext.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        Utils.safeClose(inputStream);
        return bitmap;
    }

    /**
     * Get Image Uri when clicked from Camera
     *
     * @return Uri of clicked Image
     */
    public static Uri getBitmapUri() {
        File imageStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "myCache");
        if (!imageStorageDir.exists()) {
            imageStorageDir.mkdirs();
        }
        File file = null;
        try {
            file = File.createTempFile(
                    "IMG_" + String.valueOf(System.currentTimeMillis()),
                    ".jpg",
                    imageStorageDir
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Uri.fromFile(file);
    }

}
