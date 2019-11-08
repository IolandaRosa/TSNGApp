package com.example.tsngapp.helpers;

public class Constants {

    //SHARED_PREFS
    public static final String SHARED_PREFS_NAME = "TSNGApp";
    public static final String TOKEN_KEY = "token";

    //API
    private static final String BASE_URL = "http://smartaal.dei.estg.ipleiria.pt/api/";
    public static final String LOGIN_URL = BASE_URL + "login";
    public static final String USERS_ME_URL = BASE_URL + "users/me";
    public static final String REGISTER_URL = BASE_URL + "register";
    public static final String LOGOUT_URL = BASE_URL + "logout";

    // last valye/state format: elderid
    public static final String DOOR_STATE_URL = BASE_URL + "door/%d/last";
    public static final String BED_STATE_URL = BASE_URL + "bed/%d/last";
    public static final String TEMPERATURE_VALUE_URL = BASE_URL + "temperature/%d/last";

    // last values format order: elderid, number of values
    public static final String DOOR_LAST_VALUES_URL = BASE_URL + "door/%d/lastValues/%d";
    public static final String LIGHT_LAST_VALUES_URL = BASE_URL + "light/%d/lastValues/%d";
    public static final String WINDOW_LAST_VALUES_URL = BASE_URL + "window/%d/lastValues/%d";
    public static final String BED_LAST_VALUES_URL = BASE_URL + "bed/%d/lastValues/%d";
    public static final String INTERNAL_TEMP_LAST_VALUES_URL = BASE_URL + "internaltemp/%d/lastValues/%d";
    public static final String ELECTRICAL_CURRENT_LAST_VALUES_URL = BASE_URL + "current/%d/lastValues/%d";

    //Activities
    public static final int REGISTER_ACTIVITY_CODE = 1;
    public static final String INTENT_USER_KEY = "user";
    public static final String INTENT_PASSWORD_KEY = "password";

    //API_RESPONSES
    public static final String HTTP_OK = "Success";
    public static final String HTTP_ERROR = "Error";
}


