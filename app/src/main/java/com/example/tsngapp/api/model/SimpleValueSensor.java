package com.example.tsngapp.api.model;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SimpleValueSensor {
    private float value;
    private Date date;

    public SimpleValueSensor(float value, Date date) {
        this.value = value;
        this.date = date;
    }

    @SuppressLint("SimpleDateFormat")
    public SimpleValueSensor(float value, String strDate) throws ParseException {
        final Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(strDate);
        this.value = value;
        this.date = date;
    }

    public float getValue() {
        return value;
    }

    public Date getDate() {
        return date;
    }
}
