package com.example.ImageEditor;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class DefaultMethods {
    public static final int GALLERY_PERMISSION = 10;
    public static final int GALLERY_REQUEST_CODE = 11;

    private DefaultMethods() {
    }

    public static Bitmap loadBitmapExample(Context context) {
        return BitmapLoader.decodeSampledBitmapFromResource(context.getResources(),
                R.drawable.add,
                1920,
                1080,
                true);
    }

    public static Bitmap loadBitmapFromUri(Context context, Uri uri) {
        return BitmapLoader.decodeSampledBitmapFromResource(context, uri,
                1920,
                1080,
                true);
    }

    public static Bitmap loadBitmapUntouchedFromUri(Context context, Uri uri) {
        return BitmapLoader.decodeSampledBitmapFromResource(context, uri);
    }

    public static Bitmap loadBitmapFromUri(Context context, Uri uri, boolean compress) {
        return BitmapLoader.decodeSampledBitmapFromResource(context, uri,
                1920,
                1080,
                compress);
    }

    public static Bitmap loadBitmapFromUri(Context context, Uri uri, int reqWidth, int reqHeight) {
        return BitmapLoader.decodeSampledBitmapFromResource(context, uri,
                reqWidth,
                reqHeight,
                true);
    }

    public static Bitmap loadBitmapFromUri(Context context, Uri uri, int reqWidth, int reqHeight, boolean compress) {
        return BitmapLoader.decodeSampledBitmapFromResource(context, uri,
                reqWidth,
                reqHeight,
                compress);
    }


    public static boolean checkGalleryPermission(Context context) {
        int galleryPermission = ContextCompat.checkSelfPermission(Objects.requireNonNull(context),
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (galleryPermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        return false;
    }

    public static boolean permissionBoxStillAvailable(Activity activity) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    public static void openSettingsForPermission(Activity activity) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivity(intent);
    }

    public static void requestPermission(Activity activity, int permissionStatic) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                permissionStatic);
    }

    public static void showSnackBarForPermission(Activity activity, int id) {
        Snackbar.make(activity.findViewById(id), "Please grant access or application will use demo picture", Snackbar.LENGTH_INDEFINITE)
                .setAction("Permission", v -> {
                    if (permissionBoxStillAvailable(activity)) {
                        requestPermission(activity, GALLERY_PERMISSION);
                    } else {
                        openSettingsForPermission(activity);
                    }
                })
                .show();
    }

    public static void pickFromGallery(Fragment fragment) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        fragment.startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST_CODE);
    }
}
