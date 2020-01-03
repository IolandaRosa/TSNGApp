package com.example.tsngapp.api.model;

import android.annotation.SuppressLint;

import com.example.tsngapp.helpers.Constants;
import com.example.tsngapp.helpers.DateUtil;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SimpleValueSensor implements Serializable {
    private int id;
    private float value;
    private Date date;

    public SimpleValueSensor() {}

    public SimpleValueSensor(int id, float value, Date date) {
        this.id = id;
        this.value = value;
        this.date = date;
    }

    @SuppressLint("SimpleDateFormat")
    public SimpleValueSensor(int id, float value, String strDate) throws ParseException {
        this.id = id;
        this.value = value;
        this.date = DateUtil.getDateFromString(strDate);
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
