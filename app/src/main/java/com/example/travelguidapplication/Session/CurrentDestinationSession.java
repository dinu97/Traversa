package com.example.travelguidapplication.Session;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class CurrentDestinationSession {

    SharedPreferences sharedPreferences;

    SharedPreferences.Editor editor;

    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_SESSION = "current_destination_session";

    public static final String KEY_CURRENTLATITUDE = "currentLatitude";
    public static final String KEY_CURRENTLONGITUDE = "currentLongitude";

    public CurrentDestinationSession(Context context){
        this._context = context;
        sharedPreferences = _context.getSharedPreferences(PREF_SESSION, PRIVATE_MODE);
        editor = sharedPreferences.edit();

    }

    public void writeCurrentDestination(String currentLatitude,String currentLongitude){


        editor.putString(KEY_CURRENTLATITUDE, currentLatitude);
        editor.putString(KEY_CURRENTLONGITUDE, currentLongitude);

        editor.commit();
    }


    public HashMap<String, String> readCurrentDestination(){
        HashMap<String, String> place = new HashMap<String, String>();

        place.put(KEY_CURRENTLATITUDE, sharedPreferences.getString(KEY_CURRENTLATITUDE,null));
        place.put(KEY_CURRENTLONGITUDE, sharedPreferences.getString(KEY_CURRENTLONGITUDE,null));

        return place;
    }

    public void clearCurrentDestination(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

    }
}
