package com.example.tsngapp.api.model;

import android.annotation.SuppressLint;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SimpleValueSensor {
    private String sensorId;
    private float value;
    private Date date;

    public SimpleValueSensor(String sensorId, float value, Date date) {
        this.sensorId = sensorId;
        this.value = value;
        this.date = date;
    }

    @SuppressLint("SimpleDateFormat")
    public SimpleValueSensor(String sensorId, float value, String strDate) throws ParseException {
        final Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(strDate);
        this.sensorId = sensorId;
        this.value = value;
        this.date = date;
    }

    public String getSensorId() {
        return sensorId;
    }

    public float getValue() {
        return value;
    }

    public Date getDate() {
        return date;
    }
}
