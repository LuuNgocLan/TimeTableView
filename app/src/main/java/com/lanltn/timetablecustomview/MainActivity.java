
package com.lanltn.timetablecustomview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TimetableContainer.IOnTimeTableClickEvent {

    private TimeTableView timeTableView;
    private List<Event> mListFesEvent = new ArrayList<>();
    private List<Stage> mStageList = new ArrayList<>();
    private Calendar timeCurrent = Calendar.getInstance();
    private TimetableContainer timetableContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        timetableContainer = findViewById(R.id.timeTableContainer);
        timetableContainer.setmStageList(mStageList);
        timetableContainer.setmIsToday(true);
        timetableContainer.setmIOnClickEventItem(this);
    }

    private void initData() {
        for(int i = 0; i<6; i++){

        }
        List<Event> mListEvent = new ArrayList<>();
        mListEvent.add(new Event("DEDE", "00:00", "01:00", 0));
        mListEvent.add(new Event("TRAIN", "01:00", "02:00", 0));
        mListEvent.add(new Event("OGREYOU", "02:00", "03:00", 0));
        mListEvent.add(new Event("DOCT", "5:00", "6:00", 0));
        mListEvent.add(new Event("Upend", "10:20", "11:00", 0));
        mStageList.add(new Stage("RED MARIQUE",mListEvent));

        List<Event> mListEvent_1 = new ArrayList<>();
        mListEvent_1.add(new Event("GALLANT", "10:20", "11:00", 1));
        mListEvent_1.add(new Event("SOCCER", "19:30", "21:00", 0));
        mListEvent_1.add(new Event("THE BACK", "00:00", "02:00", 1));
        mListEvent_1.add(new Event("CATFISH", "8:00", "9:30", 1));
        mStageList.add(new Stage("GREEN STAGE",mListEvent_1));

        mListFesEvent.add(new Event("YOGE", "12:00", "13:00", 0));
        mListFesEvent.add(new Event("EDEN", "13:00", "15:50", 2));
        mListFesEvent.add(new Event("FATHER", "16:00", "17:10", 1));
        mListFesEvent.add(new Event("Rei", "18:00", "19:00", 0));
        mListFesEvent.add(new Event("DEDE", "3:00", "3:50", 1));
        mStageList.add(new Stage("GYPSY AVALON", mListFesEvent));
//
//        mListFesEvent.clear();
//        mListFesEvent.add(new Event("Micha", "16:00", "16:50", 2));
//        mListFesEvent.add(new Event("Upend bonjour", "1:00", "2:50", 1));

//        mListFesEvent.add(new Event("DATS", "3:00", "4:50", 0));
//        mListFesEvent.add(new Event("the HIATU", "5:00", "7:50", 1));
//        mListFesEvent.add(new Event("Numb", "21:30", "23:00", 0));
//        mStageList.add(new Stage("FIELD OF HEAVEN",mListFesEvent));
//
//        mListFesEvent.clear();
//        mListFesEvent.add(new Event("Micha", "16:00", "16:50", 2));
//        mListFesEvent.add(new Event("Upend bonjour", "1:00", "2:50", 1));
//        mListFesEvent.add(new Event("DATS", "3:00", "4:50", 0));
//        mListFesEvent.add(new Event("the HIATU", "5:00", "7:50", 1));
//        mListFesEvent.add(new Event("Numb", "21:30", "23:00", 2));
//        mStageList.add(new Stage("FIELD OF HEAVEN",mListFesEvent));
//
//        mListFesEvent.clear();
//        mListFesEvent.add(new Event("Micha", "16:00", "16:50", 2));
//        mListFesEvent.add(new Event("Upend bonjour", "1:00", "2:50", 1));
//        mListFesEvent.add(new Event("DATS", "3:00", "4:50", 0));
//        mListFesEvent.add(new Event("the HIATU", "5:00", "7:50", 1));
//        mListFesEvent.add(new Event("Numb", "21:30", "23:00", 0));
//        mStageList.add(new Stage("FIELD OF HEAVEN", mListFesEvent));
    }

    @Override
    public void clickEventItem(Event event) {
        Toast.makeText(this, "YOUR CLICK ON " + event.getmNameEvent(), Toast.LENGTH_SHORT).show();
        event.setIdType(2);

    }

    @Override
    public void updateCurrentTime() {

    }
}
