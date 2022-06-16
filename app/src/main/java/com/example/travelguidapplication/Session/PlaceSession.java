package com.example.travelguidapplication.Session;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class PlaceSession {

    SharedPreferences sharedPreferences;

    SharedPreferences.Editor editor;

    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_SESSION = "place_session";

    public static final String KEY_PLACEID = "placeId";
    public static final String KEY_PLACELATITUDE = "placeLatitude";
    public static final String KEY_PLACELONGITUDE = "placeLongitude";


    public PlaceSession(Context context){
        this._context = context;
        sharedPreferences = _context.getSharedPreferences(PREF_SESSION, PRIVATE_MODE);
        editor = sharedPreferences.edit();

    }

    public void writePlaceDetails(String placeId,String placeLatitude,String placeLongitude){

        editor.putString(KEY_PLACEID, placeId);
        editor.putString(KEY_PLACELATITUDE, placeLatitude);
        editor.putString(KEY_PLACELONGITUDE, placeLongitude);

        editor.commit();
    }


    public HashMap<String, String> readPlaceDetails(){
        HashMap<String, String> place = new HashMap<String, String>();

        place.put(KEY_PLACEID, sharedPreferences.getString(KEY_PLACEID,null));
        place.put(KEY_PLACELATITUDE, sharedPreferences.getString(KEY_PLACELATITUDE,null));
        place.put(KEY_PLACELONGITUDE, sharedPreferences.getString(KEY_PLACELONGITUDE,null));

        return place;
    }

    public void clearPlaceSession(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

    }
}
