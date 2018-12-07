package com.lanltn.timetablecustomview;

public class FesEvent {
    private String mNameFesEvent;
    private String mStartFesEvent;
    private String mEndFesEvent;
    private int idCol;
    private int idFesType;

    public FesEvent(String mNameFesEvent, String mStartFesEvent, String mEndFesEvent, int idCol, int idStatus) {
        this.mNameFesEvent = mNameFesEvent;
        this.mStartFesEvent = mStartFesEvent;
        this.mEndFesEvent = mEndFesEvent;
        this.idCol = idCol;
        this.idFesType = idStatus;
    }

    public int getIdFesType() {
        return idFesType;
    }

    public void setIdFesType(int idFesType) {
        this.idFesType = idFesType;
    }


    public int getIdCol() {
        return idCol;
    }

    public void setIdCol(int idCol) {
        this.idCol = idCol;
    }

    public String getmNameFesEvent() {
        return mNameFesEvent;
    }

    public void setmNameFesEvent(String mNameFesEvent) {
        this.mNameFesEvent = mNameFesEvent;
    }

    public String getmStartFesEvent() {
        return mStartFesEvent;
    }

    public void setmStartFesEvent(String mStartFesEvent) {
        this.mStartFesEvent = mStartFesEvent;
    }

    public String getmEndFesEvent() {
        return mEndFesEvent;
    }

    public void setmEndFesEvent(String mEndFesEvent) {
        this.mEndFesEvent = mEndFesEvent;
    }
}
