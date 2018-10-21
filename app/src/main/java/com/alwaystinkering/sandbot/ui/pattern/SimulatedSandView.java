package com.alwaystinkering.sandbot.ui.pattern;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.alwaystinkering.sandbot.model.pattern.Coordinate;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SimulatedSandView extends View {

    private static final String TAG = "";

    private Paint linePaint;
    private int viewRadius;
    private int tableRadius = 200; //table radius in mm

    private List<Coordinate> coordinates = new CopyOnWriteArrayList<>();

    public SimulatedSandView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        linePaint = new Paint();
    }

    public void initTableRadius(int tableRadius) {
        this.tableRadius = tableRadius;
    }

    public void addCoordinateAndRender(Coordinate c) {
        coordinates.add(c);
        update();
    }

    private void update() {
        this.postInvalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        viewRadius = getWidth() / 2;

        if (coordinates.size() == 0) {
            return;
        }

        boolean first = true;

        float lastX = translateTablePointToView(coordinates.get(0).getX());
        float lastY = translateTablePointToView(coordinates.get(0).getY());

        for (Coordinate c : coordinates) {
            if (first) {
                linePaint.setColor(Color.TRANSPARENT);
                first = false;
            } else {
                linePaint.setStrokeWidth(8);
                linePaint.setColor(Color.WHITE);
            }

            float x = translateTablePointToView(c.getX());
            float y = translateTablePointToView(c.getY());

            canvas.drawLine(lastX, lastY, x, y, linePaint);

            lastX = x;
            lastY = y;
        }
    }

    private float translateTablePointToView(float point) {
        return ((point / tableRadius)) * viewRadius + viewRadius;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = Math.min(getMeasuredWidth(), getMeasuredHeight());
        setMeasuredDimension(size, size);
    }

    public void clear() {
        coordinates.clear();
        this.postInvalidate();
    }
}