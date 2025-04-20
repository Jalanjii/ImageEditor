package com.example.ImageEditor.Spline;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class CurveView extends View {


    public static final int BLUE = 0;
    public static final int YELLOW = 1;
    public static final int GREEN = 2;
    public static final int RED = 3;
    public static Paint colorDefault;
    private static List<Point> listPoints;
    private static Set<Point> setPoints;
    int indice = -1;

    private Paint colorBlack;
    private Paint colorControlPoint;
    private Paint colorRed;
    private Paint colorGreen;
    private Paint colorBlue;

    private boolean isAdd = false;
    private boolean isRemove = false;
    private boolean isMove = false;
    private boolean showControlPoints = true;

    /* Construtores */
    public CurveView(Context context, AttributeSet attrs) {

        super(context, attrs);
        this.init();

    }

    public CurveView(Context context) {

        super(context);
        this.init();

    }


    private void init() {

        listPoints = new ArrayList<>();
        setPoints = new HashSet<>();

        this.colorBlack = new Paint();
        this.colorBlack.setARGB(255, 0, 0, 0);

        this.colorControlPoint = new Paint();
        this.colorControlPoint.setARGB(255, 141, 19, 247);

        this.colorRed = new Paint();
        this.colorRed.setARGB(255, 255, 0, 0);

        this.colorGreen = new Paint();
        this.colorGreen.setARGB(255, 0, 255, 0);

        this.colorBlue = new Paint();
        this.colorBlue.setARGB(255, 0, 0, 255);


        colorDefault = colorBlue;
        selectAdd();

    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);


        for (Point p : listPoints) {
            if (setPoints.contains(p) && !showControlPoints) {

            } else {
                canvas.drawRect(p.getArea(), colorBlack);
            }
        }


        bezier(canvas);


    }

    /**
     * Basically clears all the points
     */
    public void clearList() {

        listPoints.clear();
        invalidate();

    }


    /**
     * This method returns point under x and y coordinate
     * if there is no point then it returns -1
     *
     * @param x Coordinate X
     * @param y Coordinate Y
     * @return point position on list
     */
    private int findPoint(float x, float y) {

        for (Point p : listPoints) {
            if (setPoints.contains(p) && !showControlPoints) {
                return -1;
            }
            RectF r = p.getArea();

            if (r.contains(x, y)) {
                return listPoints.indexOf(p);
            }
        }

        return -1;

    }


    /**
     * Method that draws bezier curves
     *
     * @param canvas
     */
    private void bezier(Canvas canvas) {


        int listSize = listPoints.size();

        if (listSize >= 3) {
            colorDefault = colorBlue;
            defaultColorPref();

            int i = 0;


            //List size bigger than 3 that means we can draw cubic
            while (i + 3 < listSize) {
                colorDefault = colorBlue;

                float lX = Math.abs((listPoints.get(i + 3).getX() - listPoints.get(i).getX()));

                float lY = Math.abs((listPoints.get(i + 3).getY() - listPoints.get(i).getY()));


                float inter = lX > lY ? 1 / lX : 1 / lY;
                setPoints.add(listPoints.get(i + 1));
                setPoints.add(listPoints.get(i + 2));
                drawControlPoints(canvas, i);


                float pX, pY, xAnt = 0, yAnt = 0;

                for (float t = 0; t <= 1; t += inter) {

                    pX = bezierCubicX(i, t);

                    pY = bezierCubicY(i, t);

                    if (t == 0) {

                        xAnt = pX;
                        yAnt = pY;

                    } else {

                        canvas.drawLine(xAnt, yAnt, pX, pY, colorDefault);


                        xAnt = pX;
                        yAnt = pY;

                    }

                }

                i = i + 3;

            }

            //List size bigger than 2 that means we can draw quadratic
            if (i + 2 < listSize) {
                colorDefault = colorGreen;
                defaultColorPref();


                float lX = Math.abs((listPoints.get(i + 2).getX() - listPoints.get(i).getX()));

                float lY = Math.abs((listPoints.get(i + 2).getY() - listPoints.get(i).getY()));

                float inter = lX > lY ? 1 / lX : 1 / lY;
                setPoints.add(listPoints.get(i + 1));
                drawControlPoints(canvas, i);


                float pX, pY, xAnt = 0, yAnt = 0;

                for (float t = 0; t <= 1; t += inter) {

                    pX = bezierQuadraticX(i, t);

                    pY = bezierQuadraticY(i, t);

                    if (t == 0) {

                        xAnt = pX;
                        yAnt = pY;

                    } else {

                        canvas.drawLine(xAnt, yAnt, pX, pY, colorDefault);


                        xAnt = pX;
                        yAnt = pY;

                    }

                }

                i += 2;

            }

            //List size bigger than 1 that means we can draw linear
            else if (i + 1 < listSize) {
                colorDefault = colorRed;
                defaultColorPref();


                float lX = Math.abs((listPoints.get(i + 1).getX() - listPoints.get(i).getX()));


                float lY = Math.abs((listPoints.get(i + 1).getY() - listPoints.get(i).getY()));


                float inter = lX > lY ? 1 / lX : 1 / lY;


                float pX, pY, xAnt = 0, yAnt = 0;

                for (float t = 0; t <= 1; t += inter) {

                    pX = bezierLinearY(i, t);

                    pY = bezierLinearX(i, t);

                    if (t == 0) {

                        xAnt = pX;
                        yAnt = pY;

                    } else {

                        canvas.drawLine(xAnt, yAnt, pX, pY, colorDefault);

                        xAnt = pX;
                        yAnt = pY;
                    }
                }

                i++;

            }


        }

    }

    private void defaultColorPref() {
        colorDefault.setStyle(Paint.Style.STROKE);
        colorDefault.setStrokeWidth(5);
    }

    private void drawControlPoints(Canvas canvas, int i) {
        if (!showControlPoints) {
            return;
        }
        colorControlPoint.setStrokeWidth(3);
        canvas.drawLine(listPoints.get(i).getX(), listPoints.get(i).getY(), listPoints.get(i + 1).getX(), listPoints.get(i + 1).getY(), colorControlPoint);
        if (i + 3 < listPoints.size()) {
            canvas.drawLine(listPoints.get(i + 3).getX(), listPoints.get(i + 3).getY(), listPoints.get(i + 2).getX(), listPoints.get(i + 2).getY(), colorControlPoint);
        } else if (i + 2 < listPoints.size()) {
            canvas.drawLine(listPoints.get(i + 2).getX(), listPoints.get(i + 2).getY(), listPoints.get(i + 1).getX(), listPoints.get(i + 1).getY(), colorControlPoint);
        }
    }

    private float bezierLinearY(int i, float t) {
        return (
                (-t + 1) * listPoints.get(i).getX()
                        + (t) * listPoints.get(i + 1).getX()
        );
    }

    private float bezierLinearX(int i, float t) {
        return (
                (-t + 1) * listPoints.get(i).getY()
                        + (t) * listPoints.get(i + 1).getY()
        );
    }

    private float bezierQuadraticY(int i, float t) {
        return (float) (
                Math.pow((1 - t), 2) * listPoints.get(i).getY() // (1 − t)^2[B0]
                        + 2 * t * (1 - t) * listPoints.get(i + 1).getY() // 2t(1 − t)[B1]
                        + Math.pow(t, 2) * listPoints.get(i + 2).getY() // t^2[B2]
        );
    }

    private float bezierQuadraticX(int i, float t) {
        return (float) (
                Math.pow((1 - t), 2) * listPoints.get(i).getX() // (1 − t)^2[B0]
                        + 2 * t * (1 - t) * listPoints.get(i + 1).getX() // 2t(1 − t)[B1]
                        + Math.pow(t, 2) * listPoints.get(i + 2).getX() // t^2[B2]
        );
    }

    private float bezierCubicY(int i, float t) {
        return (float) (Math.pow((1 - t), 3) * listPoints.get(i).getY() // (1 - t)^3[B0]
                + 3 * t * Math.pow((1 - t), 2) * listPoints.get(i + 1).getY() // 3t(1 - t)^2[B1]
                + 3 * Math.pow(t, 2) * (1 - t) * listPoints.get(i + 2).getY() // 3t^2(1 - t)[B2]
                + Math.pow(t, 3) * listPoints.get(i + 3).getY() // t^3[B3]
        );
    }

    private float bezierCubicX(int i, float t) {
        return (float) (
                Math.pow((1 - t), 3) * listPoints.get(i).getX() // (1 - t)^3[B0]
                        + 3 * t * Math.pow((1 - t), 2) * listPoints.get(i + 1).getX() // 3t(1 - t)^2[B1]
                        + 3 * Math.pow(t, 2) * (1 - t) * listPoints.get(i + 2).getX() // 3t^2(1 - t)[B2]
                        + Math.pow(t, 3) * listPoints.get(i + 3).getX() // t^3[B3]
        );
    }


//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        float x = event.getX();
//        float y = event.getY();
//
//        Point selectedPoint;
//
//        switch (event.getAction()) {
//
//            case MotionEvent.ACTION_DOWN:
//
//                if (isAdd) {
//
//                    listPoints.add(new Point(x, y));
//
//                } else if (isRemove) {
//
//
//                    int i = findPoint(x, y);
//
//                    if (i != -1) {
//
//                        listPoints.remove(i);
//
//                    }
//
//                } else if (isMove) {
//
//                    indice = findPoint(x, y);
//
//                }
//
//                break;
//
//            case MotionEvent.ACTION_MOVE:
//
//                if (indice != -1) {
//
//                    selectedPoint = listPoints.get(indice);
//                    selectedPoint.movePoint(x, y);
//
//                }
//
//                break;
//
//            case MotionEvent.ACTION_UP:
//
//                selectedPoint = null;
//                indice = -1;
//
//        }
//
//        invalidate();
//
//        return true;
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        Point selectedPoint;

        int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN) {

            if (isAdd) {
                listPoints.add(new Point(x, y));
            } else if (isRemove) {
                int i = findPoint(x, y);
                if (i != -1) {
                    listPoints.remove(i);
                }
            } else if (isMove) {
                indice = findPoint(x, y);
            }

        } else if (action == MotionEvent.ACTION_MOVE) {

            if (indice != -1) {
                selectedPoint = listPoints.get(indice);
                selectedPoint.movePoint(x, y);
            }

        } else if (action == MotionEvent.ACTION_UP) {

            selectedPoint = null;
            indice = -1;

        }

        invalidate();

        return true;
    }

    public void selectAdd() {
        setVisibilityControlPoints(true);
        this.isAdd = true;
        this.isRemove = false;
        this.isMove = false;
        invalidate();
    }

    public void selectMove() {
        setVisibilityControlPoints(true);
        this.isAdd = false;
        this.isRemove = false;
        this.isMove = true;
        invalidate();
    }

    public void selectDelete() {
        setVisibilityControlPoints(true);
        this.isAdd = false;
        this.isRemove = true;
        this.isMove = false;
        invalidate();
    }

    public void setVisibilityControlPoints(boolean b) {
        this.showControlPoints = b;
        if (!showControlPoints) {
            this.isAdd = false;
            this.isRemove = false;
            this.isMove = false;
        }
        invalidate();
    }
}
