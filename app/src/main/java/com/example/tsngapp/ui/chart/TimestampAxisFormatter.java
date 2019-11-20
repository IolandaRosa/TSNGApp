package com.example.tsngapp.ui.chart;

import android.annotation.SuppressLint;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimestampAxisFormatter extends ValueFormatter {
    @SuppressLint("SimpleDateFormat")
    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        Date time = new Date();
        time.setTime((long) value);
        return new SimpleDateFormat("HH:mm").format(time);
    }
}