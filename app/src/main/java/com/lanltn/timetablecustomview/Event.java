package com.lanltn.timetablecustomview;

public class Event {
    private String mNameEvent;
    private String mStartEvent;
    private String mEndEvent;
    private int idCol;
    private int idType;

    public Event(String mNameEvent, String mStartEvent, String mEndEvent, int idCol, int idType) {
        this.mNameEvent = mNameEvent;
        this.mStartEvent = mStartEvent;
        this.mEndEvent = mEndEvent;
        this.idCol = idCol;
        this.idType = idType;
    }

    public int getIdType() {
        return idType;
    }

    public void setIdType(int idType) {
        this.idType = idType;
    }


    public int getIdCol() {
        return idCol;
    }

    public void setIdCol(int idCol) {
        this.idCol = idCol;
    }

    public String getmNameEvent() {
        return mNameEvent;
    }

    public void setmNameEvent(String mNameEvent) {
        this.mNameEvent = mNameEvent;
    }

    public String getmStartEvent() {
        return mStartEvent;
    }

    public void setmStartEvent(String mStartEvent) {
        this.mStartEvent = mStartEvent;
    }

    public String getmEndEvent() {
        return mEndEvent;
    }

    public void setmEndEvent(String mEndEvent) {
        this.mEndEvent = mEndEvent;
    }
}
