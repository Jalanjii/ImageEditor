package com.example.ImageEditor;

import android.graphics.Bitmap;
import android.util.Log;

public class GaussianBlur {

    /**
     * This method blurs all picture using gaussian and stack blur algorithm
     * so it's not gaussian blur it's faster
     *
     * @param bitmap Bitmap that will blur
     * @param radius it's kind of effect because it's radius of blur
     * @return blurred bitmaps 1d int array
     */
    private static int[] blurAllPicture(Bitmap bitmap, int radius) {


        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = getRed(p);
                sir[1] = getGreen(p);
                sir[2] = (getBlue(p));

                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = getRed(p);
                sir[1] = getGreen(p);
                sir[2] = getBlue(p);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        return pix;
    }

    /**
     * Well it's public blur method that uses
     *
     * @param sentBitmap Bitmap that will blur
     * @param radius     it's kind of effect because it's radius of blur
     * @param x          circle center x
     * @param y          circle center y
     * @param circle     circle radius that will blur
     * @return partly blurred bitmap
     */
    public static Bitmap blur(Bitmap sentBitmap, int radius, int x, int y, int circle) {
        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int sizewh = w * h;
        int[] original = new int[sizewh];
        bitmap.getPixels(original, 0, w, 0, 0, w, h);
        int[] pix = blurAllPicture(sentBitmap, radius);

        for (int i = 0; i < original.length; i++) {
            int valy = i / w;
            int valx = i - (valy * w);

            if (isPointInCircle(valx, valy, x, y, Math.min(w, h) / reverseIntFromSeekBar(circle))) {
                original[i] = pix[i];
            }
        }


        bitmap.setPixels(original, 0, w, 0, 0, w, h);

        return bitmap;

    }

    private static int getBlue(int p) {
        return p & 0x0000ff;
    }

    private static int getGreen(int p) {
        return (p & 0x00ff00) >> 8;
    }

    private static int getRed(int p) {
        return (p & 0xff0000) >> 16;
    }

    /**
     * This method checks is point in and on circle
     *
     * @param pointx  Point x Coordinate
     * @param pointy  Point y Coordinate
     * @param circlex Circle x Coordinate
     * @param circley Circle y Coordinate
     * @param radius  Circle radius
     * @return returns true if point is in and on circle
     */
    private static boolean isPointInCircle(int pointx, int pointy, int circlex, int circley, int radius) {
        int deltaX = pointx - circlex;
        int deltaY = pointy - circley;

        int distanceSquared = deltaX * deltaX + deltaY * deltaY;

        int radiusSquared = radius * radius;

        return distanceSquared <= radiusSquared;
    }

    /**
     * This method reverse int from seekBar because of image.width or height / i
     *
     * @param i int from seekBar
     * @return seekBar.Max - i
     */
    private static int reverseIntFromSeekBar(int i) {
        return i == 20 ? 2 : (20 - i) * 2;
    }
}
