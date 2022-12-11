package com.patel.tanmay.assignment_login.interfaces;

import com.android.volley.NetworkResponse;

import org.json.JSONObject;

public interface CallBack {
    void responseCallback(JSONObject response);
    void errorCallback(JSONObject error_message);
    void responseStatus(NetworkResponse response_code);
}
