package com.lanltn.timetablecustomview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.OverScroller;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.graphics.Region.Op.INTERSECT;
import static android.graphics.Region.Op.REPLACE;

public class TimetableContainer extends View {

    public static final int PLUS_HOUR_FOR_DAY = 24;
    private static final int MAXIMUM_STAGE_MT10 = 6;
    private static final int MAXIMUM_STAGE_MT20 = 20;

    public static int MAXIMUM_HOUR_IN_DAY = 33;

    public static final int CELL_HEIGHT = 60;
    private PointF mCurrentOrigin = new PointF(0, 0);
    private float mWidthEventContainer;
    private float mWidthEachEvent;
    private float mHeightEachEvent;
    private float mNormalDistance;

    private int mMaximumStage;
    private final int TOTAL_DISTANCE_EACH_NORNAL_TIME_STONE = 6; // 1 hour division to 6

    //scroll
    private GestureDetectorCompat mGestureDetector;
    private OverScroller mScroller;
    private Scroller mStickyScroller;
    private Direction mCurrentScrollDirection = Direction.NONE;
    private Direction mCurrentFlingDirection = Direction.NONE;

    private List<Event> mEvents = new ArrayList<>();
    private List<EventRectF> mEventRects = new ArrayList<>();
    private String[] mTitleHeader = {
            "RED MARQUEE",
            "GREEN STAGE",
            "WHITE STAGE",
            "GYPSY AVALON",
            "FIELD OF HEAVEN",
            "ORANGE CAFE"};
    private float mTextTimeRuler = 0;
    private float mTextTitleStage;
    private float mTextEventTitle;
    private float mTextTimeEvent;

    public void setmEvents(List<Event> mEvents) {
        this.mEvents = mEvents;
    }

    ///scale
    private ScaleGestureDetector mScaleDetector;
    private float mNewWidthEachEvent;
    private float mNewHeightEachEvent;
    private float mWidthHeader;
    private float mHeightHeader;
    private float mEffectiveMinHourHeight;
    private float mMaxHourHeight;
    private float mEffectiveMinHourWidth;
    private float mMaxHourWidth;

    //pain to draw line
    private Paint mPaintLineNoStroke = new Paint();
    private Paint mPaintLineStroke = new Paint();
    private Paint mPaintLineLight = new Paint();

    //drawText
    private Paint mPaintTitleText = new Paint();
    private Paint mPaintEventTitleText = new Paint();
    private Paint mPaintTimeText = new Paint();
    private Paint mPaintTextFavorite = new Paint();

    //draw rect
    private Paint mPaintEventNormal = new Paint();
    private Paint mPaintEventFavorite = new Paint();
    private Paint mPaintEventWatch = new Paint();
    private Paint mPaintCurrentTime = new Paint();
    //over lay
    private Paint mPaintOverLay = new Paint();

    Calendar rightNow = Calendar.getInstance(Locale.JAPAN);
    int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
    int currentMin = rightNow.get(Calendar.MINUTE);
    private boolean mIsToday;
    private int mPlusHour;
    //margin
    private int mDefaultCornerRadius;
    private int mMaxMargin;
    private int mMinMargin;

    //listener click
    private IOnTimeTableClickEvent mIOnClickEventItem;

    //update realtime
    private Canvas mCanvas;
    private boolean mIsPressDown;
    private boolean mIsScale;

    //rectF obj press or up
    RectF mRectFPress = new RectF();
    private float mStoreOffet = -1.0f;
    private boolean isMinOven = false;
    private boolean isMinOdd = false;
    private Context mContext;
    private TimetableContainerType mTimetableContainerType;

    //prevent double click
    private static final long DOUBLE_CLICK_TIME_DELTA = 600;//milliseconds
    private long lastClickTime = 0;
    private int mCountEventPress;
    private boolean hasShowDialogArtist;
    private int width = 1200;
    private boolean isToday = true;
    private int plusHour = 6;

    public TimetableContainer(Context context) {
        super(context);

    }

    public TimetableContainer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
//        mTimetableContainerType = timetableContainerType;

//        if (mTimetableContainerType == TimetableContainerType.TIME_TABLE_MY_FES) {
//            mMaximumStage = stages.size();
//        } else {
//            mMaximumStage = MAXIMUM_STAGE_MT10;
//        }
        mMaximumStage = 6;
        mWidthHeader = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                60,
                context.getResources().getDisplayMetrics());
        mHeightHeader = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                50,
                context.getResources().getDisplayMetrics());
        mDefaultCornerRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                3,
                context.getResources().getDisplayMetrics());


        mWidthEachEvent = ((width - mWidthHeader) / 6);

        mWidthEventContainer = width;
        mEffectiveMinHourHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                CELL_HEIGHT,
                context.getResources().getDisplayMetrics());
        if (6 > mMaximumStage) {
            mEffectiveMinHourWidth = ((width - mWidthHeader) / mMaximumStage);   // chieu rong lon nhat cua event
        } else {
            mEffectiveMinHourWidth = ((width - mWidthHeader) / 6);
        }
        mHeightEachEvent = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                CELL_HEIGHT,
                context.getResources().getDisplayMetrics());

        mMaxHourWidth = mWidthEachEvent * 4;
        mMaxHourHeight = mHeightEachEvent * 4;
        initScroll(context);
        //text size
        mTextTimeRuler = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                10,
                context.getResources().getDisplayMetrics());
        mTextTitleStage = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                7,
                context.getResources().getDisplayMetrics());
        mTextEventTitle = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                9,
                context.getResources().getDisplayMetrics());
        setTextTimeEvent(3);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        //pain
        setPaintLineNoStoke();
        setPaintLineStroke();
        setPaintLineLight();
        //set paint text
        setPaintTitleText();
        setPaintEventTitleText();
        setPaintTimeText();
        setPaintEventFavoriteText();
        //set paint rect
        setPaintEventNormal();
        setPaintEventFavorite();
        setPaintEventWatch();
        setPaintCurrentTime();
        // set paint OverLay
        setPaintOverLay();
//        mIOnClickEventItem = onClickEvent;
        mIsToday = isToday;
        mPlusHour = plusHour;
        currentHour += mPlusHour;
        mMaxMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                10,
                context.getResources().getDisplayMetrics());
        mMinMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                5,
                context.getResources().getDisplayMetrics());

        if (!isToday) {
            mIOnClickEventItem.updateCurrentTime();
        }
    }


    private void setTextTimeEvent(int size) {
        mTextTimeEvent = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                size,
                mContext.getResources().getDisplayMetrics());
    }

    private void setPaintOverLay() {
        mPaintOverLay.setStyle(Paint.Style.FILL);
        mPaintOverLay.setColor(getResources().getColor(R.color.bg_overlay_color));
    }

    private void setPaintCurrentTime() {
        mPaintCurrentTime.setStyle(Paint.Style.FILL);
        mPaintCurrentTime.setStrokeWidth(4);
        mPaintCurrentTime.setColor(getResources().getColor(android.R.color.white));
    }

    private void setPaintEventWatch() {
        mPaintEventWatch.setStyle(Paint.Style.FILL);
        mPaintEventWatch.setColor(getResources().getColor(R.color.colorAccent));
    }

    private void setPaintEventFavorite() {
        mPaintEventFavorite.setStyle(Paint.Style.STROKE);
        mPaintEventFavorite.setColor(getResources().getColor(R.color.colorAccent));
        mPaintEventFavorite.setStrokeWidth(2);
    }

    private void setPaintEventNormal() {
        mPaintEventNormal.setStyle(Paint.Style.STROKE);
        mPaintEventNormal.setColor(Color.WHITE);
        mPaintEventNormal.setStrokeWidth(2);
    }

    private void setPaintTitleText() {
        mPaintTitleText.setColor(Color.WHITE);
        mPaintTitleText.setTextSize(mTextTitleStage);
        mPaintTitleText.setTextAlign(Paint.Align.CENTER);
        mPaintTitleText.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
    }

    private void setPaintTimeText() {
        mPaintTimeText.setColor(getResources().getColor(R.color.colorLabelHour));
        mPaintTimeText.setTextSize(mTextTimeEvent);
        mPaintTimeText.setTextAlign(Paint.Align.LEFT);
        mPaintTimeText.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
    }

    private void setPaintEventTitleText() {
        mPaintEventTitleText.setColor(Color.WHITE);
        mPaintEventTitleText.setTextSize(mTextEventTitle);
        mPaintEventTitleText.setTextAlign(Paint.Align.CENTER);
        mPaintEventTitleText.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
    }

    private void setPaintEventFavoriteText() {
        mPaintTextFavorite.setColor(getResources().getColor(R.color.colorAccent));
        mPaintTextFavorite.setTextAlign(Paint.Align.CENTER);
        mPaintTextFavorite.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
    }

    private void setPaintLineStroke() {
        mPaintLineStroke.setColor(getResources().getColor(R.color.colorTitle));
        mPaintLineStroke.setStrokeWidth(4);
    }

    private void setPaintLineNoStoke() {
        mPaintLineNoStroke.setColor(getResources().getColor(R.color.colorTitle));
        mPaintLineNoStroke.setStrokeWidth(1);
    }

    private void setPaintLineLight() {
        mPaintLineLight.setColor(getResources().getColor(android.R.color.white));
        mPaintLineLight.setStrokeWidth(1);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mNewHeightEachEvent = Math.round(mHeightEachEvent * detector.getScaleFactor());
            mNewWidthEachEvent = Math.round(mWidthEachEvent * detector.getScaleFactor());
            mIsScale = true;
            invalidate();
            return true;
        }
    }

    private void initScroll(Context context) {
        // Scrolling initialization.
        mGestureDetector = new GestureDetectorCompat(context, mGestureListener);
        mScroller = new OverScroller(context);
        mStickyScroller = new Scroller(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCanvas = canvas;
        drawIconBitmapOlockAtTopLeftTimeTable(canvas);
        drawHeaderRowAndEvents(canvas);
        drawTime(canvas);
        if (mIsToday) {
            drawCurrentTime(mCanvas);
        }
    }

    private void drawHeaderRowAndEvents(Canvas canvas) {
        Paint paintEvenBg = new Paint();
        paintEvenBg.setColor(getResources().getColor(R.color.colorCol));
        Paint paintOddBg = new Paint();
        paintOddBg.setColor(getResources().getColor(R.color.colorColDark));
        // tinh toan lai kich thuoc khi scale qua lon hoac qua nho
        if (mNewHeightEachEvent > 0) {
            if (mNewHeightEachEvent < mEffectiveMinHourHeight) {
                mNewHeightEachEvent = mEffectiveMinHourHeight;
            } else if (mNewHeightEachEvent > mMaxHourHeight)
                mNewHeightEachEvent = mMaxHourHeight;
            mCurrentOrigin.y = (mCurrentOrigin.y / mHeightEachEvent) * mNewHeightEachEvent;
            mHeightEachEvent = mNewHeightEachEvent;
            mNewHeightEachEvent = -1;
        }
        if (mNewWidthEachEvent > 0) {
            if (mNewWidthEachEvent < mEffectiveMinHourWidth) {
                mNewWidthEachEvent = mEffectiveMinHourWidth;
            } else if (mNewWidthEachEvent > mMaxHourWidth)
                mNewWidthEachEvent = mMaxHourWidth;

            mCurrentOrigin.x = (mCurrentOrigin.x / mWidthEachEvent) * mNewWidthEachEvent;
            mWidthEachEvent = mNewWidthEachEvent;
            mNewWidthEachEvent = -1;
        }

        //tinh do chia nho nhat
        mNormalDistance = mHeightEachEvent / TOTAL_DISTANCE_EACH_NORNAL_TIME_STONE;
        //Tinh toan tuong doi giua kich thuoc event card va kich thuoc chu
        if (mWidthEachEvent < mMaxHourWidth / 2 || mHeightEachEvent < mMaxHourHeight / 2) {
            setTextTimeEvent(3); //set text luc event card normal
            mPaintTimeText.setTextSize(mTextTimeEvent);
        } else if (mWidthEachEvent >= mMaxHourWidth / 2 || mHeightEachEvent >= mMaxHourHeight / 2) {
            setTextTimeEvent(9);//set text lucs event card large
            mPaintTimeText.setTextSize(mTextTimeEvent);
        }
        /**
         *  mCurrentOrigin  chua biet dung de lam gi??
         *4:52: mCurrentOrigin coi nhu vi tri cuoi cung scroll toi
         */

        // If the new mCurrentOrigin.y is invalid, make it valid.
        if (mCurrentOrigin.y < getHeight() - mHeightEachEvent * MAXIMUM_HOUR_IN_DAY - mHeightHeader - mMinMargin)
            mCurrentOrigin.y = getHeight() - mHeightEachEvent * MAXIMUM_HOUR_IN_DAY - mHeightHeader - mMinMargin;

        // scrolling vertically.
        if (mCurrentOrigin.y > 0) {
            mCurrentOrigin.y = 0;
        }

        if (mCurrentOrigin.x < mWidthEventContainer - mWidthEachEvent * 6 - mWidthHeader)
            mCurrentOrigin.x = mWidthEventContainer - mWidthEachEvent * 6 - mWidthHeader;
        //scrolling horizontal
        if (mCurrentOrigin.x > 0) {
            mCurrentOrigin.x = 0;
        }
        //////////////////////////////////////////////////////////
        /**
         * Rect contain content TimeTable
         */
        canvas.clipRect(mWidthHeader, mHeightHeader, mWidthEventContainer, getHeight(), Region.Op.REPLACE);
        //startY  = chieu cao header + vi cuoi cung.y
        float startY = mHeightHeader + mCurrentOrigin.y;
        //duyet tat ca cac stage?? chua biet la gi?
        /**
         * stage la list cac title header, co bao nhieu duyet bay nhieu
         */
        for (int i = 0; i < 6; i++) {
            //xac dinh vi tri de ve cac background tai thoi diem hien tai
            int dx = (int) (mCurrentOrigin.x + mWidthEachEvent * i + mWidthHeader);
            // ve cac hinh chu nhat voi mau dam nhat xen  ke nhau
            if (i % 2 == 0) {
                //vi tri chan
                canvas.drawRect(dx, startY, dx + mWidthEachEvent, getHeight(), paintEvenBg);
            } else {
                //vi tri le
                canvas.drawRect(dx, startY, dx + mWidthEachEvent, getHeight(), paintOddBg);
            }
        }

        //save Rect event for action click
        mEventRects.clear();
        for (int i = 0; i < 6; i++) {
            //check cot co su kien
//            if (mStages.get(i).getEvents() != null && mStages.get(i).getEvents().size() != 0) {
//                //duyet tat ca cac su kien trong cot do
//                for (int j = 0; j < mStages.get(i).getEvents().size(); j++) {
//                    //add vao EventRect list su kien va khung dinh vi-default null
//                    mEventRects.add(new EventRect(null, mStages.get(i).getEvents().get(j)));
//                }
//            }
        }

        //duyet moc thoi gian tu 0->33
        for (int hourNumber = 0; hourNumber < MAXIMUM_HOUR_IN_DAY; hourNumber++) {
            //vi tri top hien tai
            float top = mCurrentOrigin.y + mHeightEachEvent * hourNumber + mMinMargin + mHeightHeader;
            int dx = (int) (mCurrentOrigin.x + mWidthHeader);
            if (top < getHeight()) {
                //ve duong line chinh 0:00
                canvas.drawLine(dx, (int) top, mWidthEventContainer, (int) top, mPaintLineLight);
                //ve duong line phu 0:30
                canvas.drawLine(dx, (int) top + mHeightEachEvent / 2, mWidthEventContainer,
                        (int) top + mHeightEachEvent / 2, mPaintLineLight);
            }
        }
        //
        for (int hourNumber = 0; hourNumber < MAXIMUM_HOUR_IN_DAY; hourNumber++) {
            float top = mCurrentOrigin.y + mHeightEachEvent * hourNumber + mMinMargin + mHeightHeader;
            int dx = (int) (mCurrentOrigin.x + mWidthHeader);
            if (top < getHeight()) {
                drawEvent(canvas, top, hourNumber, dx);
                if (mIsToday) {
                    // check de ve duong chi thoi gian white
                    if (currentHour == hourNumber) {
                        float dy = top + ((currentMin * mNormalDistance) / 10);
                        canvas.drawLine(mWidthHeader, dy, mWidthEventContainer, dy, mPaintCurrentTime);
                    }
                }
            }
        }
        //doan nay de xet hieu ung luc press xuong rect bang cach Overlay mau sac
        if (mIsPressDown) {
            if (mCountEventPress < 10) {
                mCountEventPress++;
                mPaintOverLay.setColor(getResources().getColor(R.color.bg_overlay_color));
                canvas.drawRoundRect(mRectFPress, mDefaultCornerRadius, mDefaultCornerRadius, mPaintOverLay);
            }

        } else {
            // neu k nhan thi ve event card tai vi tri da xac dinh
            mPaintOverLay.setColor(getResources().getColor(android.R.color.transparent));
            canvas.drawRoundRect(mRectFPress, mDefaultCornerRadius, mDefaultCornerRadius, mPaintOverLay);
        }

        /**
         * Draw First row title header TimeTable
         */

        Paint bgPaintHeader = new Paint();
        bgPaintHeader.setColor(Color.BLACK);
        canvas.clipRect(mWidthHeader, 0, mWidthEventContainer, mHeightHeader, Region.Op.REPLACE);
        canvas.drawRect(mWidthHeader, 0, mWidthEventContainer, mHeightHeader, bgPaintHeader);

        Rect bounds = new Rect();
        float textWidth = mWidthEachEvent - 32;
//        if (mTimetableContainerType == TimetableContainerType.TIME_TABLE_ALL_STAGE) {
        for (int i = 0; i < 6; i++) {
            int dx = (int) (mCurrentOrigin.x + mWidthEachEvent * i + mWidthHeader);
            if (i % 2 == 0) {
                canvas.drawRect(dx, 0, dx + mWidthEachEvent, mHeightHeader, paintEvenBg);
            } else {
                canvas.drawRect(dx, 0, dx + mWidthEachEvent, mHeightHeader, paintOddBg);
            }
            String value = getTextForStageBound(mTitleHeader[i], mWidthEachEvent);
            if (!TextUtils.isEmpty(value)) {
                drawTextAndBreakLine(canvas, mPaintTitleText, dx + (mWidthEachEvent / 2),
                        mHeightHeader / 2 - (bounds.height() / 4) + (mTextTitleStage / 2), mWidthEachEvent, value);
            }

        }
//    }
    }

    private String getTextForStageBound(String stageName, float mWidthEachEvent) {
        if (stageName == null) return "";

        Rect bounds = new Rect();
        String nameOutput = stageName;

        mPaintTitleText.getTextBounds(nameOutput, 0, nameOutput.length(), bounds);
        boolean isHandleLevel3 = false;
        while (bounds.width() / 3 > mWidthEachEvent && nameOutput.length() > 2) {
            isHandleLevel3 = true;
            nameOutput = nameOutput.substring(0, nameOutput.length() - 2);
            mPaintTitleText.getTextBounds(nameOutput, 0, nameOutput.length(), bounds);
        }

        if (isHandleLevel3 && nameOutput.length() > 6) {
            nameOutput = nameOutput.substring(0, nameOutput.length() - 6);
            nameOutput = nameOutput + "...";
        }

        return nameOutput;
    }

    private void drawCurrentTime(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.colorHighLine));
        paint.setTextSize(mTextTimeRuler);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

        for (int i = 0; i < MAXIMUM_HOUR_IN_DAY; i++) {
            float top = mCurrentOrigin.y + mHeightEachEvent * i + mMinMargin + mHeightHeader;
            if (top < (getHeight())) {
                if (currentHour == i) {
                    int hourConverted = currentHour;
                    if (currentHour >= PLUS_HOUR_FOR_DAY) {
                        hourConverted -= PLUS_HOUR_FOR_DAY;
                    }
                    float dy = top + ((currentMin * mNormalDistance) / 10);
                    RectF rectF = new RectF();
                    rectF.top = dy - mNormalDistance;
                    rectF.bottom = dy + mNormalDistance;
                    rectF.left = mDefaultCornerRadius * 2;
                    rectF.right = mWidthHeader - mDefaultCornerRadius * 2;
                    //get width of text
                    Rect bounds = new Rect();
                    String value = String.format(Locale.getDefault(), "%02d:%02d", hourConverted, currentMin);
                    mPaintCurrentTime.getTextBounds(value, 0, value.length(), bounds);

                    canvas.drawRoundRect(rectF, mDefaultCornerRadius, mDefaultCornerRadius, mPaintCurrentTime);
                    canvas.drawText(value,
                            rectF.width() / 2 + (bounds.width() / 2),
                            dy + mNormalDistance - (rectF.height() / 2) + (bounds.height()), paint);
                    canvas.drawLine(rectF.right, dy, mWidthHeader, dy, mPaintCurrentTime);
                }
            }
        }
    }

    /**
     * Draw Left header Time
     * @param canvas
     */
    private void drawTime(Canvas canvas) {
        Paint paintEvenBg = new Paint();
        paintEvenBg.setColor(Color.BLACK);
        canvas.clipRect(0, mHeightHeader, mWidthHeader, getHeight(), Region.Op.REPLACE);
        canvas.drawRect(0, mHeightHeader, mWidthHeader, getHeight(), paintEvenBg);
        for (int i = 0; i < MAXIMUM_HOUR_IN_DAY; i++) {
            float top = mCurrentOrigin.y + mHeightEachEvent * i + mMinMargin + mHeightHeader;
            if (top < (getHeight())) {
                drawTextTime((int) top, canvas, i);
                if (i >= 24) {
                    mPaintLineStroke.setColor(getResources().getColor(R.color.colorAccent));
                    mPaintLineNoStroke.setColor(getResources().getColor(R.color.colorAccent));
                } else {
                    mPaintLineNoStroke.setColor(getResources().getColor(R.color.colorTitle));
                    mPaintLineStroke.setColor(getResources().getColor(R.color.colorTitle));
                }
                canvas.drawLine(mWidthHeader, (int) top, mWidthHeader - 30, (int) top, mPaintLineStroke);
                canvas.drawLine(mWidthHeader, (int) top + mNormalDistance, mWidthHeader - mMinMargin,
                        (int) top + mNormalDistance, mPaintLineNoStroke);
                canvas.drawLine(mWidthHeader, (int) top + mNormalDistance * 2,
                        mWidthHeader - mMinMargin, (int) top + mNormalDistance * 2, mPaintLineNoStroke);
                canvas.drawLine(
                        mWidthHeader, (int) top + mHeightEachEvent / 2, mWidthHeader - mMaxMargin,
                        (int) top + mHeightEachEvent / 2, mPaintLineNoStroke);
                canvas.drawLine(
                        mWidthHeader, (int) top + mNormalDistance * 4,
                        mWidthHeader - mMinMargin, (int) top + mNormalDistance * 4, mPaintLineNoStroke);
                canvas.drawLine(
                        mWidthHeader, (int) top + mNormalDistance * 5,
                        mWidthHeader - mMinMargin, (int) top + mNormalDistance * 5, mPaintLineNoStroke);
            }
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            mCurrentOrigin.y = mScroller.getCurrY();
            mCurrentOrigin.x = mScroller.getCurrX();
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    /**
     * @param top    location top
     * @param canvas canvas obj
     * @param i      time hour
     *               draw text time ruler
     */
    private void drawTextTime(int top, Canvas canvas, int i) {
        Paint paintTextCounter = new Paint();
        paintTextCounter.setColor(getResources().getColor(R.color.colorTitle));
        paintTextCounter.setTextSize(mTextTimeRuler);
        paintTextCounter.setTextAlign(Paint.Align.CENTER);
        paintTextCounter.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        switch (i) {
            case 24:
                i = 0;
                paintTextCounter.setColor(getResources().getColor(R.color.colorAccent));
                break;
            case 25:
                i = 1;
                paintTextCounter.setColor(getResources().getColor(R.color.colorAccent));
                break;
            case 26:
                i = 2;
                paintTextCounter.setColor(getResources().getColor(R.color.colorAccent));
                break;
            case 27:
                i = 3;
                paintTextCounter.setColor(getResources().getColor(R.color.colorAccent));
                break;
            case 28:
                i = 4;
                paintTextCounter.setColor(getResources().getColor(R.color.colorAccent));
                break;
            case 29:
                i = 5;
                paintTextCounter.setColor(getResources().getColor(R.color.colorAccent));
                break;
            case 30:
                i = 6;
                paintTextCounter.setColor(getResources().getColor(R.color.colorAccent));
                break;
            case 31:
                i = 7;
                paintTextCounter.setColor(getResources().getColor(R.color.colorAccent));
                break;
            case 32:
                i = 8;
                paintTextCounter.setColor(getResources().getColor(R.color.colorAccent));
                break;
            default:
                break;
        }
        Rect bounds = new Rect();
        String value = checkTime(i);
        paintTextCounter.getTextBounds(value, 0, value.length(), bounds);
        int height = bounds.height();
        int widthText = bounds.width();
        canvas.drawText(value,
                mWidthHeader / 2 - (widthText / 4) + (mTextTimeRuler / 2),
                top - (height / 4) + (mTextTimeRuler / 2), paintTextCounter);
    }

    private String checkTime(int time) {
        String text = ":00";
        return String.format(Locale.getDefault(), "%02d%s", time, text);
    }

    /**
     * draw O'clock
     */
    private void drawIconBitmapOlockAtTopLeftTimeTable(Canvas canvas) {
        Paint paintOclock = new Paint();
        paintOclock.setStrokeWidth(2);
        paintOclock.setStyle(Paint.Style.STROKE);
        canvas.clipRect(0, 0, mWidthHeader, mHeightHeader, REPLACE);
        canvas.drawColor(Color.BLACK);
        Drawable drawable = getResources().getDrawable(R.drawable.ic_access_time);
        Bitmap bitmap = drawableToBitmap(drawable);
        int widthBitmap = bitmap.getWidth();
        int heightBitmap = bitmap.getHeight();
        canvas.drawBitmap(bitmap, (mWidthHeader - widthBitmap) / 2,
                (mHeightHeader - heightBitmap) / 2, paintOclock);
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


    private void drawEvent(Canvas canvas, float top, int i, int dx) {
//        RectF rectF;
//        for (int j = 0; j < mStages.size(); j++) {
//            Stage stage = mStages.get(j);
//            if (stage.getEvents() != null && stage.getEvents().size() != 0) {
//                for (int k = 0; k < stage.getEvents().size(); k++) {
//                    EventLog.Event event = stage.getEvents().get(k);
//                    if (mTimetableContainerType != TimetableContainerType.TIME_TABLE_MY_FES ||
//                            (mTimetableContainerType == TimetableContainerType.TIME_TABLE_MY_FES && event.isWatched())) {
//                        int timeHourStart = TimerUtils.getHourFromString(event.getStartAt());
//                        int timeHourEnd = TimerUtils.getHourFromString(event.getEndAt());
//                        int timeMinStart = TimerUtils.getMinFromString(event.getStartAt());
//                        int timeMinEnd = TimerUtils.getMinFromString(event.getEndAt());
//
//                        if (timeHourStart == i) {
//
//                            float dy = top + ((timeMinStart * mNormalDistance) / 10);
//
//                            float lastDy = top + mHeightEachEvent * (timeHourEnd - timeHourStart)
//                                    + ((timeMinEnd * mNormalDistance) / 10);
//                            rectF = new RectF();
//                            rectF.bottom = (int) lastDy - AppUtils.dipToPx(mContext, 0.5f);
//
//                            float widthForMM20 = mWidthEachEvent / (float) event.getKey();
//                            if (mTimetableContainerType == TimetableContainerType.TIME_TABLE_MY_FES) {
//                                rectF.left = dx + widthForMM20 * (event.getColumnLevelTimeTable() - 1) + AppUtils.dipToPx(mContext, 0.5f);
//                            } else {
//                                rectF.left = dx + mWidthEachEvent * (stage.getKey() - 1) + AppUtils.dipToPx(mContext, 0.5f);
//                            }
//
//                            if (mTimetableContainerType == TimetableContainerType.TIME_TABLE_MY_FES) {
//                                rectF.right = rectF.left + widthForMM20;
//                            } else {
//                                rectF.right = dx + mWidthEachEvent * stage.getKey();
//                            }
//                            rectF.right -= AppUtils.dipToPx(mContext, 0.5f);
//                            rectF.top = (int) dy + AppUtils.dipToPx(mContext, 0.5f);
//                            if (mEventRects != null && mEventRects.size() > 0) {
//                                for (EventRect eventRect : mEventRects) {
//                                    if (eventRect.getEvent() == event) {
//                                        eventRect.setRect(rectF);
//                                    }
//                                }
//                            }
//
//                            String title;
//                            String timeStart = getTimeOver24(event.getStartAt());
//                            String timeEnd = getTimeOver24(event.getEndAt());
//                            Rect timeBounds = new Rect();
//                            checkFavoriteEvent(event);
//
//                            if (event.isFavorite()) {
//                                if (event.isWatched()) {
//                                    canvas.drawRoundRect(rectF, mDefaultCornerRadius, mDefaultCornerRadius, mPaintEventWatch);
//                                } else {
//                                    canvas.drawRoundRect(rectF, mDefaultCornerRadius, mDefaultCornerRadius, mPaintEventFavorite);
//                                }
//                            } else if (event.isWatched()) {
//                                canvas.drawRoundRect(rectF, mDefaultCornerRadius, mDefaultCornerRadius, mPaintEventWatch);
//                            } else {
//                                canvas.drawRoundRect(rectF, mDefaultCornerRadius, mDefaultCornerRadius, mPaintEventNormal);
//                            }
//
//                            mPaintTimeText.getTextBounds(timeStart, 0, timeStart.length(), timeBounds);
//
//                            canvas.drawText(timeStart, rectF.left + (timeBounds.width() / 4),
//                                    dy + (timeBounds.height() / 2) + (mTextTimeEvent),
//                                    mPaintTimeText);
//                            List<Artist> artists = new ArrayList<>();
//                            artists.addAll(event.getArtists());
//                            int sizeArtists = artists.size();
//
//                            float positionX;
//                            if (mTimetableContainerType == TimetableContainerType.TIME_TABLE_MY_FES) {
//                                positionX = rectF.left + (mWidthEachEvent / event.getKey() / 2);
//                            } else {
//                                positionX = rectF.left + (mWidthEachEvent / 2);
//                            }
//
//                            /* Draw EventArtistsBox. */
//                            // Artist Rectangle.
//                            float padding = 5;
//                            float verticalMargin = 0;
//                            // EventArtistsBox template.
//                            Rect artistRectDummy = new Rect();
//                            mPaintEventTitleText.getTextBounds(artists.get(0).getName(),
//                                    0, artists.get(0).getName().length(), artistRectDummy);
//                            // EventArtist total.
//                            int artistCnt = (int) (rectF.height() / (artistRectDummy.height() + padding * 2));
//                            // EventArtistDisplayed total.
//                            int totalLine = (artistCnt < sizeArtists) ? artistCnt : sizeArtists;
//                            // If EventArtistsBox too height (EventArtist quantity << EventArtistDisplayed total,
//                            // set vertical margin to display at centre.
//                            if (rectF.height() > totalLine * (artistRectDummy.height() + padding * 2)) {
//                                verticalMargin = (rectF.height() - totalLine * (artistRectDummy.height() + padding * 2)) / 2;
//                            }
//                            // Draw EventArtistsBox.
//                            for (int l = 0; l < totalLine; l++) {
//                                // Check & Set Text color.
//                                if (artists.get(l).isFavorite() && !event.isWatched()) {
//                                    // In [current artist is favorite & event is not watched] case, Text color = [pink].
//                                    mPaintEventTitleText.setColor(getResources().getColor(R.color.colorAccent));
//                                } else {
//                                    // In other cases, Text color = [white].
//                                    mPaintEventTitleText.setColor(Color.WHITE);
//                                }
//                                // Get current artist display box.
//                                Rect rect = new Rect();
//                                mPaintEventTitleText.getTextBounds(artists.get(0).getName(),
//                                        0, artists.get(0).getName().length(), rect);
//                                // Get the position of current artist display box.
//                                float y = verticalMargin + rectF.top + (l * (rect.height() + padding * 2)) + padding + rect.height();
//                                // Measure DisplayArtistName.
//                                int ArtistNameEndPos = mPaintEventTitleText.breakText(artists.get(l).getName().toCharArray(), 0,
//                                        artists.get(l).getName().length(), rectF.width() - padding * 2, null);
//                                // Draw current artist display box.
//                                canvas.drawText(artists.get(l).getName(), 0, ArtistNameEndPos, positionX,
//                                        y, mPaintEventTitleText);
//                            }
//
//                            /* Draw EventTimeEnd. */
//                            canvas.drawText(timeEnd, rectF.left + (timeBounds.width() / 4),
//                                    lastDy - (timeBounds.height()) + (mTextTimeEvent / 4),
//                                    mPaintTimeText);
//
//                        }
//                    }
//                }
//            }
//        }
    }

    private int getMaxLineForBox(float height) {
//        float heightBoxDP = AppUtils.pxToDip(height, mContext);
//        if (heightBoxDP <= 30) {
//            return 1;
//        } else if (heightBoxDP <= 50) {
//            return 2;
//        }

        return 100; // Assume maximum 100 artists
    }

    private void checkFavoriteEvent(Event event) {

    }

    private void drawTextAndBreakLine(final Canvas canvas, final Paint paint,
                                      final float x, final float y, final float maxWidth,
                                      final String text, float maxHeight, float minHeight) {
        String textToDisplay = text;
        String tempText;
        char[] chars;
        float textHeight = paint.descent() - paint.ascent();
        float lastY = y;
        int nextPos;
        int lengthBeforeBreak;
        Rect rect = new Rect();
        mPaintEventTitleText.getTextBounds(text, 0, text.length(), rect);
        if (y > (maxHeight + rect.height())) {
            return;
        }
        do {
            lengthBeforeBreak = textToDisplay.length();
            chars = textToDisplay.toCharArray();
            nextPos = paint.breakText(chars, 0, chars.length, maxWidth - (maxWidth / 4), null);
            tempText = textToDisplay.substring(0, nextPos);
            textToDisplay = textToDisplay.substring(nextPos, textToDisplay.length());
            if (lastY < (minHeight)) {
                lastY = lastY + (rect.height());
            }
            canvas.drawText(tempText, x, lastY, paint);
            lastY += (textHeight);
            if (lastY + (textHeight) > maxHeight) {
                return;
            }
        } while (nextPos < lengthBeforeBreak);
    }

    private void drawTextManyArtist(final Canvas canvas, final Paint paint,
                                    final float x, final float y, final float maxWidth,
                                    final String text, float maxHeight) {
        String textToDisplay = text;
        String tempText;
        char[] chars;
        float textHeight = paint.descent() - paint.ascent();
        int nextPos;
        int lengthBeforeBreak;
        Rect rect = new Rect();
        mPaintEventTitleText.getTextBounds(text, 0, text.length(), rect);
        float lastY = y + rect.height();
        if (y > (maxHeight + rect.height())) {
            return;
        }
        do {
            lengthBeforeBreak = textToDisplay.length();
            chars = textToDisplay.toCharArray();
            nextPos = paint.breakText(chars, 0, chars.length, maxWidth - (maxWidth / 4), null);
            tempText = textToDisplay.substring(0, nextPos);
            textToDisplay = textToDisplay.substring(nextPos, textToDisplay.length());
            canvas.drawText(tempText, x, lastY, paint);
            lastY += (textHeight / 2);
            if (lastY + (textHeight) > maxHeight) {
                return;
            }
        } while (nextPos < lengthBeforeBreak);
    }

    private void drawTextAndBreakLine(final Canvas canvas, final Paint paint,
                                      final float x, final float y, final float maxWidth,
                                      final String text, float maxHeight) {
        String textToDisplay = text;
        String tempText;
        char[] chars;
        float textHeight = paint.descent() - paint.ascent();
        float lastY = y;
        int nextPos;
        int lengthBeforeBreak;
        Rect rect = new Rect();
        mPaintEventTitleText.getTextBounds(text, 0, text.length(), rect);
        if (y > (maxHeight + rect.height())) {
            return;
        }
        do {
            lengthBeforeBreak = textToDisplay.length();
            chars = textToDisplay.toCharArray();
            nextPos = paint.breakText(chars, 0, chars.length, maxWidth - (maxWidth / 4), null);
            tempText = textToDisplay.substring(0, nextPos);
            textToDisplay = textToDisplay.substring(nextPos, textToDisplay.length());
            canvas.drawText(tempText, x, lastY, paint);
            lastY += (textHeight / 2);
            if (lastY + (textHeight) > maxHeight) {
                return;
            }
        } while (nextPos < lengthBeforeBreak);
    }

    private void drawTextAndBreakLine(final Canvas canvas, final Paint paint,
                                      final float x, final float y, final float maxWidth, final String text) {
        String textToDisplay = text;
        String tempText;
        char[] chars;
        float textHeight = paint.descent() - paint.ascent();
        float lastY = y;
        int nextPos;
        int lengthBeforeBreak;
        Rect rect = new Rect();
        mPaintTitleText.getTextBounds(text, 0, text.length(), rect);
        do {
            lengthBeforeBreak = textToDisplay.length();
            chars = textToDisplay.toCharArray();
            nextPos = paint.breakText(chars, 0, chars.length, maxWidth - AppUtils.dipToPixels(mContext, 2), null);
            tempText = textToDisplay.substring(0, nextPos);
            textToDisplay = textToDisplay.substring(nextPos, textToDisplay.length());
            canvas.drawText(tempText, x + AppUtils.dipToPixels(mContext, 2), lastY, paint);
            lastY += textHeight;
        } while (nextPos < lengthBeforeBreak);
    }

    private final GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {
            mScroller.forceFinished(true);
            mStickyScroller.forceFinished(true);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            switch (mCurrentScrollDirection) {
                case NONE: {
                    // Allow scrolling only in one direction.
                    if (Math.abs(distanceX) > Math.abs(distanceY)) {
                        mCurrentScrollDirection = Direction.HORIZONTAL;
                    } else {
                        mCurrentScrollDirection = Direction.VERTICAL;
                    }
                    break;
                }
            }
            mIsScale = true;
            mIsPressDown = false;
            // Calculate the new origin after scroll.
            switch (mCurrentScrollDirection) {
                case HORIZONTAL:
                    float mXScrollingSpeed = 1f;
                    mCurrentOrigin.x -= distanceX * mXScrollingSpeed;
                    ViewCompat.postInvalidateOnAnimation(TimetableContainer.this);
                    break;
                case VERTICAL:
                    mCurrentOrigin.y -= distanceY;
                    ViewCompat.postInvalidateOnAnimation(TimetableContainer.this);
                    break;
            }
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return super.onDoubleTapEvent(e);
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mIsScale) {
//            for (FesEventRectF eventRect : mEventRects) {
//                if (eventRect.getRect() != null) {
//                    if (eventRect.getRect().contains(event.getX(), event.getY())) {
//                        switch (event.getAction()) {
//                            case MotionEvent.ACTION_DOWN:
//                                mRectFPress = eventRect.getRect();
//                                mIsPressDown = true;
//                                invalidate();
//                                break;
//                            case MotionEvent.ACTION_UP:
//                                mIsPressDown = false;
//                                mCountEventPress = 0;
//                                invalidate();
//                                if (isSingleClick()) {
//                                    mIOnClickEventItem.clickEventItem(eventRect.getEvent());
//                                }
//                                break;
//                            case MotionEvent.ACTION_CANCEL:
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        mIsPressDown = false;
//                                        mCountEventPress = 0;
//                                        invalidate();
//                                    }
//                                }, 1000);
//                                break;
//                            case MotionEvent.ACTION_MOVE:
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        mIsPressDown = false;
//                                        mCountEventPress = 0;
//                                        invalidate();
//                                    }
//                                }, 1000);
//                                break;
//                        }
//                    }
//                }
//            }
        }
        mScaleDetector.onTouchEvent(event);
        boolean val = mGestureDetector.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_UP && mCurrentFlingDirection == Direction.NONE) {
            mCurrentScrollDirection = Direction.NONE;
            mIsScale = false;
        }
        if (event.getAction() == MotionEvent.ACTION_UP && mIsScale) {
            mIsScale = false;
        }
        return val;
    }

    private boolean isSingleClick() {
        long clickTime = System.currentTimeMillis();
        long transcureTime = clickTime - lastClickTime;
        lastClickTime = clickTime;
        return transcureTime > DOUBLE_CLICK_TIME_DELTA;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void invalidated() {
        invalidate();
    }

    public void drawTextTime() {
        rightNow = Calendar.getInstance(Locale.JAPAN);
        currentHour = rightNow.get(Calendar.HOUR_OF_DAY) + mPlusHour;
        currentMin = rightNow.get(Calendar.MINUTE);
        invalidate();
    }

    private enum Direction {
        NONE, HORIZONTAL, VERTICAL
    }

    public interface IOnTimeTableClickEvent {
        void clickEventItem(Event event);

        void updateCurrentTime();
    }

//    public void updateOffset(String timeEvent, int artistId) {
//        if (TextUtils.isEmpty(timeEvent)) {
//            timeEvent = "";
//        }
//        Log.e("xxx", "updateOffset: " + timeEvent);
//        int hourEvent = !timeEvent.equals("") ? Integer.parseInt(timeEvent.substring(11, 13)) : -1;
//        int minEvent = !timeEvent.equals("") ? Integer.parseInt(timeEvent.substring(14, 16)) : -1;
//        int starthour = Integer.MAX_VALUE;
//        int endhour = 0;
//        Event event = null;
//        if (hourEvent > 0) {
//            starthour = hourEvent;
//            endhour = minEvent;
//            for (int j = 0; j < mStages.size(); j++) {
//                if (mStages.get(j).getEvents() != null && mStages.get(j).getEvents().size() != 0) {
//                    for (int k = 0; k < mStages.get(j).getEvents().size(); k++) {
//                        if (TimerUtils.getHourFromString(mStages.get(j).getEvents().get(k).getStartAt()) == hourEvent) {
//                            List<Artist> artists = mStages.get(j).getEvents().get(k).getArtists();
//                            for (Artist artist : artists) {
//                                if (artist.getId() == artistId) {
//                                    event = mStages.get(j).getEvents().get(k);
//                                    if (mTimetableContainerType == TimetableContainerType.TIME_TABLE_ALL_STAGE) {
//                                        mIOnClickEventItem.clickEventItem(event);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        } else {
//            for (int j = 0; j < mStages.size(); j++) {
//                if (mStages.get(j).getEvents() != null && mStages.get(j).getEvents().size() != 0) {
//                    for (int k = 0; k < mStages.get(j).getEvents().size(); k++) {
//                        int timeHourStart = TimerUtils.getHourFromString(mStages.get(j).getEvents().get(k).getStartAt());
//                        int timeMinStart = TimerUtils.getMinFromString(mStages.get(j).getEvents().get(k).getStartAt());
//                        if (timeHourStart < starthour) {
//                            starthour = timeHourStart;
//                            endhour = timeMinStart;
//                        }
//                    }
//                }
//            }
//        }
//        updateHeight(starthour, endhour);
//
//    }

    private void updateHeight(int starthour, int endhour) {
        if (starthour != Integer.MAX_VALUE) {
            mStoreOffet = (starthour - 1) * mHeightEachEvent - (endhour / 10 * mHeightEachEvent / mMaximumStage);
        }
        if (mStoreOffet > 0) {
            mCurrentOrigin.y -= mStoreOffet;
            ViewCompat.postInvalidateOnAnimation(TimetableContainer.this);
        }
    }

    public boolean isChangedScrollOffset() {
        if (mCurrentOrigin.y == 0 && mStoreOffet == -1) {
            return false;
        }
        return mCurrentOrigin.y != -mStoreOffet;
    }

    public enum TimetableContainerType {
        TIME_TABLE_ALL_STAGE, TIME_TABLE_MY_FES, TIME_TABLE_MM20
    }
}