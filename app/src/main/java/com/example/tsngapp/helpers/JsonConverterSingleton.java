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

    public User jsonToUser(String jsonUser, Boolean isRegister){
        User user=null;

        try{

            JSONObject userObject = new JSONObject(jsonUser);

            if(!isRegister){
                userObject = userObject.getJSONObject("data");
            }

            int elder_id=-1;

            try{
                elder_id = userObject.getInt("elder_id");
            }
            catch (Exception e){
                e.printStackTrace();
            }

            user = new User(
                    userObject.getInt("id"),
                    userObject.getString("name"),
                    userObject.getString("username"),
                    userObject.getString("email"),
                    userObject.getString("type"),
                    elder_id //se for -1 é uma situação de registo
            );
        }
        catch (Exception ex){
            Log.d(LOG_TAG,"Exception while parsing json to user");
        }

        return user;
    }
}
