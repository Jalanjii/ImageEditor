package com.example.ImageEditor;

import android.graphics.Bitmap;
import android.os.AsyncTask;

public class BitmapUnsharpMask extends AsyncTask<Void, Void, Bitmap> {
    private ResultContract resultContract;
    private Bitmap bitmap;
    private float amount;
    private int threshold;

    public BitmapUnsharpMask(ResultContract resultContract, Bitmap bitmap, float amount, int threshold) {
        this.resultContract = resultContract;
        this.bitmap = bitmap;
        this.amount = amount;
        this.threshold = threshold;
    }

    /**
     * This method takes original image and return sharpened version with
     * unsharp masking method that implemented from java version to android
     * that's why it's working slow
     *
     * @param original  Original bitmap
     * @param amount    effect amount
     * @param threshold effect threshold for well for effect of course
     * @return Unsharped bitmap
     */
    public static Bitmap sharpen(Bitmap original, float amount, int threshold) {
        Bitmap sharpened = original.copy(original.getConfig(), true);

        int imgWidth = sharpened.getWidth();
        int imgHeight = sharpened.getHeight();

        int boxWidth = 2, boxHeight = 2; //its kernel box for usm algorithm
        int left = 1, top = 1; //left and top should be more than half of box width and height
        int right = (imgWidth - left), bottom = (imgHeight - top);

        //USM filter parameters
        float usmAmount = amount;
        int usmThreshold = threshold;

        int[][] origPixels = loadFromBitmap(sharpened);
        int[][] blurredPixels = new int[imgWidth][imgHeight];

        boxCar(sharpened, origPixels, blurredPixels, left, top,
                right, bottom, boxWidth, boxHeight);

        unsharpMask(sharpened, origPixels, blurredPixels, left, top,
                right, bottom, usmAmount, usmThreshold);

        return sharpened;
    }

    private static void boxCar(Bitmap loadedImage, int[][] origPixels,
                               int[][] blurredPixels, int left, int top, int right, int bottom,
                               int filtX, int filtY) throws ArrayIndexOutOfBoundsException {


        /*
         * a boundaries of a small 2d array containing adjacent box of pixels for a given
         * pixel is sent to blurPixels function. This function processes for R,G,B and
         * alpha bytes in each pixel in the box separately and reconstructs the averaged pixel
         */

        for (int j = top; j < bottom; j++) {
            for (int i = left; i < right; i++) {

                /*
                 * blur pixels using averaging a box of pixels surrounding the given
                 * pixel (i,j) and saving the result in both blurredPixels and
                 * loadedImage bitmap object
                 */
                blurredPixels[i][j] = blurPixels(origPixels, (i - filtX / 2), (j - filtY / 2),
                        (i + filtX / 2), (j + filtY / 2));
                loadedImage.setPixel(i, j, blurredPixels[i][j]);
            }
        }

    }

    /**
     * This method returns a RGB value taking the mean of
     * RGB values of each pixel in the filter kernel box
     *
     * @param origPixels original untouched pixels
     * @param left       left side kernel box
     * @param top        top side kernel box
     * @param right      right side kernel box
     * @param bottom     bottom side kernel box
     * @return blurred pixel
     */
    private static int blurPixels(int[][] origPixels, int left, int top, int right, int bottom) {
        //transparency is not considered
        int alpha = 0xff000000, red = 0, green = 0, blue = 0;
        int boxSize = (right - left) * (bottom - top);


        //the following nested for loops takes the sum of RGB components of each
        //pixels in the box.
        for (int q = top; q < bottom; q++) {
            for (int p = left; p < right; p++) {
                int pixel = origPixels[p][q];
                red += ((pixel >> 16) & 0xff);
                green += ((pixel >> 8) & 0xff);
                blue += ((pixel) & 0xff);
            }
        }
        /*
         * average is computed using integer arithmetic. If the box size is too large
         * this routine will fail. max box size = (INT_Max/256) = 8,388,608
         */
        red /= boxSize;
        green /= boxSize;
        blue /= boxSize;
        //returns the reconstructed pixel back
        return (alpha | (red << 16) | (green << 8) | blue);
    }

    /**
     * This method calculates the unsharpmask by subtracting original pixels by blurred pixels.
     * Then it sharpens the original image by adding a weighted amount of the unsharpmask
     * back to the original depending on the given threshold value
     *
     * @param usmImage      bitmap image
     * @param origPixels    unchanged pixel
     * @param blurredPixels blurred pixels
     * @param left          left side box
     * @param top           top side box
     * @param right         right side box
     * @param bottom        bottom side box
     * @param amount        amount effect
     * @param threshold     amount threshold
     */
    private static void unsharpMask(Bitmap usmImage,
                                    int[][] origPixels, int[][] blurredPixels, int left,
                                    int top, int right, int bottom, float amount, int threshold) {

        int orgRed = 0, orgGreen = 0, orgBlue = 0;
        int blurredRed = 0, blurredGreen = 0, blurredBlue = 0;
        int usmPixel = 0;
        int alpha = 0xFF000000; //transperency is not considered and always zero

        for (int j = top; j < bottom; j++) {
            for (int i = left; i < right; i++) {
                int origPixel = origPixels[i][j], blurredPixel = blurredPixels[i][j];

                //seperate RGB values of original and blurred pixels into seperate R,G and B values
                orgRed = ((origPixel >> 16) & 0xff);
                orgGreen = ((origPixel >> 8) & 0xff);
                orgBlue = (origPixel & 0xff);
                blurredRed = ((blurredPixel >> 16) & 0xff);
                blurredGreen = ((blurredPixel >> 8) & 0xff);
                blurredBlue = (blurredPixel & 0xff);

                //If the absolute val. of difference between original and blurred
                //values are greater than the given threshold add weighed difference
                //back to the original pixel. If the result is outside (0-255),
                //change it back to the corresponding margin 0 or 255
                if (Math.abs(orgRed - blurredRed) >= threshold) {
                    orgRed = (int) (amount * (orgRed - blurredRed) + orgRed);
                    orgRed = orgRed > 255 ? 255 : orgRed < 0 ? 0 : orgRed;
                }

                if (Math.abs(orgGreen - blurredGreen) >= threshold) {
                    orgGreen = (int) (amount * (orgGreen - blurredGreen) + orgGreen);
                    orgGreen = orgGreen > 255 ? 255 : orgGreen < 0 ? 0 : orgGreen;
                }

                if (Math.abs(orgBlue - blurredBlue) >= threshold) {
                    orgBlue = (int) (amount * (orgBlue - blurredBlue) + orgBlue);
                    orgBlue = orgBlue > 255 ? 255 : orgBlue < 0 ? 0 : orgBlue;
                }

                usmPixel = (alpha | (orgRed << 16) | (orgGreen << 8) | orgBlue);
                usmImage.setPixel(i, j, usmPixel);
            }
        }
    }

    /**
     * Function to load ARGB values of each pixel in to a 2D array
     *
     * @param image image that will turn into 2d array
     * @return 2d array
     */
    private static int[][] loadFromBitmap(Bitmap image) {
        int width = image.getWidth(), height = image.getHeight();
        int[][] pixels = new int[width][height];
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                pixels[i][j] = image.getPixel(i, j);
            }
        }
        return pixels;
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        return sharpen(bitmap, amount, threshold);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        resultContract.getResult(bitmap);
    }
}
