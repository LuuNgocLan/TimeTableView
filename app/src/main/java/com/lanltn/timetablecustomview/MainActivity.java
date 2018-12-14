
package com.lanltn.timetablecustomview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TimetableContainer.IOnTimeTableClickEvent{

    private TimeTableView timeTableView;
    private List<Event> mListFesEvent = new ArrayList<>();
    //current time
    private Calendar timeCurrent = Calendar.getInstance();
    private TimetableContainer timetableContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        timetableContainer = findViewById(R.id.timeTableContainer);
        timetableContainer.setmEvents(mListFesEvent);
        timetableContainer.setmIOnClickEventItem(this);
    }

    private void initData() {
        mListFesEvent.add(new Event("DEDE", "00:00", "01:00", 0, 0));
        mListFesEvent.add(new Event("DOCT", "5:00", "6:00", 2, 1));
        mListFesEvent.add(new Event("Upend", "10:20", "11:00", 3, 2));
        mListFesEvent.add(new Event("Upend", "10:20", "11:00", 1, 0));
        mListFesEvent.add(new Event("SOCCER", "19:30", "21:00", 0, 2));

        mListFesEvent.add(new Event("YOGE", "12:00", "13:50", 4, 0));
        mListFesEvent.add(new Event("Micha", "13:00", "15:50", 3, 1));
        mListFesEvent.add(new Event("YOGE", "15:00", "17:10", 5, 1));
        mListFesEvent.add(new Event("Micha", "13:00", "15:50", 3, 1));
        mListFesEvent.add(new Event("DEDE", "3:00", "3:50", 3, 2));
        mListFesEvent.add(new Event("Micha", "13:00", "15:50", 3, 1));
        mListFesEvent.add(new Event("Upend", "1:00", "2:50", 1, 2));
        mListFesEvent.add(new Event("YOGE", "3:00", "4:50", 0, 1));
        mListFesEvent.add(new Event("Upend", "5:00", "7:50", 1, 0));
        mListFesEvent.add(new Event("Micha", "21:30", "23:00", 3, 1));
    }

    @Override
    public void clickEventItem(Event event) {
        Toast.makeText(this,"YOUR CLICK ON "+event.getmNameEvent(),Toast.LENGTH_SHORT).show();

    }

    @Override
    public void updateCurrentTime() {

    }
}
