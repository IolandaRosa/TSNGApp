package com.example.tsngapp.ui.chart;

import android.annotation.SuppressLint;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class DateTimeAxisFormatter extends ValueFormatter {
    private String time;
    private HashMap<Float, Long> diffs;

    public DateTimeAxisFormatter(String time, HashMap<Float, Long> diffs) {
        this.time = time;
        this.diffs = diffs;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        Long times = diffs.get(value);

        Date d = new Date();
        SimpleDateFormat returnedDate = new SimpleDateFormat("HH:mm:ss");

        if(time.equals("day")) {
            returnedDate = new SimpleDateFormat("dd,HH");
        }
        else if (time.equals("month")){
            returnedDate = new SimpleDateFormat("dd MMM");
        }

        if( times != null) {
            d = new Date(times);

        }

        if ( time.equals("day")){
            String day = returnedDate.format(d);
            day = "d"+day.split(",")[0]+" " + day.split(",")[1]+"h";
            return day;
        }

        return returnedDate.format(d);
    }
}
