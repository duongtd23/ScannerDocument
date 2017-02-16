package com.duongtd.scannerdocument;

import android.graphics.Bitmap;

/**
 * Created by duongtd on 12/02/2017.
 */

public class MyNDK {
    static{
        System.loadLibrary("opencv_java3");
        System.loadLibrary("MyLibrary");
    }

    public static native String getNDKString();

    public static native Bitmap autoFilter(Bitmap bitmap);

    public static native Bitmap cropBitmap(Bitmap bitmap, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4);

}
