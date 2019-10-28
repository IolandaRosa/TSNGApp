package com.example.tsngapp.view_managers;

import com.example.tsngapp.helpers.ErrorCode;
import com.example.tsngapp.helpers.ErrorValidator;
import com.example.tsngapp.model.DataToSend;
import com.example.tsngapp.model.UserGender;
import com.example.tsngapp.model.UserType;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RegisterManager {

    private final String LOG_TAG = "RegisterManager";

    private static final RegisterManager ourInstance = new RegisterManager();

    public static RegisterManager getInstance() {
        return ourInstance;
    }

    private RegisterManager() {
    }

    public DataToSend generateJsonElderForPost(String elderName,
                                               String elderBirthDate,
                                               UserGender elderGender){

        DataToSend dataToSend = new DataToSend();

        if(ErrorValidator.getInstance().isEmpty(elderName)){
            dataToSend.addErrorCode(ErrorCode.ELDER_NAME_EMPTY);
        }

        if(ErrorValidator.getInstance().isEmpty(elderBirthDate)){
            dataToSend.addErrorCode(ErrorCode.ELDER_BIRTH_DATE_EMPTY);
        }
        else{
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try{
                Date convertedDate = dateFormat.parse(elderBirthDate);

                Date actualDate = new Date();

                long diff = actualDate.getTime()-convertedDate.getTime();

                long years = diff/1000/60/60/24/365;

                if(years<0 || years>200){
                    dataToSend.addErrorCode(ErrorCode.ELDER_BIRTH_DATE_INAVLID_DATE);
                }

            }catch (Exception ex){
                dataToSend.addErrorCode(ErrorCode.ELDER_BIRTH_DATE_INVALID_FORMAT);
            }

        }
        if (elderGender == UserGender.UNDEFINED) {
            dataToSend.addErrorCode(ErrorCode.GENDER_UNDEFINED);
        }

        if (dataToSend.getErrorCodes().size() > 0) {
            return dataToSend;
        }




        try {
            dataToSend.getJsonObject().put("name", elderName);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            Date convertedDate = dateFormat.parse(elderBirthDate);

            dataToSend.getJsonObject().put("birth_date", convertedDate.toString());

            if(elderGender==UserGender.FEMALE){
                dataToSend.getJsonObject().put("gender", "F");
            }else if(elderGender == UserGender.MALE){
                dataToSend.getJsonObject().put("gender", "M");
            }

            dataToSend.getJsonObject().put("photo_url", "");


        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataToSend;
    }


    public DataToSend generateJsonForPost(String name,
                                          String username,
                                          String email,
                                          String password,
                                          String passwordConf) {

        DataToSend dataToSend = new DataToSend();

        if (ErrorValidator.getInstance().isEmpty(name)) {
            dataToSend.addErrorCode(ErrorCode.NAME_EMPTY);
        }

        if (ErrorValidator.getInstance().isEmpty(username)) {
            dataToSend.addErrorCode(ErrorCode.USERNAME_EMPTY);
        }

        if (ErrorValidator.getInstance().isEmpty(email)) {
            dataToSend.addErrorCode(ErrorCode.EMAIL_EMPTY);
        }

        if (ErrorValidator.getInstance().isEmpty(password)) {
            dataToSend.addErrorCode(ErrorCode.PASSWORD_EMPTY);
        }

        if (ErrorValidator.getInstance().isEmpty(passwordConf)) {
            dataToSend.addErrorCode(ErrorCode.PASSWORD_CONF_EMPTY);
        }

        if (!password.equals(passwordConf)) {
            dataToSend.addErrorCode(ErrorCode.PASSWORD_DONT_MATCH);
        }

        if (dataToSend.getErrorCodes().size() > 0) {
            return dataToSend;
        }

        try {
            dataToSend.getJsonObject().put("name", name);
            dataToSend.getJsonObject().put("username", username);
            dataToSend.getJsonObject().put("email", email);
            dataToSend.getJsonObject().put("password", password);
            dataToSend.getJsonObject().put("type", "normal");



        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dataToSend;
    }
}
