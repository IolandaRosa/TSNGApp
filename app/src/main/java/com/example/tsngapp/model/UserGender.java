package com.example.tsngapp.model;

public enum UserGender {
    FEMALE, MALE, UNDEFINED;

    public static UserGender valueOfAbreviature(String gender) {
        if (gender.equals("F")) return FEMALE;
        if (gender.equals("M")) return MALE;
        return UNDEFINED;
    }
}
