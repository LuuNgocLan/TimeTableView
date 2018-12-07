package com.lanltn.timetablecustomview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Scroller;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class TimeTableView extends View {

    private static final int
            FES_EVENT_NORMAL = 0,
            FES_EVENT_FAVORITE = 1,
            FES_EVENT_WATCH = 2,
            RECT_WHITE = 100;

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

    private int mNumColumns;
    private int mNumRows;
    private int mCellWidth;
    private int mCellHeight;
    private int heightTitle;
    private int widthLabelHours;
    private int mTextSizeLabel = 20;

    private long mLastScroll;

    private float offestScrollX;
    private float offsetScrollY;
    private float initialTouchX;
    private float initialTouchY;


    private Paint mPaintLineIndicatorCurrentTime = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint mPaintBitmapOClockIcon = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint mPaintRectVertivalDark = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mPaintRectVerticallight = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mPaintRectEvent = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mPaintRectCurrentTime = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint mPaintLabelHourText = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mPaintLabelHourCurrentText = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mPaintTitleHeaderText = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mPaintNameEventText = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mPaintTimeEventText = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint blackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mGestureDetector;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    private List<FesEvent> mListFesEvent = new ArrayList<>();
    private List<FesEventRectF> mListFesEventRectF = new ArrayList<>();

    public TimeTableView(Context context) {
        this(context, null);
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
    }

    public TimeTableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        mGestureDetector = new GestureDetector(getContext(), new MyGestureListener());
        mScroller = new Scroller(getContext());
    }


    public void setmNumColumns(int mNumColumns) {
        this.mNumColumns = mNumColumns;
        calculateDimensions();
    }

    public int getmNumColumns() {
        return mNumColumns;
    }

    public void setmNumRows(int mNumRows) {
        this.mNumRows = mNumRows;
        calculateDimensions();
    }

    public int getmNumRows() {
        return mNumRows;
    }

    public int getmCellWidth() {
        return mCellWidth;
    }

    public void setmCellWidth(int mCellWidth) {
        this.mCellWidth = mCellWidth;
    }

    public int getmCellHeight() {
        return mCellHeight;
    }

    public void setmCellHeight(int mCellHeight) {
        this.mCellHeight = mCellHeight;
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
        scrollTo(0, (int) (getHeightTitle() + timeFocus * mCellHeight / 2));
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
        if (mNumColumns < 1 || mNumRows < 1) {
            return;
        }

        mCellWidth = (getWidth() - widthLabelHours) / mNumColumns;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mNumColumns == 0 || mNumRows == 0) {
            return;
        }

        int height = getmCellHeight() * mNumRows;
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
        for (int i = 0; i < mNumColumns; i++) {
            if (i % 2 == 0) {
                mPaint.setColor(getResources().getColor(R.color.colorCol));
            } else {
                mPaint.setColor(getResources().getColor(R.color.colorColDark));
            }

            mPaint.setStrokeWidth(mCellWidth);
            int start_X_line = getWidthLabelHours() + i * mCellWidth;
            canvas.drawLine(
                    start_X_line + mCellWidth / 2,
                    0,
                    start_X_line + mCellWidth / 2,
                    height + getHeightTitle(),
                    mPaint);

            //draw text title
            blackPaint.setColor(getResources().getColor(R.color.colorTitle));
            blackPaint.setTextSize(mTextSizeLabel);
            blackPaint.setStrokeWidth(1);
            String[] words = titles[i].split(" ");
            for (int k = 0; k < words.length; k++) {
                canvas.drawText(words[k], leftPaddingTimeTable + i * mCellWidth + 10, 30 + k * 25, blackPaint);
            }
        }

        //draw line horizontal
        int textLabelHourMarginLeft = 20;
        for (int i = 0; i < mNumRows * 6 - 1; i++) {
            blackPaint.setColor(getResources().getColor(R.color.colorLabelHour));
            if (i % 3 == 0) {
                if (i % 2 == 0) {
                    blackPaint.setStrokeWidth(6);
                    canvas.drawLine(
                            leftPaddingTimeTable - 30,
                            topPaddingTimeTable + i * mCellHeight / 6 + blackPaint.getStrokeWidth() / 2,
                            leftPaddingTimeTable,
                            topPaddingTimeTable + i * mCellHeight / 6 + blackPaint.getStrokeWidth() / 2,
                            blackPaint);

                    //draw hours label
                    blackPaint.setTextSize(mTextSizeLabel);
                    blackPaint.setStrokeWidth(1);
                    canvas.drawText(
                            i / 6 + ":00",
                            textLabelHourMarginLeft,
                            topPaddingTimeTable + i * mCellHeight / 6 + blackPaint.getTextSize() / 2,
                            blackPaint);

                } else {
                    blackPaint.setStrokeWidth(2);
                    canvas.drawLine(
                            leftPaddingTimeTable - 25,
                            topPaddingTimeTable + i * mCellHeight / 6,
                            leftPaddingTimeTable,
                            topPaddingTimeTable + i * mCellHeight / 6,
                            blackPaint);
                }
            } else {
                blackPaint.setStrokeWidth(2);
                canvas.drawLine(
                        leftPaddingTimeTable - 15,
                        topPaddingTimeTable + i * mCellHeight / 6,
                        leftPaddingTimeTable,
                        topPaddingTimeTable + i * mCellHeight / 6,
                        blackPaint);
            }

        }


        for (int i = 0; i < mNumRows * 6 - 1; i++) {
            blackPaint.setColor(getResources().getColor(R.color.colorLabelHour));
            if (i % 3 == 0) {
                for (int col = 0; col < mNumColumns; col++) {
                    if (col % 2 == 0) {
                        blackPaint.setStrokeWidth(1.5f);
                        blackPaint.setAlpha(100);
                        canvas.drawLine(
                                leftPaddingTimeTable + col * mCellWidth,
                                topPaddingTimeTable + i * mCellHeight / 6,
                                leftPaddingTimeTable + (col + 1) * mCellWidth,
                                topPaddingTimeTable + i * mCellHeight / 6, blackPaint);
                    } else {
                        blackPaint.setStrokeWidth(1.5f);
                        blackPaint.setAlpha(75);
                        canvas.drawLine(
                                leftPaddingTimeTable + col * mCellWidth,
                                topPaddingTimeTable + i * mCellHeight / 6,
                                leftPaddingTimeTable + (col + 1) * mCellWidth,
                                topPaddingTimeTable + i * mCellHeight / 6, blackPaint);
                    }
                }
            }
        }

        drawFesEventCard(canvas);

        drawIndicatorLineWithCurrentTime(canvas);

    }

    private void drawFesEventCard(Canvas canvas) {
        mListFesEventRectF.clear();
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
                left = getWidthLabelHours() + fesEvent.getIdCol() * getmCellWidth();
                timeFesStart = convertTimeStringToHour(fesEvent.getmStartFesEvent());
                timeFesEnd = convertTimeStringToHour(fesEvent.getmEndFesEvent());
                top = getHeightTitle() + timeFesStart * getmCellHeight();
                height_rect = (timeFesEnd - timeFesStart) * getmCellHeight();
                width_rect = mCellWidth;

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

                //add rect fes to the list mFesEventOfRectF
                mListFesEventRectF.add(new FesEventRectF(rectF, fesEvent));
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
                getHeightTitle() + time * mCellHeight + blackPaint.getStrokeWidth() / 2, getmCellWidth() * 6 + getWidthLabelHours(),
                getHeightTitle() + time * mCellHeight + blackPaint.getStrokeWidth() / 2, blackPaint);

        //Rect bound of label
        blackPaint.setTextSize(mTextSizeLabel);
        blackPaint.setStrokeWidth(1);

        int width_bound = (int) blackPaint.getTextSize() + 2;
        blackPaint = setStyleRect(RECT_WHITE);
        canvas.drawRoundRect(
                10,
                getHeightTitle() + time * mCellHeight - width_bound / 2,
                85,
                width_bound + getHeightTitle() + time * mCellHeight,
                5,
                5,
                blackPaint);

        blackPaint.setTextSize(mTextSizeLabel);
        blackPaint.setStrokeWidth(1);
        blackPaint.setColor(getResources().getColor(R.color.colorAccent));
        canvas.drawText(
                labelHour,
                20,
                getHeightTitle() + time * mCellHeight + blackPaint.getTextSize() / 2,
                blackPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mScaleDetector.onTouchEvent(event);
        mGestureDetector.onTouchEvent(event);

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
                    final int availableToScroll = getWidthLabelHours() + getmNumColumns() * getmCellWidth() - getScrollX() - rightEdge;
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
                    final int availableToScroll = getHeightTitle() + getmNumRows() * getmCellHeight() - getScrollY() - bottomEdge;
                    if (availableToScroll > 0) {
                        deltaY = Math.min(availableToScroll, deltaY);
                    } else {
                        deltaY = 0;
                    }
                }

                doScroll(deltaX, deltaY);
                invalidate();
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

    private void detectRectFOfEventAtTouch(float posX, float posY) {
        Random rand = new Random();
        int timeCount = rand.nextInt(3);
        for (FesEventRectF fesEventRectF : mListFesEventRectF) {
            RectF rectF = fesEventRectF.getmRectFFesEvent();
            if (rectF.contains(posX + getScrollX(), posY + getScrollY())) {
                Toast.makeText(getContext(), timeCount + "Touch at " + fesEventRectF.getmFesEvent().getmNameFesEvent(), Toast.LENGTH_SHORT).show();
            }
        }
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
            float widthCellScale = getmCellWidth() * scale;
            float heightCellScale = getmCellHeight() * scale;
            setmCellWidth((int) Math.max(MIN_CELL_WIDTH, Math.min(widthCellScale, MAX_CELL_WIDTH)));
            setmCellHeight((int) Math.max(MIN_CELL_HEIGHT, Math.min(heightCellScale, MAX_CELL_HEIGHT)));
            return true;
        }

    }

    ////////////////
    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gesture";

        @Override
        public boolean onDown(MotionEvent e) {
            detectRectFOfEventAtTouch(e.getX(), e.getY());
            Log.d(DEBUG_TAG, e.getX() + "");
            return true;
        }

    }

}
