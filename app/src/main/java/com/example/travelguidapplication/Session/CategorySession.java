package com.example.travelguidapplication.Session;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class CategorySession {

    SharedPreferences sharedPreferences;

    SharedPreferences.Editor editor;

    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_SESSION = "category_session";

    public static final String KEY_CATEGORYID = "categoryId";

    public static final String KEY_CATEGORYNAME = "categoryName";

    public CategorySession(Context context){
        this._context = context;
        sharedPreferences = _context.getSharedPreferences(PREF_SESSION, PRIVATE_MODE);
        editor = sharedPreferences.edit();

    }

    public void writeCategoryDetails(String categoryId,String categoryName){

        editor.putString(KEY_CATEGORYID, categoryId);
        editor.putString(KEY_CATEGORYNAME, categoryName);

        editor.commit();
    }


    public HashMap<String, String> readCategoryDetails(){
        HashMap<String, String> category = new HashMap<String, String>();

        category.put(KEY_CATEGORYID, sharedPreferences.getString(KEY_CATEGORYID,null));
        category.put(KEY_CATEGORYNAME, sharedPreferences.getString(KEY_CATEGORYNAME,null));

        return category;
    }

    public void clearCategorySession(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

    }
}
