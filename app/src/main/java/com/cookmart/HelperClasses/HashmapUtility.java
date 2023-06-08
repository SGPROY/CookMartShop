package com.cookmart.HelperClasses;

import android.content.SharedPreferences;

import com.cookmart.SplashScreen;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

public class HashmapUtility {
    public void saveHashMap(String key, Object obj) {
        SharedPreferences prefs = SplashScreen.sharedPreferences;
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(obj);
        editor.putString(key, json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public HashMap<String, HashMap<String, String>> getHashMap(String key) {
        SharedPreferences prefs = SplashScreen.sharedPreferences;
        Gson gson = new Gson();
        String json = prefs.getString(key, "");
        java.lang.reflect.Type type = new TypeToken<HashMap<String, HashMap<String, String>>>() {
        }.getType();
        HashMap<String, HashMap<String, String>> obj = gson.fromJson(json, type);
        return obj;
    }

    public HashMap<String,String> getHashSimple(String key){
        SharedPreferences prefs = SplashScreen.sharedPreferences;
        Gson gson = new Gson();
        String json = prefs.getString(key, "");
        java.lang.reflect.Type type = new TypeToken<HashMap<String,String>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

}
