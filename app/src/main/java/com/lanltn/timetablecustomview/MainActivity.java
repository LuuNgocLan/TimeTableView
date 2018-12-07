
package com.lanltn.timetablecustomview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TimeTableView timeTableView;
    private List<FesEvent> mListFesEvent = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        timeTableView = findViewById(R.id.pixelGridView);
        timeTableView.setmNumColumns(6);
        timeTableView.setmNumRows(24);
        timeTableView.setmCellHeight(70);
        timeTableView.setWidthLabelHours(110);
        timeTableView.setHeightTitle(150);
        timeTableView.setmListFesEvent(mListFesEvent);
        timeTableView.setFocusToTime("12:00");

    }

    private void initData() {
        mListFesEvent.add(new FesEvent("DEDE", "00:00", "01:00", 0, 0));
        mListFesEvent.add(new FesEvent("DOCT", "5:00", "6:00", 2, 1));
        mListFesEvent.add(new FesEvent("Upend", "10:20", "11:00", 3, 2));
        mListFesEvent.add(new FesEvent("Upend", "10:20", "11:00", 2, 0));
        mListFesEvent.add(new FesEvent("SOCCER", "19:30", "21:00", 0, 2));
        mListFesEvent.add(new FesEvent("YOGE", "12:00", "13:50", 4, 0));
        mListFesEvent.add(new FesEvent("Micha", "13:00", "15:50", 3, 1));
    }

}
