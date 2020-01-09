package com.example.tsngapp.view_managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.tsngapp.helpers.Constants;
import com.example.tsngapp.helpers.ErrorCode;
import com.example.tsngapp.helpers.ErrorValidator;
import com.example.tsngapp.model.DataToSend;
import com.example.tsngapp.model.Elder;
import com.example.tsngapp.model.User;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginManager {
    private final String LOG_TAG="LoginManager";

    private static final LoginManager ourInstance = new LoginManager();

    public static LoginManager getInstance() {
        return ourInstance;
    }

    private LoginManager() {
    }

    /**
     *
     * Metodo que permite gerar JSON para POST atraves dos dados da vista
     * @param username
     * @param password
     * @return JSONObject
     */
    public DataToSend generateJsonForPost(String username, String password) {

        DataToSend dataToSend = new DataToSend();

        if(ErrorValidator.getInstance().isEmpty(username)){
            dataToSend.addErrorCode(ErrorCode.EMAIL_EMPTY);
        }

        if (ErrorValidator.getInstance().isEmpty(password)) {
            dataToSend.addErrorCode(ErrorCode.PASSWORD_EMPTY);
        }

        if(dataToSend.getErrorCodes().size() > 0){
            return dataToSend;
        }

        try {

            if (android.util.Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
                dataToSend.getJsonObject().put("email", username);
            }
            else{
                dataToSend.getJsonObject().put("username",username);
            }

            dataToSend.getJsonObject().put("password",password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dataToSend;
    }

    /**
     * Used to save authentication information (user, elder & token) on Shared Preferences
     * @param user
     * @param elder
     * @param token
     * @param context
     * @return LoginManager
     */
    public LoginManager saveAuthInfo(String token, User user, Elder elder, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.APP_TAG, Context.MODE_PRIVATE);
        final Gson gson = new Gson();
        sharedPreferences
                .edit()
                .putString(Constants.SP_TOKEN_KEY, token)
                .putString(Constants.SP_USER_KEY, gson.toJson(user))
                .putString(Constants.SP_ELDER_KEY, gson.toJson(elder))
                .apply();
        return this;
    }

    /**
     * Used to clear authentication information (user, elder & token) from Shared Preferences
     * @param context
     */
    public void clearAuthenticationInfo(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.APP_TAG, Context.MODE_PRIVATE);
        sharedPreferences
                .edit()
                .remove(Constants.SP_TOKEN_KEY)
                .remove(Constants.SP_USER_KEY)
                .remove(Constants.SP_ELDER_KEY)
                .apply();
    }

    /**
     * Used to save a user Object on Shared Preferences
     * @param user
     * @param context
     * @return LoginManager
     */
    public LoginManager saveUserInfo(User user, Context context) {
        saveSharedPreferencesString(Constants.SP_USER_KEY, new Gson().toJson(user), context);
        return this;
    }

    /**
     * Used to save an Elder object on Shared Preferences
     * @param elder
     * @param context
     * @return LoginManager
     */
    public LoginManager saveElderInfo(Elder elder, Context context) {
        saveSharedPreferencesString(Constants.SP_ELDER_KEY, new Gson().toJson(elder), context);
        return this;
    }

    /**
     * Metodo para salvar o token de utilizador nas shared prefrences
     * @param token
     * @param context
     * @return LoginManager
     */
    public LoginManager saveAuthToken(String token, Context context) {
        saveSharedPreferencesString(Constants.SP_TOKEN_KEY, token, context);
        return this;
    }

    /**
     * Metodo para ir buscar o token de utilizador às Shared Preferences
     * @param context
     * @return
     */
    public String retrieveAuthToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.APP_TAG, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Constants.SP_TOKEN_KEY,"");
    }

    public User retrieveUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.APP_TAG, Context.MODE_PRIVATE);
        final String userJson = sharedPreferences.getString(Constants.SP_USER_KEY, null);
        try {
            return new Gson().fromJson(userJson, User.class);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

    public Elder retrieveElder(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.APP_TAG, Context.MODE_PRIVATE);
        final String elderJson = sharedPreferences.getString(Constants.SP_ELDER_KEY, null);
        try {
            return new Gson().fromJson(elderJson, Elder.class);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

    public String getTokenFromJson(String jsonString) {
        String token = "";

        try{
            JSONObject jsonResponse = new JSONObject(jsonString);

            token = jsonResponse.getString("access_token");

        }
        catch(Exception ex){
            Log.d(LOG_TAG,"Error during json parsing "+ex.getMessage());
        }

        return token;
    }

    public LoginManager removeTokenFromSP(Context context) {
        removeFromSharedPreferences(Constants.SP_TOKEN_KEY, context);
        return this;
    }

    /**
     * Used to remove a certain key from Shared Preferences
     * @param key
     * @param context
     */
    private void removeFromSharedPreferences(String key, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.APP_TAG, Context.MODE_PRIVATE);
        sharedPreferences
                .edit()
                .remove(key)
                .apply();
    }

    /**
     * Used to save a string on Shared Preferences
     * @param key
     * @param value
     * @param context
     */
    private void saveSharedPreferencesString(String key, String value, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.APP_TAG, Context.MODE_PRIVATE);
        sharedPreferences
                .edit()
                .putString(key, value)
                .apply();
    }
}
