package com.lanltn.timetablecustomview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TimeTableView extends View {

    private static final int
            FES_EVENT_NORMAL = 0,
            FES_EVENT_FAVORITE = 1,
            FES_EVENT_WATCH = 2,
            RECT_WHITE = 100;

    private static final float MIN_SCALE = 0.9f;
    private static final float MAX_SCALE = 3.0f;
    private static final int ANIMATED_SCROLL_GAP = 250;
    private static final int
            MIN_CELL_WIDTH = 120,
            MAX_CELL_WIDTH = 350,
            MIN_CELL_HEIGHT = 70,
            MAX_CELL_HEIGHT = 180;

    private String[] titles = {
            "RED MARQUEE",
            "GREEN STAGE",
            "WHITE STAGE",
            "GYPSY AVALON",
            "FIELD OF HEAVEN",
            "ORANGE CAFE"};

    private int numColumns = 6, numRows = 24;
    private int cellWidth, cellHeight;
    private int heightTitle, widthLabelHours;
    private int mTextSizeLabel = 20;
    private long mLastScroll;
    private float initialTouchX;
    private float initialTouchY;

    private Paint blackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private ScaleGestureDetector mScaleDetector;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    private List<FesEvent> mListFesEvent = new ArrayList<>();

    public TimeTableView(Context context) {
        this(context, null);
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
    }

    public TimeTableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        mScroller = new Scroller(getContext());
    }


    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
        calculateDimensions();
    }

    public int getNumColumns() {
        return numColumns;
    }

    public void setNumRows(int numRows) {
        this.numRows = numRows;
        calculateDimensions();
    }

    public int getNumRows() {
        return numRows;
    }

    public int getCellWidth() {
        return cellWidth;
    }

    public void setCellWidth(int cellWidth) {
        this.cellWidth = cellWidth;
    }

    public int getCellHeight() {
        return cellHeight;
    }

    public void setCellHeight(int cellHeight) {
        this.cellHeight = cellHeight;
    }

    public int getHeightTitle() {
        return heightTitle;
    }

    public void setHeightTitle(int heightTitle) {
        this.heightTitle = heightTitle;
    }

    public int getWidthLabelHours() {
        return widthLabelHours;
    }

    public void setWidthLabelHours(int widthLabelHours) {
        this.widthLabelHours = widthLabelHours;
    }

    public String[] getTitles() {
        return titles;
    }

    public void setTitles(String[] titles) {
        this.titles = titles;
    }

    public List<FesEvent> getmListFesEvent() {
        return mListFesEvent;
    }

    public void setmListFesEvent(List<FesEvent> mListFesEvent) {
        this.mListFesEvent = mListFesEvent;
    }

    public void setFocusToTime(String time) {
        float timeFocus = convertTimeStringToHour(time);
        scrollTo(0, (int) (getHeightTitle() + timeFocus * cellHeight / 2));
    }

    public void setFocusToEvent(FesEvent fesEvent) {
    }

    ;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calculateDimensions();
    }


    private void calculateDimensions() {
        if (numColumns < 1 || numRows < 1) {
            return;
        }
        cellWidth = (getWidth() - widthLabelHours) / numColumns;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (numColumns == 0 || numRows == 0) {
            return;
        }

        int height = getCellHeight() * numRows;
        int leftPaddingTimeTable = getWidthLabelHours();
        int topPaddingTimeTable = getHeightTitle();


        //Draw icon top left
        Drawable drawable = getResources().getDrawable(R.drawable.ic_access_time);
        Bitmap bitmap = drawableToBitmap(drawable);
        int widthBitmap = bitmap.getWidth();
        int heightBitmap = bitmap.getHeight();
        canvas.drawBitmap(bitmap, (leftPaddingTimeTable - widthBitmap) / 2,
                (topPaddingTimeTable - heightBitmap) / 2, mPaint);


        //Draw Rect vertical
        for (int i = 0; i < numColumns; i++) {
            if (i % 2 == 0) {
                mPaint.setColor(getResources().getColor(R.color.colorCol));
            } else {
                mPaint.setColor(getResources().getColor(R.color.colorColDark));
            }

            mPaint.setStrokeWidth(cellWidth);
            int start_X_line = getWidthLabelHours() + i * cellWidth;
            canvas.drawLine(
                    start_X_line + cellWidth / 2,
                    0,
                    start_X_line + cellWidth / 2,
                    height + getHeightTitle(),
                    mPaint);

            //draw text title
            blackPaint.setColor(getResources().getColor(R.color.colorTitle));
            blackPaint.setTextSize(mTextSizeLabel);
            blackPaint.setStrokeWidth(1);
            String[] words = titles[i].split(" ");
            for (int k = 0; k < words.length; k++) {
                canvas.drawText(words[k], leftPaddingTimeTable + i * cellWidth + 10, 30 + k * 25, blackPaint);
            }
        }

        //draw line horizontal
        int textLabelHourMarginLeft = 20;
        for (int i = 0; i < numRows * 6 - 1; i++) {
            blackPaint.setColor(getResources().getColor(R.color.colorLabelHour));
            if (i % 3 == 0) {
                if (i % 2 == 0) {
                    blackPaint.setStrokeWidth(6);
                    canvas.drawLine(
                            leftPaddingTimeTable - 30,
                            topPaddingTimeTable + i * cellHeight / 6 + blackPaint.getStrokeWidth() / 2,
                            leftPaddingTimeTable,
                            topPaddingTimeTable + i * cellHeight / 6 + blackPaint.getStrokeWidth() / 2,
                            blackPaint);

                    //draw hours label
                    blackPaint.setTextSize(mTextSizeLabel);
                    blackPaint.setStrokeWidth(1);
                    canvas.drawText(
                            i / 6 + ":00",
                            textLabelHourMarginLeft,
                            topPaddingTimeTable + i * cellHeight / 6 + blackPaint.getTextSize() / 2,
                            blackPaint);

                } else {
                    blackPaint.setStrokeWidth(2);
                    canvas.drawLine(
                            leftPaddingTimeTable - 25,
                            topPaddingTimeTable + i * cellHeight / 6,
                            leftPaddingTimeTable,
                            topPaddingTimeTable + i * cellHeight / 6,
                            blackPaint);
                }
            } else {
                blackPaint.setStrokeWidth(2);
                canvas.drawLine(
                        leftPaddingTimeTable - 15,
                        topPaddingTimeTable + i * cellHeight / 6,
                        leftPaddingTimeTable,
                        topPaddingTimeTable + i * cellHeight / 6,
                        blackPaint);
            }

        }


        for (int i = 0; i < numRows * 6 - 1; i++) {
            blackPaint.setColor(getResources().getColor(R.color.colorLabelHour));
            if (i % 3 == 0) {
                for (int col = 0; col < numColumns; col++) {
                    if (col % 2 == 0) {
                        blackPaint.setStrokeWidth(1.5f);
                        blackPaint.setAlpha(100);
                        canvas.drawLine(
                                leftPaddingTimeTable + col * cellWidth,
                                topPaddingTimeTable + i * cellHeight / 6,
                                leftPaddingTimeTable + (col + 1) * cellWidth,
                                topPaddingTimeTable + i * cellHeight / 6, blackPaint);
                    } else {
                        blackPaint.setStrokeWidth(1.5f);
                        blackPaint.setAlpha(75);
                        canvas.drawLine(
                                leftPaddingTimeTable + col * cellWidth,
                                topPaddingTimeTable + i * cellHeight / 6,
                                leftPaddingTimeTable + (col + 1) * cellWidth,
                                topPaddingTimeTable + i * cellHeight / 6, blackPaint);
                    }
                }
            }
        }

        drawFesEventCard(canvas);

        drawIndicatorLineWithCurrentTime(canvas);

    }

    private void drawFesEventCard(Canvas canvas) {

        Paint _mPaint;
        float timeFesStart, timeFesEnd;
        String nameFes;
        float
                left,
                top,
                height_rect,
                width_rect;

        for (FesEvent fesEvent : mListFesEvent) {
            if (fesEvent != null) {
                nameFes = fesEvent.getmNameFesEvent();
                left = getWidthLabelHours() + fesEvent.getIdCol() * getCellWidth();
                timeFesStart = convertTimeStringToHour(fesEvent.getmStartFesEvent());
                timeFesEnd = convertTimeStringToHour(fesEvent.getmEndFesEvent());
                top = getHeightTitle() + timeFesStart * getCellHeight();
                height_rect = (timeFesEnd - timeFesStart) * getCellHeight();
                width_rect = cellWidth;

                //Draw rect card fes event
                _mPaint = setStyleRect(fesEvent.getIdFesType());

                //Support with api >= 21
                Rect rect = new Rect((int) left, (int) top, (int) (left + width_rect), (int) (top + height_rect));
                RectF rectF = new RectF(rect);
                canvas.drawRoundRect(
                        rectF,
                        10,
                        10,
                        _mPaint);

                //Draw name of fes event center rectangle
                _mPaint = setStyleText(fesEvent.getIdFesType());
                drawTextCenterOfRect(canvas, _mPaint, nameFes, left, top, width_rect, height_rect);

                //Draw time begin and time end of fes event
                _mPaint.setColor(Color.WHITE);
                _mPaint.setStrokeWidth(1);
                _mPaint.setTextSize(10);
                canvas.drawText(fesEvent.getmStartFesEvent(), left + 10, top + 15, _mPaint);
                canvas.drawText(fesEvent.getmEndFesEvent(), left + 10, top + height_rect - 10, _mPaint);
            }
        }

    }

    public Paint setStyleRect(int idStyle) {
        Paint _mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _mPaint.setStrokeWidth(2);
        switch (idStyle) {
            case FES_EVENT_NORMAL:
                _mPaint.setStyle(Paint.Style.STROKE);
                _mPaint.setColor(Color.WHITE);
                break;
            case FES_EVENT_FAVORITE:
                _mPaint.setStyle(Paint.Style.STROKE);
                _mPaint.setColor(getResources().getColor(R.color.colorAccent));
                break;
            case FES_EVENT_WATCH:
                _mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                _mPaint.setColor(getResources().getColor(R.color.colorAccent));
                break;
            case RECT_WHITE:
                _mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                _mPaint.setColor(Color.WHITE);
                break;
        }
        return _mPaint;
    }

    public Paint setStyleText(int idStyle) {
        Paint _mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _mPaint.setStrokeWidth(3);
        _mPaint.setStyle(Paint.Style.FILL);
        _mPaint.setTextSize(27);
        switch (idStyle) {
            case FES_EVENT_NORMAL:
                _mPaint.setColor(Color.WHITE);
                break;
            case FES_EVENT_FAVORITE:
                _mPaint.setColor(getResources().getColor(R.color.colorAccent));
                break;
            case FES_EVENT_WATCH:
                _mPaint.setColor(Color.WHITE);
                break;
        }
        return _mPaint;
    }

    private void drawTextCenterOfRect(Canvas canvas, Paint paint, String text, float posX, float posY, float with_rect, float height_rect) {
        Rect rectBoundText = new Rect();
        paint.setTextAlign(Paint.Align.LEFT);
        paint.getTextBounds(text, 0, text.length(), rectBoundText);
        float offsetX = with_rect / 2f - rectBoundText.width() / 2f;
        float offsetY = height_rect / 2f + rectBoundText.height() / 2f;
        canvas.drawText(text, posX + offsetX, posY + offsetY, paint);
    }

    public float convertTimeStringToHour(String time) {
        String[] timeParts = time.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);
        return hour + minute * 1.0f / 60;
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

        blackPaint.setColor(getResources().getColor(R.color.colorHighLine));
        blackPaint.setStrokeWidth(6);
        canvas.drawLine(
                getWidthLabelHours() - 80,
                getHeightTitle() + time * cellHeight + blackPaint.getStrokeWidth() / 2, getCellWidth() * 6 + getWidthLabelHours(),
                getHeightTitle() + time * cellHeight + blackPaint.getStrokeWidth() / 2, blackPaint);

        //Rect bound of label
        blackPaint.setTextSize(mTextSizeLabel);
        blackPaint.setStrokeWidth(1);

        int width_bound = (int) blackPaint.getTextSize() + 2;
        blackPaint = setStyleRect(RECT_WHITE);
        canvas.drawRoundRect(
                10,
                getHeightTitle() + time * cellHeight - width_bound / 2,
                85,
                width_bound + getHeightTitle() + time * cellHeight,
                5,
                5,
                blackPaint);

        blackPaint.setTextSize(mTextSizeLabel);
        blackPaint.setStrokeWidth(1);
        blackPaint.setColor(getResources().getColor(R.color.colorAccent));
        canvas.drawText(
                labelHour,
                20,
                getHeightTitle() + time * cellHeight + blackPaint.getTextSize() / 2,
                blackPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mScaleDetector.onTouchEvent(event);
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        float pos_X, pos_Y;
        pos_X = event.getX();
        pos_Y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //Remember last touch position
                initialTouchX = pos_X;
                initialTouchY = pos_Y;

                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                // Scroll to follow the motion event
                int deltaX = (int) (initialTouchX - pos_X);
                int deltaY = (int) (initialTouchY - pos_Y);

                initialTouchX = pos_X;
                initialTouchY = pos_Y;

                if (deltaX < 0) {
                    if (getScrollX() < 0) {
                        deltaX = 0;
                    }
                } else if (deltaX > 0) {
                    final int rightEdge = getWidth() - getPaddingRight();
                    final int availableToScroll = getWidthLabelHours() + getNumColumns() * getCellWidth() - getScrollX() - rightEdge;
                    if (availableToScroll > 0) {
                        deltaX = Math.min(availableToScroll, deltaX);
                    } else {
                        deltaX = 0;
                    }
                }
                if (deltaY < 0) {
                    if (getScrollY() < 0) {
                        deltaY = 0;
                    }
                } else if (deltaY > 0) {
                    final int bottomEdge = getHeight() - getPaddingBottom();
                    final int availableToScroll = getHeightTitle() + getNumRows() * getCellHeight() - getScrollY() - bottomEdge;
                    if (availableToScroll > 0) {
                        deltaY = Math.min(availableToScroll, deltaY);
                    } else {
                        deltaY = 0;
                    }
                }
                doScroll(deltaX, deltaY);
                break;

            case MotionEvent.ACTION_UP:
//                final VelocityTracker velocityTracker = mVelocityTracker;
//                velocityTracker.computeCurrentVelocity(1000);
//                int initialXVelocity = (int) velocityTracker.getXVelocity();
//                int initialYVelocity = (int) velocityTracker.getYVelocity();
//
////                if ((Math.abs(initialXVelocity) + Math.abs(initialYVelocity) > mMinimumVelocity) && getChildCount() > 0) {
////                    fling(-initialXVelocity, -initialYVelocity);
////                }
//
//                if (mVelocityTracker != null) {
//                    mVelocityTracker.recycle();
//                    mVelocityTracker = null;
//                }
                break;
        }

        invalidate();
        return true;
    }

    /**
     * Smooth scroll by a Y delta
     *
     * @param deltaX the number of pixels to scroll by on the X axis
     * @param deltaY the number of pixels to scroll by on the Y axis
     */
    private void doScroll(int deltaX, int deltaY) {
        if (deltaX != 0 || deltaY != 0) {
            scrollBy(deltaX, deltaY);
        }
    }

    /**
     * Like {@link View#scrollBy}, but scroll smoothly instead of immediately.
     *
     * @param dx the number of pixels to scroll by on the X axis
     * @param dy the number of pixels to scroll by on the Y axis
     */
    public final void smoothScrollBy(int dx, int dy) {
        long duration = AnimationUtils.currentAnimationTimeMillis() - mLastScroll;
        if (duration > ANIMATED_SCROLL_GAP) {
            mScroller.startScroll(getScrollX(), getScrollY(), dx, dy);
            awakenScrollBars(mScroller.getDuration());
            invalidate();
        } else {
            if (!mScroller.isFinished()) {
                mScroller.abortAnimation();
            }
            scrollBy(dx, dy);
        }
        mLastScroll = AnimationUtils.currentAnimationTimeMillis();
    }


    /**
     * Convert Drawable to bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }


    /////////////////////////////////////////////
    private class ScaleListener extends
            ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scale = detector.getScaleFactor();
            float widthCellScale = getCellWidth() * scale;
            float heightCellScale = getCellHeight() * scale;
            setCellWidth((int) Math.max(MIN_CELL_WIDTH, Math.min(widthCellScale, MAX_CELL_WIDTH)));
            setCellHeight((int) Math.max(MIN_CELL_HEIGHT, Math.min(heightCellScale, MAX_CELL_HEIGHT)));
            return true;
        }


    }

}
