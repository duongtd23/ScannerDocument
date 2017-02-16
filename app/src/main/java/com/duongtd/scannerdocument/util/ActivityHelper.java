package com.duongtd.scannerdocument.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.MainThread;

import com.duongtd.scannerdocument.activities.MainActivity;


/**
 * Activity Helper class to perform common operations
 */
public final class ActivityHelper {

    private static final CharSequence TAKE_PHOTO = "Take Photo";
    private static final CharSequence CHOOSE_PHOTO = "Choose Photo";
    private static final CharSequence CARD_READER = "Card Reader";
    private static final CharSequence CANCEL = "Cancel";
    private static final String PHOTO_MESSAGE = "Select Photo";

    private ActivityHelper() {
    }

    /**
     * Open Image Selection dialog
     *
     * @param activity          - Activity reference
     * @param activityCamera    - Request Code Camera
     * @param activitySelection - Request Code File
     */
    public static void selectMenu(Activity activity, int activityCamera, int activitySelection, Uri uri) {
        final CharSequence[] items = {TAKE_PHOTO, CHOOSE_PHOTO};
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(PHOTO_MESSAGE);
        builder.setCancelable(true);
        builder.setItems(items, (dialog, item) -> {

            if (items[item].equals(TAKE_PHOTO)) {
                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                activity.startActivityForResult(captureIntent,
                        activityCamera);
            } else if (items[item].equals(CHOOSE_PHOTO)) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                activity.startActivityForResult(Intent.createChooser(i, CHOOSE_PHOTO), activitySelection);
            } else if (items[item].equals(CANCEL)) {
                activity.finish();
            }
        });
        builder.show();
    }
}