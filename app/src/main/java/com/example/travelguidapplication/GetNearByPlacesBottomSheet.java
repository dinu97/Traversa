package com.example.travelguidapplication;

import android.content.Context;
import android.os.AsyncTask;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelguidapplication.Adapter.NearByPlaceAdapter;
import com.example.travelguidapplication.Interface.NearByPlaceClickListener;
import com.example.travelguidapplication.Model.NearByPlace;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GetNearByPlacesBottomSheet extends AsyncTask <Object,String,String> {

    private String googlePlaceData,url;
    private RecyclerView recyclerView_nearByPlace;
    private NearByPlaceAdapter nearByPlaceAdapter;
    private Context context;
    private BottomSheetDialog bottomSheetDialog;
    private NearByPlaceClickListener nearByPlaceClickListener;

    private ArrayList<NearByPlace> nearByPlaceListProp = new ArrayList<>();
    public GetNearByPlacesBottomSheet(Context context){
        this.context=context;
    }

    @Override
    protected String doInBackground(Object... objects)
    {
        recyclerView_nearByPlace=(RecyclerView) objects[0];
        url=(String)objects[1];
        nearByPlaceClickListener=(NearByPlaceClickListener) objects[2];
        bottomSheetDialog=(BottomSheetDialog) objects[3];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googlePlaceData = downloadUrl.ReadTheURL(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlaceData;
    }


    @Override
    protected void onPostExecute(String s)
    {
        List<HashMap<String,String>> nearByPlaceList= null;
        DataParser dataParser = new DataParser();
        nearByPlaceList = dataParser.parse(s);

        for (int i=0; i<nearByPlaceList.size();i++) {
            HashMap<String, String> googleNearbyPlace = nearByPlaceList.get(i);
            String nearByPlaceId= googleNearbyPlace.get("place_id");
            String nearByPlaceName = googleNearbyPlace.get("name");
            String nearByPlaceRating = googleNearbyPlace.get("rating");
            String nearByPlaceLatitude =googleNearbyPlace.get("lat");
            String nearByPlaceLongitude = googleNearbyPlace.get("lng");
            String nearByPlaceImage = googleNearbyPlace.get("photo_reference");
            NearByPlace nearByPlace=new NearByPlace(nearByPlaceId,nearByPlaceName,nearByPlaceRating,nearByPlaceLatitude,nearByPlaceLongitude,nearByPlaceImage);
            nearByPlaceListProp.add(nearByPlace);
        }

        recyclerView_nearByPlace.setHasFixedSize(true);
        recyclerView_nearByPlace.setLayoutManager(new LinearLayoutManager(context));
        nearByPlaceAdapter=new NearByPlaceAdapter(nearByPlaceListProp, context);
        recyclerView_nearByPlace.setAdapter(nearByPlaceAdapter);

    }


}
