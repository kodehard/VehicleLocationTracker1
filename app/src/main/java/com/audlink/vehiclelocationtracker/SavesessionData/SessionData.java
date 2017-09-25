package com.audlink.vehiclelocationtracker.SavesessionData;

import android.content.Context;
import android.content.SharedPreferences;

import com.audlink.vehiclelocationtracker.UserLoginDataModel;
import com.audlink.vehiclelocationtracker.VLocationApplication;
import com.google.gson.Gson;

/**
 * Created by sridhar on 3/2/2017.
 */

public class SessionData {
    private static SessionData sessionData;
    public static String KConnect = "KConnect";
    public static String SHPREF_KEY_LOGIN_RESPONSE = "SHPREF_KEY_LOGIN_RESPONSE";
    private SessionData() {}
    public static SessionData getSessionDataInstance() {
        if (sessionData == null) {
            sessionData = new SessionData();
        }
        return sessionData;
    }
    public static void saveLoginResponse(UserLoginDataModel loginResultModel) {
        SharedPreferences.Editor e = VLocationApplication.getAppContext().getSharedPreferences(KConnect, Context.MODE_PRIVATE).edit();
        if (loginResultModel != null) {
            Gson gson = new Gson();
            String json = gson.toJson(loginResultModel);
            e.putString(SHPREF_KEY_LOGIN_RESPONSE, json);
        } else {
            e.putString(SHPREF_KEY_LOGIN_RESPONSE, null);
        }
        e.commit();
    }
    public static UserLoginDataModel getLoginResult() {
        Gson gson = new Gson();
        String json = VLocationApplication.getAppContext().getSharedPreferences(KConnect, Context.MODE_PRIVATE).getString(SHPREF_KEY_LOGIN_RESPONSE, null);
        UserLoginDataModel loginResultModel = gson.fromJson(json, UserLoginDataModel.class);
        return loginResultModel;
    }
}
