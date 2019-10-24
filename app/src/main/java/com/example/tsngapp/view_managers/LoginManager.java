package com.example.tsngapp.view_managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.tsngapp.helpers.Constants;
import com.example.tsngapp.helpers.ErrorCode;
import com.example.tsngapp.helpers.ErrorValidator;
import com.example.tsngapp.model.DataToSend;

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
     * Metodo para salvar o token de utilizador nas shared prefrences
     * @param token
     * @param context
     */

    public void saveAuthToken(String token, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.TOKEN_KEY,token);
        editor.apply();
    }

    /**
     * Metodo para ir buscar o token de utilizador às Shared Preferences
     * @param context
     * @return
     */
    public String retrieveAuthToken(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Constants.TOKEN_KEY,"");
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
}
