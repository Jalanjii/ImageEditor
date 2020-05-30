package com.example.ImageEditor;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

public class BitmapLoader {

    /**
     * This method takes bitmap.options and calculates sample size
     * according to required width and height
     *
     * @param options   bitmap options
     * @param reqWidth  required width
     * @param reqHeight required height
     * @return calculated sample size
     */
    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * This method decode bitmap from resource with required with and height also compress option
     *
     * @param res       resources
     * @param resId     resource id
     * @param reqWidth  required width
     * @param reqHeight required height
     * @param compress  do u want compress?
     * @return of course bitmap from resources
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight, boolean compress) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap finalBitmap = BitmapFactory.decodeResource(res, resId, options);
        if (compress) {
            ByteArrayOutputStream compressedOutput = new ByteArrayOutputStream();
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 50, compressedOutput);
            finalBitmap = BitmapFactory.decodeByteArray(compressedOutput.toByteArray(), 0, compressedOutput.size());
        }
        return finalBitmap;
    }


    /**
     * This method decode bitmap from Uri path with required with and height also compress option
     *
     * @param context   context
     * @param path      Uri path
     * @param reqWidth  required with
     * @param reqHeight required height
     * @param compress  compress option
     * @return bitmap
     */
    public static Bitmap decodeSampledBitmapFromResource(Context context, Uri path,
                                                         int reqWidth, int reqHeight, boolean compress) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(path), null, options);


        } catch (FileNotFoundException e) {

        }

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap finalBitmap = null;
        try {
            finalBitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(path), null, options);


        } catch (FileNotFoundException e) {

        }
        if (compress) {
            ByteArrayOutputStream compressedOutput = new ByteArrayOutputStream();
            assert finalBitmap != null;
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 50, compressedOutput);
            finalBitmap = BitmapFactory.decodeByteArray(compressedOutput.toByteArray(), 0, compressedOutput.size());
        }
        return finalBitmap;
    }

    /**
     * This method decode untouched bitmap from path
     *
     * @param context context
     * @param path    Uri path
     * @return bitmap
     */
    public static Bitmap decodeSampledBitmapFromResource(Context context, Uri path) {
        try {
            return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(path));


        } catch (FileNotFoundException e) {
            return null;
        }
    }


}
