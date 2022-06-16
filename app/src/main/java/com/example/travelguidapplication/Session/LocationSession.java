package com.example.travelguidapplication.Session;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class LocationSession {

    private static final String PREF_SESSION = "location_session";

    private static final String KEY_LOCATION = "location_list";

    public static void writeLocation(Context context, ArrayList<LatLng> list) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(list);

        SharedPreferences pref = context.getSharedPreferences(PREF_SESSION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_LOCATION, jsonString);
        editor.apply();
    }

    public static ArrayList<LatLng> readLocation(Context context) {

        SharedPreferences pref = context.getSharedPreferences(PREF_SESSION, Context.MODE_PRIVATE);
        String jsonString = pref.getString(KEY_LOCATION, "");

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<LatLng>>() {}.getType();
        ArrayList<LatLng> locationList = gson.fromJson(jsonString, type);
        return locationList;
    }


    public static void clearLocation(Context context){

        SharedPreferences pref = context.getSharedPreferences(PREF_SESSION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.remove(KEY_LOCATION);
        editor.apply();


    }

}