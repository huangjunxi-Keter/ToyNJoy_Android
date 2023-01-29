package com.example.toynjoy.entity;

import org.json.JSONArray;
import org.json.JSONObject;

public class ServiceResult {

    public ServiceResult(boolean state) {
        object = new JSONObject();
        array = new JSONArray();
    }

    private JSONObject object;
    private JSONArray array;

    private boolean state;
    private String message;
    private String data;

    public boolean getState() { return state; }

    public String getMessage() {
        return message;
    }

    public String getData() {
        return data;
    }

}
