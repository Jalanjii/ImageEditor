package com.example.ImageEditor.Spline;

import android.graphics.RectF;

import androidx.annotation.Nullable;

public class Point {


    private final RectF area;
    private float x;
    private float y;

    public Point(float x, float y) {

        this.x = x;
        this.y = y;
        this.area = new RectF(x - 25, y - 25, x + 25, y + 25);

    }

    public float getX() {

        return x;

    }

    private void setX(float x) {

        this.x = x;

    }

    public float getY() {

        return y;

    }

    private void setY(float y) {

        this.y = y;

    }

    public RectF getArea() {

        return area;

    }

    /**
     * @param x new Coordinate X
     * @param y new Coordinate Y
     */
    public void movePoint(float x, float y) {

        this.setX(x);
        this.setY(y);
        updateArea();

    }

    private void updateArea() {

        this.area.set(x - 25, y - 25, x + 25, y + 25);
    }

    @Override
    public int hashCode() {
        return Math.round(getX()+getY()+10);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        Point objP = (Point) obj;
        return this.getY() == objP.getY() && this.getX() == objP.getX();
    }
}
