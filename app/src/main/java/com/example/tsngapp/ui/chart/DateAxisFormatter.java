package com.example.tsngapp.ui.chart;

import android.annotation.SuppressLint;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateAxisFormatter extends ValueFormatter {

    @SuppressLint("SimpleDateFormat")

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        Date date = new Date();
        try {
            String strDate = String.valueOf(value);
            SimpleDateFormat formattedDate = new SimpleDateFormat("HHmmss");
            date = formattedDate.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new SimpleDateFormat("HH:mm:ss").format(date);
    }
}
