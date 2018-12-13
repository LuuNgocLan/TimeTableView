package com.lanltn.timetablecustomview;

import java.util.List;

public class Stage {
    private String mNameStage;
    private List<Event> mEventList;

    public Stage(List<Event> mEventList) {
        this.mEventList = mEventList;
    }

    public List<Event> getmEventList() {
        return mEventList;
    }

    public void setmEventList(List<Event> mEventList) {
        this.mEventList = mEventList;
    }

    public String getmNameStage() {
        return mNameStage;
    }

    public Stage(String mNameStage, List<Event> mEventList) {
        this.mNameStage = mNameStage;
        this.mEventList = mEventList;
    }

    public void setmNameStage(String mNameStage) {
        this.mNameStage = mNameStage;
    }
}
