package com.lanltn.timetableview;

import android.graphics.RectF;

public class EventRectF {

    private RectF mRectFEvent;
    private Event mEvent;

    public EventRectF(RectF mRectFEvent, Event mEvent) {

        this.mRectFEvent = mRectFEvent;
        this.mEvent = mEvent;
    }

    public RectF getmRectFEvent() {
        return mRectFEvent;
    }

    public void setmRectFEvent(RectF mRectFEvent) {
        this.mRectFEvent = mRectFEvent;
    }

    public Event getmEvent() {
        return mEvent;
    }

    public void setmEvent(Event mEvent) {
        this.mEvent = mEvent;
    }
}
