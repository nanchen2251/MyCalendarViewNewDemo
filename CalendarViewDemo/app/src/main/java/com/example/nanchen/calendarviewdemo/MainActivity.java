package com.example.nanchen.calendarviewdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.nanchen.calendarviewdemo.view.MyCalendarView;
import com.example.nanchen.calendarviewdemo.view.ClickDataListener;

import java.util.Locale;

/**
 * @author nanchen
 * @date   2016-08-11 10:48:21
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyCalendarView myCalendarView = (MyCalendarView) findViewById(R.id.main_calendar);
        myCalendarView.setClickDataListener(new ClickDataListener() {
            @Override
            public void onClickData(int year, int month, int day) {
                String date = String.format(Locale.CHINA,"%04d年%02d月%02d日",year,month,day);
                Toast.makeText(MainActivity.this,date,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
