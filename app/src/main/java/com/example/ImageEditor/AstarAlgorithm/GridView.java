package com.example.ImageEditor.AstarAlgorithm;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.List;

public class GridView extends View implements ResultContract {

    public static final int TILE_EMPTY = 0;
    public static final int TILE_START = 1;
    public static final int TILE_END = 2;
    public static final int TILE_BLOCK = 3;
    public static final int TILE_SET = 4;
    public static final int TILE_QUEUE = 5;
    public static final int TILE_PATH = 6;
    private final Paint paint = new Paint();
    private int rows = 20;
    private int cols = 20;
    private int[][] grid;
    private int start_y;
    private int start_x;
    private int stop_y;
    private int stop_x;
    private float tileWidth;
    private float tileHeight;
    private float width;
    private float height;
    private boolean start_clicked = false;
    private boolean stop_clicked = false;

    public GridView(Context context) {
        super(context);
    }

    public GridView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GridView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public GridView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void init(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        grid = new int[rows][cols];
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        tileWidth = width / cols;
        tileHeight = height / rows;
        boolean border = false;
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++) {
                paint.setStyle(Paint.Style.FILL);
                int value = grid[r][c];
                switch (value) {
                    case TILE_EMPTY:
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setColor(Color.BLACK);
                        break;
                    case TILE_START:
                        paint.setColor(Color.RED);
                        break;
                    case TILE_END:
                        paint.setColor(Color.GREEN);
                        break;
                    case TILE_BLOCK:
                        paint.setColor(Color.BLACK);
                        break;
                    case TILE_SET:
                        paint.setColor(Color.CYAN);
                        border = true;
                        break;
                    case TILE_QUEUE:
                        paint.setColor(Color.LTGRAY);
                        border = true;
                        break;
                    case TILE_PATH:
                        paint.setColor(Color.YELLOW);
                        border = true;
                        break;
                }
                canvas.drawRect(c * tileWidth, r * tileHeight, c * tileWidth + tileWidth, r * tileHeight + tileHeight, paint);
                if (border) {
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setColor(Color.BLACK);
                    canvas.drawRect(c * tileWidth, r * tileHeight, c * tileWidth + tileWidth, r * tileHeight + tileHeight, paint);
                }

            }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            int c = (int) (x / tileWidth);
            int r = (int) (y / tileHeight);
            if (start_clicked) {
                grid[start_y][start_x] = 0;
                grid[r][c] = 1;
                start_x = c;
                start_y = r;
                start_clicked = false;
            } else if (stop_clicked) {
                grid[stop_y][stop_x] = 0;
                grid[r][c] = 2;
                stop_x = c;
                stop_y = r;
                stop_clicked = false;
            } else if (grid[r][c] != TILE_START && grid[r][c] != TILE_END) {
                if (grid[r][c] != TILE_BLOCK) {
                    grid[r][c] = TILE_BLOCK;
                } else {
                    grid[r][c] = TILE_EMPTY;
                }
            }


        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float x = event.getX();
            float y = event.getY();
            int c = (int) (x / tileWidth);
            int r = (int) (y / tileHeight);
            if (x > width || y > height || x < 0 || y < 0) {
                return false;
            }
            if (grid[r][c] != TILE_START && grid[r][c] != TILE_END) {
                if (grid[r][c] != TILE_BLOCK) {
                    grid[r][c] = TILE_BLOCK;
                } else {
                    grid[r][c] = TILE_EMPTY;
                }
            }

        }
        invalidate();
        return true;
    }

    public void setStart_clicked(boolean start_clicked) {
        this.start_clicked = start_clicked;
    }

    public void setStop_clicked(boolean stop_clicked) {
        this.stop_clicked = stop_clicked;
    }


    public int[][] getGrid() {
        return grid;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void showFinal(List<Node> result) {
        if (result == null) {
            Toast.makeText(getContext(), "Star and End node can't be null", Toast.LENGTH_LONG).show();
            return;
        }
        result.forEach(node -> {
            int r = node.getRow();
            int c = node.getCol();

            if (grid[r][c] != TILE_START && grid[r][c] != TILE_END) {
                grid[r][c] = TILE_PATH;
            }
        });
        invalidate();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void getResult(List<Node> result) {
        showFinal(result);
    }

    public void clearPath() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int value = grid[r][c];

                if(value == TILE_PATH){
                    grid[r][c] = TILE_EMPTY;
                }

            }
        }
    }

    public void setRowsNCols(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        init(rows,cols);
        invalidate();
    }

}
