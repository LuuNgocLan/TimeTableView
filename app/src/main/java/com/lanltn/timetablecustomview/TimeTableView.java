package com.lanltn.timetablecustomview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.widget.Scroller;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import static android.graphics.Region.Op.INTERSECT;
import static android.graphics.Region.Op.REPLACE;

public class TimeTableView extends View {

    private static final int
            FES_EVENT_NORMAL = 0,
            FES_EVENT_FAVORITE = 1,
            FES_EVENT_WATCH = 2,
            RECT_WHITE = 100;

    private static final int ANIMATED_SCROLL_GAP = 250;

    private static final int
            MIN_CELL_WIDTH = 120,
            MAX_CELL_WIDTH = 450,
            MIN_CELL_HEIGHT = 70,
            MAX_CELL_HEIGHT = 180;

    private String[] mTitleHeader = {
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
    private int mHeightHeaderTimeTable;
    private int mWidthHourRuler;
    private int mTextSizeLabel = 20;

    private long mLastScroll;
    private boolean isScalling;

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
    private int mMinimumVelocity;

    //scroll
    private GestureDetectorCompat mGestureDetector;
    private Scroller mScroller;
    private Direction mCurrentScrollDirection = Direction.NONE;

    private VelocityTracker mVelocityTracker;

    private List<FesEvent> mListFesEvent = new ArrayList<>();
    private List<FesEventRectF> mListFesEventRectF = new ArrayList<>();

    public TimeTableView(Context context) {
        this(context, null);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public TimeTableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        initScroll(context);

    }

    private void initScroll(Context context) {
        // Scrolling initialization.
        mGestureDetector = new GestureDetectorCompat(context, new MyGestureListener());
        mScroller = new Scroller(context);
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calculateDimensions();
    }


    private void calculateDimensions() {
        if (mNumColumns < 1 || mNumRows < 1) {
            return;
        }
        mCellWidth = MAX_CELL_WIDTH;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mNumColumns == 0 || mNumRows == 0) {
            return;
        }

        int heightContentTimeTable = mCellHeight * mNumRows;
        int widthContentTimeTable = mCellWidth * mNumColumns;

        canvas.drawColor(Color.BLACK);
        canvas.translate(0, 0);
        canvas.clipRect(0, 0,
                mWidthHourRuler + widthContentTimeTable,
                mHeightHeaderTimeTable + heightContentTimeTable,
                REPLACE);
        canvas.drawColor(Color.BLACK);
        canvas.save(); //Save current state of clip - state #1
        canvas.restore();//restore state #1

        switch (mCurrentScrollDirection) {
            case VERTICAL:
                canvas.clipRect(0, 0,
                        mWidthHourRuler + widthContentTimeTable,
                        mHeightHeaderTimeTable + heightContentTimeTable,
                        REPLACE);
                //Content timetable
                drawRectVerticalDivisionInContentTimeTable(canvas, heightContentTimeTable, mHeightHeaderTimeTable);
                drawLineHorizontalInContentTimeTableWithOpacity(canvas);
                drawTimeRulerLeftTimeTable(canvas);
                drawFesEventCard(canvas);
                drawIndicatorLineWithCurrentTime(canvas);
                canvas.restore();
                canvas.save();
                //pin HeaderTitle and bitmap at top
                canvas.translate(0, 0);
                drawTitleHeaderTimetable(canvas);
                drawIconBitmapOlockAtTopLeftTimeTable(canvas);
                break;
            case HORIZONTAL:
                canvas.clipRect(0, 0,
                        mWidthHourRuler + widthContentTimeTable,
                        mHeightHeaderTimeTable + heightContentTimeTable,
                        Region.Op.REPLACE);
                //Content timetable
                drawRectVerticalDivisionInContentTimeTable(canvas, heightContentTimeTable, mHeightHeaderTimeTable);
                drawLineHorizontalInContentTimeTableWithOpacity(canvas);
                drawTitleHeaderTimetable(canvas);
                drawFesEventCard(canvas);
                drawIndicatorLineWithCurrentTime(canvas);
                canvas.restore();
                canvas.save();
                //pin RulerTime and bitmap at left
                canvas.translate(0, 0);
                drawTimeRulerLeftTimeTable(canvas);
                drawIconBitmapOlockAtTopLeftTimeTable(canvas);
                break;
            case NONE:
                canvas.save();
                canvas.clipRect(0, 0,
                        mWidthHourRuler + widthContentTimeTable,
                        mHeightHeaderTimeTable + heightContentTimeTable,
                        Region.Op.REPLACE);
//                Content timetable
                drawRectVerticalDivisionInContentTimeTable(canvas, heightContentTimeTable, mHeightHeaderTimeTable);
                drawLineHorizontalInContentTimeTableWithOpacity(canvas);
                drawTimeRulerLeftTimeTable(canvas);
                drawTitleHeaderTimetable(canvas);
                drawIconBitmapOlockAtTopLeftTimeTable(canvas);
                drawFesEventCard(canvas);
                drawIndicatorLineWithCurrentTime(canvas);
                canvas.restore();
                canvas.save();
                break;
        }
    }

    private void drawTimeRulerLeftTimeTable(Canvas canvas) {
        int textLabelHourMarginLeft = 20;
        Paint bgPaint = new Paint();
        bgPaint.setColor(Color.BLACK);
        canvas.drawRect(0, mHeightHeaderTimeTable,
                mWidthHourRuler, mHeightHeaderTimeTable + mCellHeight * mNumRows, bgPaint);
        for (int i = 0; i < mNumRows * 6 - 1; i++) {
            blackPaint.setColor(getResources().getColor(R.color.colorLabelHour));
            if (i % 3 == 0) {
                if (i % 2 == 0) {
                    blackPaint.setStrokeWidth(6);
                    canvas.drawLine(
                            mWidthHourRuler - 30,
                            mHeightHeaderTimeTable + i * mCellHeight / 6 + blackPaint.getStrokeWidth() / 2,
                            mWidthHourRuler,
                            mHeightHeaderTimeTable + i * mCellHeight / 6 + blackPaint.getStrokeWidth() / 2,
                            blackPaint);

                    //draw hours label
                    blackPaint.setTextSize(mTextSizeLabel);
                    blackPaint.setStrokeWidth(1);
                    canvas.drawText(
                            i / 6 + ":00",
                            textLabelHourMarginLeft,
                            mHeightHeaderTimeTable + i * mCellHeight / 6 + blackPaint.getTextSize() / 2,
                            blackPaint);

                } else {
                    blackPaint.setStrokeWidth(2);
                    canvas.drawLine(
                            mWidthHourRuler - 25,
                            mHeightHeaderTimeTable + i * mCellHeight / 6,
                            mWidthHourRuler,
                            mHeightHeaderTimeTable + i * mCellHeight / 6,
                            blackPaint);
                }
            } else {
                blackPaint.setStrokeWidth(2);
                canvas.drawLine(
                        mWidthHourRuler - 15,
                        mHeightHeaderTimeTable + i * mCellHeight / 6,
                        mWidthHourRuler,
                        mHeightHeaderTimeTable + i * mCellHeight / 6,
                        blackPaint);
            }
        }
    }

    private void drawLineHorizontalInContentTimeTableWithOpacity(Canvas canvas) {
        for (int i = 0; i < mNumRows * 6 - 1; i++) {
            blackPaint.setColor(getResources().getColor(R.color.colorLabelHour));
            if (i % 3 == 0) {
                for (int col = 0; col < mNumColumns; col++) {
                    if (col % 2 == 0) {
                        blackPaint.setStrokeWidth(1.5f);
                        blackPaint.setAlpha(100);
                        canvas.drawLine(
                                mWidthHourRuler + col * mCellWidth,
                                mHeightHeaderTimeTable + i * mCellHeight / 6,
                                mWidthHourRuler + (col + 1) * mCellWidth,
                                mHeightHeaderTimeTable + i * mCellHeight / 6, blackPaint);
                    } else {
                        blackPaint.setStrokeWidth(1.5f);
                        blackPaint.setAlpha(75);
                        canvas.drawLine(
                                mWidthHourRuler + col * mCellWidth,
                                mHeightHeaderTimeTable + i * mCellHeight / 6,
                                mWidthHourRuler + (col + 1) * mCellWidth,
                                mHeightHeaderTimeTable + i * mCellHeight / 6, blackPaint);
                    }
                }
            }
        }
    }

    private void drawTitleHeaderTimetable(Canvas canvas) {
        for (int i = 0; i < mNumColumns; i++) {
            if (i % 2 == 0) {
                mPaint.setColor(getResources().getColor(R.color.colorCol));
            } else {
                mPaint.setColor(getResources().getColor(R.color.colorColDark));
            }

            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            int start_X = getmWidthHourRuler() + i * mCellWidth;
            canvas.drawRect(
                    start_X,
                    0,
                    start_X + mCellWidth,
                    getmHeightHeaderTimeTable(),
                    mPaint);
            blackPaint.setColor(getResources().getColor(R.color.colorTitle));
            blackPaint.setTextSize(mTextSizeLabel);
            blackPaint.setStrokeWidth(1);
            String[] words = mTitleHeader[i].split(" ");
            for (int k = 0; k < words.length; k++) {
                canvas.drawText(words[k], mWidthHourRuler + i * mCellWidth + 10, 30 + k * 25, blackPaint);
            }
        }
    }

    private void drawIconBitmapOlockAtTopLeftTimeTable(Canvas canvas) {
        canvas.clipRect(0, 0, mWidthHourRuler, mHeightHeaderTimeTable, INTERSECT);
        canvas.drawColor(Color.BLACK);
        Drawable drawable = getResources().getDrawable(R.drawable.ic_access_time);
        Bitmap bitmap = drawableToBitmap(drawable);
        int widthBitmap = bitmap.getWidth();
        int heightBitmap = bitmap.getHeight();
        canvas.drawBitmap(bitmap, (mWidthHourRuler - widthBitmap) / 2,
                (mHeightHeaderTimeTable - heightBitmap) / 2, mPaint);
    }

    private void drawRectVerticalDivisionInContentTimeTable(Canvas canvas, int heightTimeTable, int heightHeaderTitle) {
        for (int i = 0; i < mNumColumns; i++) {
            if (i % 2 == 0) {
                mPaint.setColor(getResources().getColor(R.color.colorCol));
            } else {
                mPaint.setColor(getResources().getColor(R.color.colorColDark));
            }

            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            int start_X = getmWidthHourRuler() + i * mCellWidth;
            canvas.drawRect(
                    start_X,
                    heightHeaderTitle,
                    start_X + mCellWidth,
                    heightTimeTable,
                    mPaint);
        }

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
                left = getmWidthHourRuler() + fesEvent.getIdCol() * getmCellWidth();
                timeFesStart = convertTimeStringToHour(fesEvent.getmStartFesEvent());
                timeFesEnd = convertTimeStringToHour(fesEvent.getmEndFesEvent());
                top = getmHeightHeaderTimeTable() + timeFesStart * getmCellHeight();
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
                getmWidthHourRuler() - 80,
                getmHeightHeaderTimeTable() + time * mCellHeight + blackPaint.getStrokeWidth() / 2, getmCellWidth() * 6 + getmWidthHourRuler(),
                getmHeightHeaderTimeTable() + time * mCellHeight + blackPaint.getStrokeWidth() / 2, blackPaint);

        //Rect bound of label
        blackPaint.setTextSize(mTextSizeLabel);
        blackPaint.setStrokeWidth(1);

        int width_bound = (int) blackPaint.getTextSize() + 2;
        blackPaint = setStyleRect(RECT_WHITE);
        canvas.drawRoundRect(
                10,
                getmHeightHeaderTimeTable() + time * mCellHeight - width_bound / 2,
                85,
                width_bound + getmHeightHeaderTimeTable() + time * mCellHeight,
                5,
                5,
                blackPaint);

        blackPaint.setTextSize(mTextSizeLabel);
        blackPaint.setStrokeWidth(1);
        blackPaint.setColor(getResources().getColor(R.color.colorAccent));
        canvas.drawText(
                labelHour,
                20,
                getmHeightHeaderTimeTable() + time * mCellHeight + blackPaint.getTextSize() / 2,
                blackPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        mScaleDetector.onTouchEvent(event);
        mGestureDetector.onTouchEvent(event);

        return true;
    }

    /**
     * Fling the scroll view
     *
     * @param velocityY The initial velocity in the Y direction. Positive
     *                  numbers mean that the finger/curor is moving down the screen,
     *                  which means we want to scroll towards the top.
     */
    public void fling(int velocityX, int velocityY) {
        int height = getHeight() - getPaddingBottom() - getPaddingTop();
        int bottom = mCellHeight;
        int width = getWidth() - getPaddingRight() - getPaddingLeft();
        int right = mCellWidth;

        mScroller.fling(getScrollX(), getScrollY(), velocityX, velocityY, 0, right - width, 0, bottom - height);

        awakenScrollBars(mScroller.getDuration());
        invalidate();
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


    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
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

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gesture";

        @Override
        public boolean onDown(MotionEvent e) {
            detectRectFOfEventAtTouch(e.getX(), e.getY());

            mScroller.forceFinished(true);

            if (!mScroller.isFinished()) {
                mScroller.abortAnimation();
            }
            invalidate();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            // Allow scrolling only in one direction.
            if (Math.abs(distanceX) > Math.abs(distanceY)) {
                mCurrentScrollDirection = Direction.HORIZONTAL;
            } else {
                mCurrentScrollDirection = Direction.VERTICAL;
            }
            // Scroll to follow the motion event
            int deltaX = (int) (distanceX);
            int deltaY = (int) (distanceY);

            // Calculate the new origin after scroll.
            switch (mCurrentScrollDirection) {
                case HORIZONTAL:

                    if (deltaX < 0) {
                        if (getScrollX() < 0) {
                            deltaX = 0;
                        }
                    } else if (deltaX > 0) {
                        final int rightEdge = getWidth() - getPaddingRight();
                        final int availableToScroll = getmWidthHourRuler() + getmNumColumns() * getmCellWidth() - getScrollX() - rightEdge;
                        if (availableToScroll > 0) {
                            deltaX = Math.min(availableToScroll, deltaX);
                        } else {
                            deltaX = 0;
                        }
                    }
                    doScroll(deltaX, 0);
                    ViewCompat.postInvalidateOnAnimation(TimeTableView.this);
                    break;
                case VERTICAL:

                    if (deltaY < 0) {
                        if (getScrollY() < 0) {
                            deltaY = 0;
                        }
                    } else if (deltaY > 0) {
                        final int bottomEdge = getHeight() - getPaddingBottom();
                        final int availableToScroll = getmHeightHeaderTimeTable() + getmNumRows() * getmCellHeight() - getScrollY() - bottomEdge;
                        if (availableToScroll > 0) {
                            deltaY = Math.min(availableToScroll, deltaY);
                        } else {
                            deltaY = 0;
                        }
                    }
                    ViewCompat.postInvalidateOnAnimation(TimeTableView.this);
                    doScroll(0, deltaY);
                    break;
            }
            invalidate();
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return super.onDoubleTapEvent(e);
        }
    }

    private enum Direction {
        NONE, HORIZONTAL, VERTICAL
    }

    //////////////GETTER AND SETTER
    public void setmNumColumns(int mNumColumns) {
        this.mNumColumns = mNumColumns;
    }

    public int getmNumColumns() {
        return mNumColumns;
    }

    public void setmNumRows(int mNumRows) {
        this.mNumRows = mNumRows;
    }

    public int getmNumRows() {
        return mNumRows;
    }

    public int getmCellWidth() {
        return mCellWidth;
    }

    public void setmCellWidth(int mCellWidth) {
        this.mCellWidth = mCellWidth;
        calculateDimensions();
    }

    public int getmCellHeight() {
        return mCellHeight;
    }

    public void setmCellHeight(int mCellHeight) {
        this.mCellHeight = mCellHeight;
        calculateDimensions();
    }

    public int getmHeightHeaderTimeTable() {
        return mHeightHeaderTimeTable;
    }

    public void setmHeightHeaderTimeTable(int mHeightHeaderTimeTable) {
        this.mHeightHeaderTimeTable = mHeightHeaderTimeTable;
    }

    public int getmWidthHourRuler() {
        return mWidthHourRuler;
    }

    public void setmWidthHourRuler(int mWidthHourRuler) {
        this.mWidthHourRuler = mWidthHourRuler;
    }

    public String[] getmTitleHeader() {
        return mTitleHeader;
    }

    public void setmTitleHeader(String[] mTitleHeader) {
        this.mTitleHeader = mTitleHeader;
    }

    public List<FesEvent> getmListFesEvent() {
        return mListFesEvent;
    }

    public void setmListFesEvent(List<FesEvent> mListFesEvent) {
        this.mListFesEvent = mListFesEvent;
    }

    public void setFocusViewToTime(String time) {
        float timeFocus = convertTimeStringToHour(time);
        scrollTo(0, (int) (getmHeightHeaderTimeTable() + timeFocus * mCellHeight / 2));
    }

    public void setFocusToEvent(FesEvent fesEvent) {
    }
}
