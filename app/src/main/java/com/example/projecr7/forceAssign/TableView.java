package com.example.projecr7.forceAssign;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.projecr7.R;

public class TableView extends View {
    Paint mPaint, otherPaint, outerPaint, mTextPaint, mSmllTextPaint, tablePaint;
    RectF mRectF;
    int mPadding;

    final String TAG = "TableView";

    float arcLeft, arcTop, arcRight, arcBottom, circleTableR, seatR, smallSeatR, seatCircleR;

    int numberOfSests, tableType;
    String tableName;

    Canvas mCanvas;

    Path mPath;


    public TableView(Context context, int numberOfSests, int tableType, String tableName) {
        super(context);

        this.numberOfSests = numberOfSests;
        this.tableType = tableType;
        this.tableName = tableName;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(5);


        mTextPaint = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(pxFromDp(context, 15));

        mSmllTextPaint = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        mSmllTextPaint.setColor(Color.WHITE);
        mSmllTextPaint.setTextSize(pxFromDp(context, 10));

        otherPaint = new Paint();
        otherPaint.setStyle(Paint.Style.FILL);
        otherPaint.setColor(Color.BLACK);

        tablePaint = new Paint();
        tablePaint.setStyle(Paint.Style.FILL);
        int YYllo = ContextCompat.getColor(context, R.color.YYllo);
        tablePaint.setColor(YYllo);

        outerPaint = new Paint();
        outerPaint.setStyle(Paint.Style.FILL);
        outerPaint.setColor(Color.YELLOW);

        mPadding = 100;


        DisplayMetrics displayMetrics = new DisplayMetrics();

        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);


        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        arcLeft = pxFromDp(context, 20);
        arcTop = pxFromDp(context, 20);
        circleTableR = pxFromDp(context, 100);
        seatCircleR = pxFromDp(context, 120);
        seatR = pxFromDp(context, 15);
        smallSeatR = pxFromDp(context, 10);
        arcRight = pxFromDp(context, 100);
        arcBottom = pxFromDp(context, 100);


        Point p1 = new Point((int) pxFromDp(context, 80) + (screenWidth / 2), (int) pxFromDp(context, 40));
        Point p2 = new Point((int) pxFromDp(context, 40) + (screenWidth / 2), (int) pxFromDp(context, 80));
        Point p3 = new Point((int) pxFromDp(context, 120) + (screenWidth / 2), (int) pxFromDp(context, 80));

        mPath = new Path();
        mPath.moveTo(p1.x, p1.y);
        mPath.lineTo(p2.x, p2.y);
        mPath.lineTo(p3.x, p3.y);
        mPath.close();

        mRectF = new RectF(screenWidth / 4, screenHeight / 3, screenWidth / 6, screenHeight / 2);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCanvas = canvas;

        canvas.drawColor(Color.WHITE);
        if(tableType == -1){
            Log.i(TAG, "return====================");
            return;
        }
        Log.i(TAG, "not return====================");
        float centerX = getWidth() / 2.0f;
        float centerY = getHeight() / 2.0f;
        float x, y, padding;
        switch (tableType){
            case 0:
                canvas.drawCircle(centerX, centerY, circleTableR, tablePaint);
                double angle = 360.0 / (double)numberOfSests;
                double currentAngle = 0;
                for(int i = 0; i < numberOfSests; i++){
                    x = centerX + (float)Math.cos( Math.toRadians(currentAngle)) * seatCircleR;
                    y = centerY + (float)Math.sin( Math.toRadians(currentAngle)) * seatCircleR;
                    Log.i(TAG, x + "-" + y);
                    canvas.drawCircle(x, y, seatR, otherPaint);
                    canvas.drawText(Integer.toString(i+1), x - 20.0f, y + 17.0f, mTextPaint);
                    currentAngle += angle;
                }
                break;
            case 1:
                canvas.drawRect(
                getLeft() + (getRight() - getLeft()) / 9,
                getTop() + (getBottom() - getTop()) / 3,
                getRight() - (getRight() - getLeft()) / 9,
                getBottom() - (getBottom() - getTop()) / 3, tablePaint);
                padding = (((getLeft() + (getRight() - getLeft()) / 9) * 7) - 60.0f) / ((numberOfSests / 2) - 1);
                for(int i = 0; i < numberOfSests; i++){
                    if(i < numberOfSests / 2){
                        x = getLeft() + (getRight() - getLeft()) / 9.0f + 30.0f + padding * i;
                        y = getTop() + (getBottom() - getTop()) / 3 - 50.0f;
                    }
                    else{
                        x = getRight() - (getRight() - getLeft()) / 9.0f - 30.0f - padding * (i-(numberOfSests / 2));
                        y = getBottom() - (getBottom() - getTop()) / 3 + 50.0f;
                    }
                    if(numberOfSests >= 14) {
                        canvas.drawCircle(x, y, smallSeatR, otherPaint);
                        canvas.drawText(Integer.toString(i + 1), x - 13.0f, y + 10.0f, mSmllTextPaint);
                    }
                    else {
                        canvas.drawCircle(x, y, seatR, otherPaint);
                        canvas.drawText(Integer.toString(i + 1), x - 20.0f, y + 17.0f, mTextPaint);
                    }
                }
                break;
            case 2:
                canvas.drawRect(
                        getLeft() + (getRight() - getLeft()) / 8,
                        getTop() + (getBottom() - getTop()) / 3,
                        getRight() - (getRight() - getLeft()) / 8,
                        getBottom() - (getBottom() - getTop()) / 3, tablePaint);
                padding = (((getLeft() + (getRight() - getLeft()) / 8) * 6) - 60.0f) / (((numberOfSests-2) / 2) - 1);
                for(int i = 0; i < numberOfSests; i++){
                    if(i < (numberOfSests / 2) - 1){
                        x = getLeft() + (getRight() - getLeft()) / 8.0f + 30.0f + padding * i;
                        y = getTop() + (getBottom() - getTop()) / 3 - 50.0f;
                    }
                    else if(i == (numberOfSests / 2) - 1){
                        x = getRight() - (getRight() - getLeft()) / 8.0f + 50.0f;
                        y = centerY;
                    }
                    else if(i == numberOfSests - 1){
                        x = getLeft() + (getRight() - getLeft()) / 8.0f - 50.0f;
                        y = centerY;
                    }
                    else{
                        x = getRight() - (getRight() - getLeft()) / 8.0f - 30.0f - padding * (i-(numberOfSests / 2));
                        y = getBottom() - (getBottom() - getTop()) / 3 + 50.0f;
                    }
                    if(numberOfSests >= 14) {
                        canvas.drawCircle(x, y, smallSeatR, otherPaint);
                        canvas.drawText(Integer.toString(i + 1), x - 13.0f, y + 10.0f, mSmllTextPaint);
                    }
                    else {
                        canvas.drawCircle(x, y, seatR, otherPaint);
                        canvas.drawText(Integer.toString(i + 1), x - 20.0f, y + 17.0f, mTextPaint);
                    }
                }
                break;

        }





//        canvas.drawText("Canvas basics", (float) (getWidth() * 0.3), (float) (getHeight() * 0.8), mTextPaint);

    }

    public void update(int numberOfSests, int tableType, String tableName){
        this.numberOfSests = numberOfSests;
        this.tableType = tableType;
        this.tableName = tableName;
    }


    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
}
