package com.example.tsngapp.helpers;

public class Constants {

    //SHARED_PREFS
    public static final String SHARED_PREFS_NAME="TSNGApp";
    public static final String TOKEN_KEY = "token";


    //API
    private static final String BASE_URL="http://smartaal.dei.estg.ipleiria.pt/api/";
    public static final String LOGIN_URL= BASE_URL+"login";
    public static  final String USERS_ME_URL = BASE_URL+"users/me";
    public static final String REGISTER_URL = BASE_URL+"register";

    //Activities
    public static final int REGISTER_ACTIVITY_CODE = 1;
    public static final String INTENT_USER_KEY = "user";
    public static final String INTENT_PASSWORD_KEY = "password";
}


