package com.example.tsngapp.api.model;

import android.annotation.SuppressLint;

import com.example.tsngapp.helpers.Constants;
import com.example.tsngapp.helpers.DateUtil;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SimpleValueSensor implements Serializable {
    private float value;
    private Date date;

    public SimpleValueSensor() {}

    public SimpleValueSensor(float value, Date date) {
        this.value = value;
        this.date = date;
    }

    @SuppressLint("SimpleDateFormat")
    public SimpleValueSensor(float value, String strDate) throws ParseException {
        final Date date = DateUtil.getDateFromString(strDate);
        this.value = value;
        this.date = date;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
