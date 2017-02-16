package com.duongtd.scannerdocument.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.FileNotFoundException;
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

}
