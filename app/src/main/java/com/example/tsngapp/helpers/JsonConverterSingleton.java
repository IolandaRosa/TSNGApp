package com.example.tsngapp.helpers;

import android.util.Log;

import com.example.tsngapp.model.User;

import org.json.JSONObject;

public class JsonConverterSingleton {
    private static final JsonConverterSingleton ourInstance = new JsonConverterSingleton();
    private static final String LOG_TAG = "JsonConverterSingleton";

    public static JsonConverterSingleton getInstance() {
        return ourInstance;
    }

    private JsonConverterSingleton() {
    }

    public User jsonToUser(String jsonUser){
        User user=null;

        try{

            JSONObject jsonObject = new JSONObject(jsonUser);

            JSONObject userObject = jsonObject.getJSONObject("data");

            user = new User(
                    userObject.getInt("id"),
                    userObject.getString("name"),
                    userObject.getString("username"),
                    userObject.getString("email"),
                    userObject.getString("type"),
                    userObject.getInt("elder_id")
            );
        }
        catch (Exception ex){
            Log.d(LOG_TAG,"Exception while parsing json to user");
        }

        return user;
    }
}
