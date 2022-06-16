package com.example.travelguidapplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {

    private HashMap<String,String>getSingleNearbyPlace(JSONObject googlePlaceJSON){
        HashMap<String,String> googlePlaceMap= new HashMap<>();
        String placeId ="-NA-";
        String NameOfPlace ="-NA-";
        String vicinity ="-NA-";
        String latitude ="0";
        String longitude ="0";
        String reference ="0";
        String rating="0";
        String photo="";
        try
        {
            //fetching the data

            if (!googlePlaceJSON.isNull("place_id"))
            {
                placeId =googlePlaceJSON.getString("place_id");
            }

            if (!googlePlaceJSON.isNull("name"))
            {
                NameOfPlace =googlePlaceJSON.getString("name");
            }

            if (!googlePlaceJSON.isNull("vicinity"))
            {
                vicinity =googlePlaceJSON.getString("vicinity");
            }

            if (!googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").isNull("lat"))
            {
                latitude=googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lat");
            }
            if (!googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").isNull("lng"))
            {
                longitude=googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lng");
            }
            if (!googlePlaceJSON.isNull("reference"))
            {
                reference =googlePlaceJSON.getString("reference");
            }
            if (!googlePlaceJSON.isNull("rating"))
            {
                rating =googlePlaceJSON.getString("rating");
            }

            if (!googlePlaceJSON.isNull("icon"))
            {
                photo =googlePlaceJSON.getString("icon");
            }
            /*if (!googlePlaceJSON.isNull("photos")) {

                JSONArray photos = googlePlaceJSON.getJSONArray("photos");

                for (int i = 0; i <= photos.length(); i++) {
                    JSONObject getPhtotos = photos.getJSONObject(i);
                    photo = getPhtotos.getString("photo_reference");
                }
            }else
            {
                photo =googlePlaceJSON.getString("icon");
            }*/

            googlePlaceMap.put("place_id",placeId);
            googlePlaceMap.put("name",NameOfPlace);
            googlePlaceMap.put("vicinity",vicinity);
            googlePlaceMap.put("lat",latitude);
            googlePlaceMap.put("lng",longitude);
            googlePlaceMap.put("reference",reference);
            googlePlaceMap.put("rating",rating);
            googlePlaceMap.put("photo_reference",photo);

        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        return googlePlaceMap;
    }

    private List<HashMap<String,String>>getAllNearbyPlaces(JSONArray jsonArray){
        int counter = jsonArray.length();
        List<HashMap<String,String>>NearbyPlacesList = new ArrayList<>();
        HashMap<String,String>NearbyPlaceMap = null;

        for (int i=0; i<counter; i++){
            try
            {
                NearbyPlaceMap = getSingleNearbyPlace((JSONObject) jsonArray.get(i));
                NearbyPlacesList.add(NearbyPlaceMap);


            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return NearbyPlacesList;

    }


    public List<HashMap<String,String>>parse(String JSONdata)
    {
        JSONArray jsonArray = null;
        JSONObject jsonObject;


        try
        {
            jsonObject= new JSONObject(JSONdata);
            jsonArray =jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getAllNearbyPlaces(jsonArray);
    }


}
