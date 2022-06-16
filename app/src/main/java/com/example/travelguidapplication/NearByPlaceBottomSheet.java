package com.example.travelguidapplication;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelguidapplication.Adapter.NearByPlaceAdapter;
import com.example.travelguidapplication.Interface.NearByPlaceClickListener;
import com.example.travelguidapplication.Model.NearByPlace;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;

public class NearByPlaceBottomSheet extends BottomSheetDialogFragment {

    private double latitude,longitude;
    private RecyclerView recyclerView_nearByPlace;
    private BottomSheetDialog bottomSheetDialog;
    private int ProximityRadius = 10000;
    private ArrayList<NearByPlace> nearByPlaceListProp;
    private ArrayList<NearByPlace> nearByPlaceList;
    private NearByPlaceClickListener nearByPlaceClickListener;
    private Button btn_addNearByPlaces;
    private NearByPlaceAdapter nearByPlaceAdapter;

    private String googlePlaceData,url;
    public NearByPlaceBottomSheet(double latitude, double longitude, NearByPlaceClickListener nearByPlaceClickListener){
        this.latitude=latitude;
        this.longitude=longitude;
        this.nearByPlaceClickListener=nearByPlaceClickListener;

    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(STYLE_NORMAL, R.style. CustomBottomSheetDialogTheme);

    }

    @SuppressLint("ResourceType")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        bottomSheetDialog= (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view= LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_layout_place_category,null);

        recyclerView_nearByPlace = (RecyclerView) view.findViewById(R.id.nearPlaceList);
        btn_addNearByPlaces=view.findViewById(R.id.buttonAddNearByPlaces);
        TextView btn_close = (TextView) view.findViewById(R.id.btnClose);

        nearByPlaceListProp=new ArrayList<>();
        nearByPlaceList=new ArrayList<>();

        Chip nearRestaurant = (Chip) view.findViewById(R.id.nearByResturant);
        Chip nearHospital = (Chip) view.findViewById(R.id.nearByHospitals);
        Chip nearATM = (Chip) view.findViewById(R.id.nearByATM);
        Chip nearGasStation = (Chip) view.findViewById(R.id.nearByGasStation);
        Chip nearSuperMarket = (Chip) view.findViewById(R.id.nearBySuperMarket);
        Chip nearHotels = (Chip) view.findViewById(R.id.nearByHotel);
        Chip nearPharmacies = (Chip) view.findViewById(R.id.nearByPharmacies);
        Chip nearCarWash = (Chip) view.findViewById(R.id.nearByCarWash);
        Chip nearSalons = (Chip) view.findViewById(R.id.nearBySalons);



        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetDialog.cancel();

            }
        });

        nearHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hospital = "hospital";

                NearBySearch(hospital,latitude,longitude);

            }
        });

        nearRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String restaurant = "restaurant";
                NearBySearch(restaurant,latitude,longitude);


            }
        });

        nearATM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String atm = "atm";
                NearBySearch(atm,latitude,longitude);


            }
        });

        nearGasStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String gas_station = "gas_station";
                NearBySearch(gas_station,latitude,longitude);

            }
        });

        nearSuperMarket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String supermarket = "supermarket";
                NearBySearch(supermarket,latitude,longitude);

            }
        });

        nearHotels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String hotel = "hotel";
                NearBySearch(hotel,latitude,longitude);

            }
        });

        nearPharmacies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String pharmacy = "pharmacy";
                NearBySearch(pharmacy,latitude,longitude);

            }
        });

        nearCarWash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String car_wash = "car_wash";
                NearBySearch(car_wash,latitude,longitude);

            }
        });

        nearSalons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String beauty_salon = "beauty_salon";
                NearBySearch(beauty_salon,latitude,longitude);

            }
        });

        btn_addNearByPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if (nearByPlaceAdapter.getSelected().size() > 0) {

                        for (int i = 0; i < nearByPlaceAdapter.getSelected().size(); i++) {

                            nearByPlaceList.add(nearByPlaceAdapter.getSelected().get(i));
                        }
                        nearByPlaceClickListener.clickItem(nearByPlaceList);


                        int blue = Color.parseColor("#2f3ced");
                        new StyleableToast.Builder(getContext())
                                .text("Place Added Successfully")
                                .backgroundColor(blue)
                                .solidBackground()
                                .textColor(Color.WHITE)
                                .gravity(Gravity.TOP)
                                .cornerRadius(50)
                                .textSize(12)
                                .show();

                    } else {
                        int blue = Color.parseColor("#2f3ced");
                        new StyleableToast.Builder(getContext())
                                .text("Places Not Selected")
                                .backgroundColor(blue)
                                .solidBackground()
                                .textColor(Color.WHITE)
                                .gravity(Gravity.TOP)
                                .cornerRadius(50)
                                .textSize(12)
                                .show();

                    }
                    bottomSheetDialog.cancel();


                }

        });

        bottomSheetDialog.setContentView(view);

        return bottomSheetDialog;
    }

    private void NearBySearch(String nearByPlace,double latitude, double longitude) {

        Object transferData[] = new Object[2];
        GetNearByPlacesBottomSheet nearByPlaces = new GetNearByPlacesBottomSheet();

        String url = getUrl(latitude, longitude, nearByPlace);
        transferData[0] = recyclerView_nearByPlace;
        transferData[1] = url;

        nearByPlaces.execute(transferData);

    }
    private String getUrl(double latitude, double longitude, String nearByPlaces) {
        StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleURL.append("&location=" + latitude + "," + longitude);
        googleURL.append("&radius=" + ProximityRadius);
        googleURL.append("&type=" + nearByPlaces);
        googleURL.append("&sensor=true");
        googleURL.append("&key=" + "AIzaSyDm7o4BAHQUJ0IPimPXH5H8A3JmKUMPiBk");

        return googleURL.toString();

    }

    public class GetNearByPlacesBottomSheet extends AsyncTask<Object,String,String> {

        @Override
        protected String doInBackground(Object... objects)
        {
            recyclerView_nearByPlace=(RecyclerView) objects[0];
            url=(String)objects[1];

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
            recyclerView_nearByPlace.setLayoutManager(new LinearLayoutManager(getContext()));
            nearByPlaceAdapter=new NearByPlaceAdapter(nearByPlaceListProp,getContext());
            recyclerView_nearByPlace.setAdapter(nearByPlaceAdapter);

        }


    }
}
