package com.example.tsngapp.view_managers;

import com.example.tsngapp.helpers.ErrorCode;
import com.example.tsngapp.helpers.ErrorValidator;
import com.example.tsngapp.model.DataToSend;
import com.example.tsngapp.model.UserType;

import org.json.JSONException;

public class RegisterManager {

    private final String LOG_TAG = "RegisterManager";

    private static final RegisterManager ourInstance = new RegisterManager();

    public static RegisterManager getInstance() {
        return ourInstance;
    }

    private RegisterManager() {
    }

    public DataToSend generateJsonForPost(String name,
                                          String username,
                                          String email,
                                          String password,
                                          String passwordConf,
                                          UserType type) {

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

        if (type == UserType.UNDEFINED) {
            dataToSend.addErrorCode(ErrorCode.TYPE_UNDEFINED);
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

            if (type == UserType.ADMIN) {
                dataToSend.getJsonObject().put("type", "admin");
            } else {
                dataToSend.getJsonObject().put("type", "normal");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dataToSend;
    }
}
