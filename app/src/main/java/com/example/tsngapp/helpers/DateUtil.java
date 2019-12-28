package com.example.tsngapp.helpers;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    @SuppressLint("SimpleDateFormat")
    public static Calendar getCalendarFromDateString(String dateString) throws ParseException {
        Date date = new SimpleDateFormat(Constants.FULL_DATE_FORMAT).parse(dateString);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    @SuppressLint("SimpleDateFormat")
    public static Date getDateFromString(String dateString) throws ParseException {
        return getDateFromString(dateString, Constants.FULL_DATE_FORMAT);
    }

    @SuppressLint("SimpleDateFormat")
    public static Date getDateFromString(String dateString, String format) throws ParseException {
        return new SimpleDateFormat(format).parse(dateString);
    }


    @SuppressLint("SimpleDateFormat")
    public static String getStringFromDate(Date date) {
        return getStringFromDate(date, Constants.FULL_DATE_FORMAT);
    }

    @SuppressLint("SimpleDateFormat")
    public static String getStringFromDate(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

    public static int getAgeFromDate(Date birthDate) {
        Calendar dob = Calendar.getInstance();
        dob.setTime(birthDate);
        Calendar today = Calendar.getInstance();

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        return age;
    }
}
