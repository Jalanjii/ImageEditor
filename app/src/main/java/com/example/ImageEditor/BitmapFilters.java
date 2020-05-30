package com.example.ImageEditor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;

public class BitmapFilters {
    public static final int MAX_RGB_VALUE = 255;
    private static int red = 255;
    private static int green = 255;
    private static int blue = 255;

    /**
     * This method takes bitmap and returns black n white version of it
     *
     * @param original Bitmap that you will turn into black n white
     * @return bitmap
     */
    public static Bitmap blackNWhite(Bitmap original) {
        Bitmap blackWhite = original.copy(original.getConfig(), true);

        Canvas canvas = new Canvas(blackWhite);
        ColorMatrix ma = new ColorMatrix();
        ma.setSaturation(0);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(ma));
        canvas.drawBitmap(blackWhite, 0, 0, paint);

        return blackWhite;
    }

    /**
     * This method takes bitmap and returns new bitmap that changed rgb value
     *
     * @param sourceBitmap Bitmap that you will turn into new rgb changed one
     * @return bitmap
     */
    public static Bitmap changeBitmapColor(Bitmap sourceBitmap) {
        int color = Color.rgb(red, green, blue);
        Bitmap resultBitmap = sourceBitmap.copy(sourceBitmap.getConfig(), true);
        Paint paint = new Paint();
        ColorFilter filter = new LightingColorFilter(color, 1);
        paint.setColorFilter(filter);
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, paint);
        return resultBitmap;
    }

    /**
     * This method takes bitmap and returns changed transparency
     *
     * @param original Bitmap that you will turn into new transparency changed one
     * @return bitmap
     */
    public static Bitmap transparency(Bitmap original) {
        Bitmap inversion = original.copy(original.getConfig(), true);

        // Get info about Bitmap
        int width = inversion.getWidth();
        int height = inversion.getHeight();
        int pixels = width * height;

        // Get original pixels
        int[] pixel = new int[pixels];
        inversion.getPixels(pixel, 0, width, 0, 0, width, height);

        // Modify pixels
        for (int i = 0; i < pixels; i++)
            pixel[i] ^= 0x80000000; //Each two bytes: Alpha, Red, Green, Blue
        inversion.setPixels(pixel, 0, width, 0, 0, width, height);

        // Return inverted Bitmap
        return inversion;

    }

    /**
     * This method takes bitmap and gives negative color bitmap
     *
     * @param original Original bitmap
     * @return Negative color bitmap
     */
    public static Bitmap invert(Bitmap original) {
        // Create mutable Bitmap to invert, argument true makes it mutable
        Bitmap inversion = original.copy(original.getConfig(), true);

        // Get info about Bitmap
        int width = inversion.getWidth();
        int height = inversion.getHeight();
        int pixels = width * height;

        // Get original pixels
        int[] pixel = new int[pixels];
        inversion.getPixels(pixel, 0, width, 0, 0, width, height);

        // Modify pixels
        for (int i = 0; i < pixels; i++)
            pixel[i] ^= 0x00FFFFFF; //Each two bytes: Alpha, Red, Green, Blue
        inversion.setPixels(pixel, 0, width, 0, 0, width, height);

        // Return inverted Bitmap
        return inversion;
    }

    public static void changeRed(int value) {
        if (checkColorValuesValid(value)) {
            red = value;
        }
    }

    public static void changeGreen(int value) {
        if (checkColorValuesValid(value)) {
            green = value;
        }
    }

    public static void changeBlue(int value) {
        if (checkColorValuesValid(value)) {
            blue = value;
        }
    }

    private static Boolean checkColorValuesValid(int value) {
        if (value <= 255 && value >= 0) {
            return true;
        }
        return false;
    }
}
