package com.lanltn.timetablecustomview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;

public class TimeRulerLabel extends View {

    private int
            mNumRow = 24,
            mCellHeight = 70,
            mWidthOfRuler = 120,
            mTopPadding = 10;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mTextSizeLabel = 20;

    public TimeRulerLabel(Context context) {
        this(context, null);
    }

    public TimeRulerLabel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calculateDimensions();
    }


    private void calculateDimensions() {
        if (mNumRow < 1) {
            return;
        }
        mWidthOfRuler = getWidth();

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mNumRow == 0)
            return;

        //draw line horizontal
        int textLabelHourMarginLeft = 20;
        for (int i = 0; i < mNumRow * 6 - 1; i++) {
            mPaint.setColor(getResources().getColor(R.color.colorLabelHour));
            if (i % 3 == 0) {
                if (i % 2 == 0) {
                    mPaint.setStrokeWidth(6);
                    canvas.drawLine(
                            mWidthOfRuler - 30,
                            mTopPadding + i * mCellHeight / 6 + mPaint.getStrokeWidth() / 2,
                            mWidthOfRuler,
                            mTopPadding + i * mCellHeight / 6 + mPaint.getStrokeWidth() / 2,
                            mPaint);

                    //draw hours label
                    mPaint.setTextSize(mTextSizeLabel);
                    mPaint.setStrokeWidth(1);
                    canvas.drawText(
                            i / 6 + ":00",
                            textLabelHourMarginLeft,
                            mTopPadding + i * mCellHeight / 6 + mPaint.getTextSize() / 2,
                            mPaint);

                } else {
                    mPaint.setStrokeWidth(2);
                    canvas.drawLine(
                            mWidthOfRuler - 25,
                            mTopPadding + i * mCellHeight / 6,
                            mWidthOfRuler,
                            mTopPadding + i * mCellHeight / 6,
                            mPaint);
                }
            } else {
                mPaint.setStrokeWidth(2);
                canvas.drawLine(
                        mWidthOfRuler - 15,
                        mTopPadding + i * mCellHeight / 6,
                        mWidthOfRuler,
                        mTopPadding + i * mCellHeight / 6,
                        mPaint);
            }

        }
        drawIndicatorLineWithCurrentTime(canvas);
    }

    private void drawIndicatorLineWithCurrentTime(Canvas canvas) {
        //get current time
        Calendar timeCurrent = Calendar.getInstance();
        int currentHourIn24Format = timeCurrent.get(Calendar.HOUR_OF_DAY);
        int currentMinute = timeCurrent.get(Calendar.MINUTE);

        //Create label hour
        String labelHour;
        if (currentMinute < 10) {
            labelHour = currentHourIn24Format + ":0" + currentMinute;
        } else {
            labelHour = currentHourIn24Format + ":" + currentMinute;
        }

        float time = currentHourIn24Format + currentMinute * 1.0f / 60; //EX: current time is 5 o'clock

        //Rect bound of label
        mPaint.setTextSize(mTextSizeLabel);
        mPaint.setStrokeWidth(1);

        int width_bound = (int) mPaint.getTextSize() + 2;
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(Color.WHITE);
        canvas.drawRoundRect(
                10,
                mTopPadding + time * mCellHeight - width_bound / 2,
                85,
                width_bound + mTopPadding + time * mCellHeight,
                5,
                5,
                mPaint);

        mPaint.setTextSize(mTextSizeLabel);
        mPaint.setStrokeWidth(1);
        mPaint.setColor(getResources().getColor(R.color.colorAccent));
        canvas.drawText(
                labelHour,
                20,
                mTopPadding + time * mCellHeight + mPaint.getTextSize() / 2,
                mPaint);
    }

    public void onScroll(int deltaX, int detalY) {
        if (deltaX != 0 || detalY != 0) {
            scrollBy(deltaX, detalY);
        }
        scrollTo(0,detalY);
        Toast.makeText(getContext(), detalY + " ", Toast.LENGTH_SHORT).show();
        invalidate();
    }
}
