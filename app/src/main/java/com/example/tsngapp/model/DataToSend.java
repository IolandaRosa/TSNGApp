package com.example.tsngapp.model;

import com.example.tsngapp.helpers.ErrorCode;

import org.json.JSONObject;

import java.util.ArrayList;

public class DataToSend {

    private ArrayList<ErrorCode> errorCodes;
    private JSONObject jsonObject;

    public DataToSend() {
        this.errorCodes = new ArrayList<>();
        this.jsonObject = new JSONObject();
    }

    public ArrayList<ErrorCode> getErrorCodes() {
        return errorCodes;
    }

    public void addErrorCode(ErrorCode errorCode) {
        this.errorCodes.add(errorCode);
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }
}
