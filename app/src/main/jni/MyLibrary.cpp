//
// Created by duongtd on 12/02/2017.
//

#include "com_duongtd_scannerdocument_MyNDK.h"
#include <opencv2/imgcodecs.hpp>
#include <opencv/cv.hpp>
#include <string>
#include <vector>
#include <android/bitmap.h>
#include <android/log.h>

#define APPNAME "MyLibrary"


using namespace cv;
using namespace std;

Mat structuringElement1x1 = cv::getStructuringElement(cv::MORPH_RECT, cv::Size(1, 1));
Mat structuringElement2x2 = cv::getStructuringElement(cv::MORPH_RECT, cv::Size(2, 2));
Mat structuringElement3x3 = cv::getStructuringElement(cv::MORPH_RECT, cv::Size(3, 3));

Mat processing(Mat doc);
jobject mat_to_bitmap(JNIEnv * env, Mat & src, bool needPremultiplyAlpha, jobject bitmap_config);

JNIEXPORT jstring JNICALL Java_com_duongtd_scannerdocument_MyNDK_getNDKString
        (JNIEnv *env, jclass){
    return (*env).NewStringUTF("My Jni Text");
}

Mat processing(Mat doc){
    cvtColor(doc, doc, COLOR_RGBA2GRAY);
    adaptiveThreshold(doc, doc, 255, ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY, 55, 35);
    dilate(doc, doc, structuringElement2x2);
    erode(doc, doc, structuringElement3x3);
    return doc;
}

jobject mat_to_bitmap(JNIEnv * env, Mat & src, bool needPremultiplyAlpha, jobject bitmap_config){
    jclass java_bitmap_class = (jclass)env->FindClass("android/graphics/Bitmap");
    jmethodID mid = env->GetStaticMethodID(java_bitmap_class,
                                           "createBitmap", "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");

    jobject bitmap = env->CallStaticObjectMethod(java_bitmap_class,
                                                 mid, src.size().width, src.size().height, bitmap_config);
    AndroidBitmapInfo  info;
    void*              pixels = 0;

    try {
        CV_Assert(AndroidBitmap_getInfo(env, bitmap, &info) >= 0);
        CV_Assert(src.type() == CV_8UC1 || src.type() == CV_8UC3 || src.type() == CV_8UC4);
        CV_Assert(AndroidBitmap_lockPixels(env, bitmap, &pixels) >= 0);
        CV_Assert(pixels);
        if(info.format == ANDROID_BITMAP_FORMAT_RGBA_8888){
            Mat tmp(info.height, info.width, CV_8UC4, pixels);
            if(src.type() == CV_8UC1){
                cvtColor(src, tmp, CV_GRAY2RGBA);
            }else if(src.type() == CV_8UC3){
                cvtColor(src, tmp, CV_RGB2RGBA);
            }else if(src.type() == CV_8UC4){
                if(needPremultiplyAlpha){
                    cvtColor(src, tmp, COLOR_RGBA2mRGBA);
                }else{
                    src.copyTo(tmp);
                }
            }
            tmp.release();
        }else{
            // info.format == ANDROID_BITMAP_FORMAT_RGB_565
            Mat tmp(info.height, info.width, CV_8UC2, pixels);
            if(src.type() == CV_8UC1){

                cvtColor(src, tmp, CV_GRAY2BGR565);
            }else if(src.type() == CV_8UC3){
                cvtColor(src, tmp, CV_RGB2BGR565);
            }else if(src.type() == CV_8UC4){
                cvtColor(src, tmp, CV_RGBA2BGR565);
            }
            tmp.release();
        }
        AndroidBitmap_unlockPixels(env, bitmap);
        src.release();

        return bitmap;
    }catch(cv::Exception e){
        AndroidBitmap_unlockPixels(env, bitmap);
        jclass je = env->FindClass("org/opencv/core/CvException");
        if(!je) je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, e.what());
        return bitmap;
    }catch (...){
        AndroidBitmap_unlockPixels(env, bitmap);
        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, "Unknown exception in JNI code {nMatToBitmap}");
        return bitmap;
    }
}


/*
 * Class:     com_duongtd_scannerdocument_MyNDK
 * Method:    autoFilter
 * Signature: (Landroid/graphics/Bitmap;)Landroid/graphics/Bitmap;
 */
JNIEXPORT jobject JNICALL Java_com_duongtd_scannerdocument_MyNDK_autoFilter
        (JNIEnv *env, jclass ob, jobject bitmap){
    int ret;
    void* pixels = 0;
    AndroidBitmapInfo info;

    if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
        __android_log_print(ANDROID_LOG_VERBOSE, APPNAME,"AndroidBitmap_getInfo() failed ! error=%d", ret);
        return NULL;
    }

    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
        __android_log_print(ANDROID_LOG_VERBOSE, APPNAME,"AndroidBitmap_lockPixels() failed ! error=%d", ret);
    }

    Mat mbgra(info.height, info.width, CV_8UC4, pixels);
    mbgra = processing(mbgra);


    //get source bitmap's config
    jclass java_bitmap_class = (jclass)env->FindClass("android/graphics/Bitmap");
    jmethodID mid = env->GetMethodID(java_bitmap_class, "getConfig", "()Landroid/graphics/Bitmap$Config;");
    jobject bitmap_config = env->CallObjectMethod(bitmap, mid);
    jobject _bitmap = mat_to_bitmap(env,mbgra,false,bitmap_config);

    AndroidBitmap_unlockPixels(env, bitmap);
    mbgra.release();
    return _bitmap;
    //
}

Point2f computePoint(int p1, int p2) {
    Point2f pt;
    pt.x = p1;
    pt.y = p2;
    return pt;
}

Mat crop(Mat img, jfloat x1, jfloat y1, jfloat x2, jfloat y2, jfloat x3, jfloat y3, jfloat x4, jfloat y4) {

    __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "crop() %f",x1);
    // define the destination image size:

    float w1 = sqrt( pow(x4 - x3 , 2) + pow(y4 - y3, 2));
    float w2 = sqrt( pow(x2 - x1 , 2) + pow(y2 - y1, 2));
    float h1 = sqrt( pow(x2 - x4 , 2) + pow(y2 - y4, 2));
    float h2 = sqrt( pow(x1 - x3 , 2) + pow(y1 - y3, 2));

    float maxWidth = (w1 < w2) ? w1 : w2;
    float maxHeight = (h1 < h2) ? h1 : h2;

    Mat dst = Mat::zeros(maxHeight, maxWidth, CV_8UC3);

    // corners of destination image with the sequence [tl, tr, bl, br]
    vector<Point2f> dst_pts, img_pts;
    dst_pts.push_back(Point(0, 0));
    dst_pts.push_back(Point(maxWidth - 1, 0));
    dst_pts.push_back(Point(0, maxHeight - 1));
    dst_pts.push_back(Point(maxWidth - 1, maxHeight - 1));

    img_pts.push_back(computePoint(x1,y1));
    img_pts.push_back(computePoint(x2,y2));
    img_pts.push_back(computePoint(x3,y3));
    img_pts.push_back(computePoint(x4,y4));

    // get transformation matrix
    Mat transmtx = getPerspectiveTransform(img_pts, dst_pts);
    warpPerspective(img, dst, transmtx, dst.size());
    img.release();
//    dst.release();
    transmtx.release();

    return dst;
}



JNIEXPORT jobject JNICALL Java_com_duongtd_scannerdocument_MyNDK_cropBitmap
        (JNIEnv *env, jclass thiz, jobject bitmap,
         jfloat x1,jfloat y1,jfloat x2, jfloat y2, jfloat x3,jfloat y3,jfloat x4,jfloat y4) {
    __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "getString");
    int ret;
    AndroidBitmapInfo info;
    void* pixels = 0;

    if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
        __android_log_print(ANDROID_LOG_VERBOSE, APPNAME,"AndroidBitmap_getInfo() failed ! error=%d", ret);
        return NULL;
    }

    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888 )
    {       __android_log_print(ANDROID_LOG_VERBOSE, APPNAME,"Bitmap format is not RGBA_8888!");
        return NULL;
    }

    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
        __android_log_print(ANDROID_LOG_VERBOSE, APPNAME,"AndroidBitmap_lockPixels() failed ! error=%d", ret);
    }

    // init our output image
    Mat mbgra(info.height, info.width, CV_8UC4, pixels);
    mbgra = crop(mbgra,x1,y1,x2,y2,x3,y3,x4,y4);

    //get source bitmap's config
    jclass java_bitmap_class = (jclass)env->FindClass("android/graphics/Bitmap");
    jmethodID mid = env->GetMethodID(java_bitmap_class, "getConfig", "()Landroid/graphics/Bitmap$Config;");
    jobject bitmap_config = env->CallObjectMethod(bitmap, mid);
    jobject _bitmap = mat_to_bitmap(env,mbgra,false,bitmap_config);

    AndroidBitmap_unlockPixels(env, bitmap);
    mbgra.release();
    return _bitmap;
}
