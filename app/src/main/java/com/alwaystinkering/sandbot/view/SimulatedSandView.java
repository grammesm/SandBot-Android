package com.alwaystinkering.sandbot.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.alwaystinkering.sandbot.R;
import com.alwaystinkering.sandbot.model.pattern.Coordinate;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SimulatedSandView extends View {

    private static final String TAG = "";

    private Paint linePaint;
    private Paint borderPaint;
    private Paint circlePaint;
    private int viewRadius;
    private int tableRadius = 380; //table radius in mm

//    private Drawable ballImage;

    private List<Coordinate> coordinates = new CopyOnWriteArrayList<>();

    public SimulatedSandView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        linePaint = new Paint();
        borderPaint = new Paint();
        borderPaint.setStrokeWidth(10);
        borderPaint.setColor(Color.DKGRAY);
        circlePaint = new Paint();
        circlePaint.setStrokeWidth(2);
        circlePaint.setColor(Color.BLACK);
//        ballImage = context.getResources().getDrawable(R.drawable.steel_ball);
    }

    public void initTableRadius(int tableRadius) {
        this.tableRadius = tableRadius;
    }

    public void addCoordinateAndRender(Coordinate c) {
        if (c != null) {
            coordinates.add(c);
            update();
        }
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
                linePaint.setStrokeWidth(4);
                linePaint.setColor(getContext().getResources().getColor(R.color.sandbot_sand));
            }

            float x = translateTablePointToView(c.getX());
            float y = translateTablePointToView(c.getY());

            canvas.drawLine(lastX, lastY, x, y, borderPaint);
            canvas.drawLine(lastX, lastY, x, y, linePaint);

            lastX = x;
            lastY = y;
        }

        canvas.drawCircle(lastX, lastY, 16, circlePaint);
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
