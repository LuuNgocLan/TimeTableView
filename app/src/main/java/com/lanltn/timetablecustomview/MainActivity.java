
package com.lanltn.timetablecustomview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.lanltn.timetableview.Event;
import com.lanltn.timetableview.Stage;
import com.lanltn.timetableview.TimeTableView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TimeTableView.IOnTimeTableClickEvent {

    private List<Stage> mStageList = new ArrayList<>();
    private Calendar timeCurrent = Calendar.getInstance();
    private TimeTableView timetableContainer;

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
        List<Event> mListEvent = new ArrayList<>();
        mListEvent.add(new Event("DEDE", "00:00", "01:00", 0));
        mListEvent.add(new Event("TRAIN", "01:00", "02:00", 0));
        mListEvent.add(new Event("OGREYOU", "02:00", "03:00", 1));
        mListEvent.add(new Event("DOCT", "5:00", "6:00", 0));
        mListEvent.add(new Event("Upend", "10:20", "11:00", 0));
        mStageList.add(new Stage("RED MARIQUE RED MARIQUE RED MARIQUE RED MARIQUE RED MARIQUE ", mListEvent));

        mListEvent = new ArrayList<>();
        mListEvent.add(new Event("Micha", "16:00", "16:50", 0));
        mListEvent.add(new Event("Upend bonjour", "1:00", "2:50", 1));
        mListEvent.add(new Event("DATS", "3:00", "4:50", 0));
        mListEvent.add(new Event("the HIATU", "5:00", "7:50", 1));
        mListEvent.add(new Event("Numb", "21:30", "23:00", 0));
        mStageList.add(new Stage("FIELD OF HEAVEN", mListEvent));

        mListEvent = new ArrayList<>();
        mListEvent.add(new Event("GALLANT bvbxvcsbhc ", "00:00", "01:00", 0));
        mListEvent.add(new Event("SOCCER", "03:30", "04:00", 1));
        mListEvent.add(new Event("THE BACK", "05:00", "06:00", 2));
        mListEvent.add(new Event("CATFISH", "8:00", "9:30", 0));
        mStageList.add(new Stage("WHITE STAGE", mListEvent));

        mListEvent = new ArrayList<>();
        mListEvent.add(new Event("SAKi&", "12:00", "13:00", 0));
        mListEvent.add(new Event("MART", "13:00", "15:50", 0));
        mListEvent.add(new Event("RAG'N BONE", "16:00", "17:10", 0));
        mListEvent.add(new Event("The XX", "18:00", "19:00", 0));
        mListEvent.add(new Event("ROUT E 17", "3:00", "3:50", 2));
        mStageList.add(new Stage("ORANGE COFFE", mListEvent));

        mListEvent = new ArrayList<>();
        mListEvent.add(new Event("Kirim", "16:00", "16:50", 2));
        mListEvent.add(new Event("Upend bonjour", "1:00", "2:50", 0));
        mListEvent.add(new Event("DATS", "3:00", "4:50", 0));
        mListEvent.add(new Event("the HIATU", "5:00", "7:50", 0));
        mListEvent.add(new Event("Numb", "21:30", "23:00", 0));
        mStageList.add(new Stage("FIELD OF HEAVEN", mListEvent));

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
