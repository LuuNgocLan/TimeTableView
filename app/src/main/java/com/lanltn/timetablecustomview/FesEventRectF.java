package com.lanltn.timetablecustomview;

import android.graphics.RectF;

public class FesEventRectF {
    private RectF mRectFFesEvent;
    private FesEvent mFesEvent;

    public FesEventRectF(RectF mRectFFesEvent, FesEvent mFesEvent) {

        this.mRectFFesEvent = mRectFFesEvent;
        this.mFesEvent = mFesEvent;
    }

    public RectF getmRectFFesEvent() {
        return mRectFFesEvent;
    }

    public void setmRectFFesEvent(RectF mRectFFesEvent) {
        this.mRectFFesEvent = mRectFFesEvent;
    }

    public FesEvent getmFesEvent() {
        return mFesEvent;
    }

    public void setmFesEvent(FesEvent mFesEvent) {
        this.mFesEvent = mFesEvent;
    }
}
