package com.example.tsngapp.ui.chart;

import android.annotation.SuppressLint;

import com.example.tsngapp.helpers.Constants;
import com.example.tsngapp.helpers.DateUtil;
import com.example.tsngapp.helpers.StateManager;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.Date;

public class TimestampAxisFormatter extends ValueFormatter {

    @SuppressLint("SimpleDateFormat")
    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        Date time = new Date();

        final long fullTimestamp = ((long) value) +
                StateManager.getInstance().getCurrentReferenceTimestamp();

        time.setTime(fullTimestamp);
        return DateUtil.getStringFromDate(time, Constants.FULL_TIME_FORMAT);
    }
}
