package com.example.travelguidapplication;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelguidapplication.Adapter.LocationInsideAdapter;
import com.example.travelguidapplication.Interface.NearByPlaceClickListener;
import com.example.travelguidapplication.Model.NearByPlace;
import com.example.travelguidapplication.Session.LocationSession;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TravelPlanActivity extends AppCompatActivity implements CalendarBottomSheet.BottomSheetListener{

    private String placeKey,placeName,userId;
    private TextInputEditText txt_date,txt_time;
    private MaterialTimePicker picker;
    private ArrayList<LatLng> placeList =new ArrayList<>();
    private Button btn_saveTravelPlan;
    private LocationAdapter locationAdapter;
    private double placeLatitude,placeLongitude;
    private RecyclerView recyclerView_locationList;
    private TextInputLayout txt_layoutDate,txt_layoutTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_plan);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser refUser = firebaseAuth.getCurrentUser();
        userId = refUser.getUid();

        txt_date = (TextInputEditText) findViewById(R.id.textInputEditTextDate);
        txt_time = (TextInputEditText) findViewById(R.id.textInputEditTextTime);
        btn_saveTravelPlan = (Button) findViewById(R.id.btnSaveTravelPlan);
        recyclerView_locationList = (RecyclerView) findViewById(R.id.locationList);
        txt_layoutDate = (TextInputLayout) findViewById(R.id.textInputLayoutDate);
        txt_layoutTime = (TextInputLayout) findViewById(R.id.textInputLayoutTime);


        placeList = LocationSession.readLocation(TravelPlanActivity.this);

        txt_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                selectDate();

            }
        });

        txt_time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                picker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_12H)
                        .setHour(12)
                        .setMinute(0)
                        .build();

                picker.show(getSupportFragmentManager(), "");

                picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (picker.getHour() > 12) {
                            txt_time.setText(String.format("%02d", (picker.getHour() - 12)) + ":" + String.format("%02d", picker.getMinute()) + " PM");

                        } else {
                            txt_time.setText(String.format("%02d", (picker.getHour())) + ":" + String.format("%02d", picker.getMinute()) + " AM");

                        }


                    }
                });

            }

        });

        txt_date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence searchText, int start, int before, int count) {
                //if there is some letter in the input field
                txt_layoutDate.setError(null);
                txt_date.setBackground(getDrawable(R.drawable.textinputedittext_background));

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txt_time.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence searchText, int start, int before, int count) {
                txt_layoutTime.setError(null);
                txt_time.setBackground(getDrawable(R.drawable.textinputedittext_background));

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(TravelPlanActivity.this);
        recyclerView_locationList.setLayoutManager(manager);
        recyclerView_locationList.setHasFixedSize(true);

        locationAdapter=new LocationAdapter(placeList,TravelPlanActivity.this);
        recyclerView_locationList.setAdapter(locationAdapter);

        btn_saveTravelPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validateDate() | !validateTime()) {
                    return;
                }
                else {

                    for (int i = 0; i < placeList.size(); i++) {

                        LatLng placeLatLng = new LatLng(placeList.get(i).latitude, placeList.get(i).longitude);
                        Address address = getAddressFromLatLng(placeLatLng);
                        placeName = address.getAddressLine(0);
                        placeLatitude = placeList.get(i).latitude;
                        placeLongitude = placeList.get(i).longitude;
                        SavePlaces(placeName, placeLatitude, placeLongitude);
                    }

                    Intent intent = new Intent(TravelPlanActivity.this, PlaceOrderActivity.class);
                    intent.putExtra("startDate", txt_date.getText().toString());
                    startActivity(intent);
                }

            }


        });
 }

    private boolean validateTime() {

        if (TextUtils.isEmpty(txt_time.getText())) {
            txt_layoutTime.setError("Please select a time");
            txt_time.requestFocus();
            return false;
        } else {
            txt_layoutTime.setError(null);
            return true;
        }

    }

    private boolean validateDate() {

        if (TextUtils.isEmpty(txt_date.getText())) {
            txt_layoutDate.setError("Please select a date");
            txt_date.requestFocus();
            return false;
        } else {
            txt_layoutDate.setError(null);
            return true;
        }

    }

    private void SavePlaces(String placeName, double placeLatitude, double placeLongitude) {

        RandomString randomPlaceKey = new RandomString(21);
        placeKey = randomPlaceKey.nextString();

        HashMap<String, Object> placeDataHashMap = new HashMap<>();
        placeDataHashMap.put("placeKey",placeKey);
        placeDataHashMap.put("placeId","0");
        placeDataHashMap.put("placeName",placeName);
        placeDataHashMap.put("placeLatitude",placeLatitude);
        placeDataHashMap.put("placeLongitude",placeLongitude);
        placeDataHashMap.put("startTime",txt_time.getText().toString());
        placeDataHashMap.put("startDate",txt_date.getText().toString());
        placeDataHashMap.put("time","0");
        placeDataHashMap.put("distance","0");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("Users").child(userId).child("TravelPlans").child(placeKey).setValue(placeDataHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });

    }

    private void selectDate() {

        CalendarBottomSheet calendarBottomSheet=new CalendarBottomSheet();
        calendarBottomSheet.show(getSupportFragmentManager(),calendarBottomSheet.getTag());

    }

    @Override
    public void onButtonCLiked(String text) {
        txt_date.setText(text);

    }

    private Address getAddressFromLatLng(LatLng latLng){
        Geocoder geocoder=new Geocoder(TravelPlanActivity.this);
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 5);
            if(addresses!=null){
                Address address=addresses.get(0);
                return address;
            }
            else{
                return null;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder>  {

        private Context context;
        private ArrayList<LatLng> locationList;
        private LocationInsideAdapter locationInsideAdapter;
        private ArrayList<NearByPlace> nearByPlaceList;
        public LocationAdapter(ArrayList<LatLng> locationList, Context context) {
            this.locationList=locationList;
            this.context=context;
        }

        @NonNull
        @Override
        public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_location,parent,false);

            return new LocationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {

            nearByPlaceList=new ArrayList<NearByPlace>();
            LatLng latLng=locationList.get(position);

            holder.txt_locationPlaceName.setText(getAddressFromLatLng(latLng).getAddressLine(0));

            holder.btn_addNearByPlaces.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                       NearByPlaceBottomSheet nearByPlaceBottomSheet=new NearByPlaceBottomSheet(latLng.latitude,latLng.longitude, new NearByPlaceClickListener() {
                            @Override
                            public void clickItem(ArrayList<NearByPlace> nearByPlace) {

                                nearByPlaceList = (ArrayList)nearByPlace.clone();
                                LinearLayoutManager manager = new LinearLayoutManager(context);
                                holder.recyclerView_nearByPlaceList.setLayoutManager(manager);
                                holder.recyclerView_nearByPlaceList.setHasFixedSize(true);
                                locationInsideAdapter=new LocationInsideAdapter(nearByPlaceList,context);
                                holder.recyclerView_nearByPlaceList.setAdapter(locationInsideAdapter);

                                for (int i=0;i<nearByPlaceList.size();i++)
                                {
                                    placeName=nearByPlaceList.get(i).getNearByPlaceName();
                                    placeLatitude= Double.parseDouble(nearByPlaceList.get(i).getNearByPlaceLatitude());
                                    placeLongitude= Double.parseDouble(nearByPlaceList.get(i).getNearByPlaceLongitude());

                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                                    SavePlaces(placeName,placeLatitude,placeLongitude);
                                }
                            }
                        });
                        nearByPlaceBottomSheet.show(((FragmentActivity)context).getSupportFragmentManager(),nearByPlaceBottomSheet.getTag());
                }
            });

        }

        @Override
        public int getItemCount() {
            return locationList.size();
        }

        public class LocationViewHolder extends RecyclerView.ViewHolder {


            public TextView txt_locationPlaceName;
            public FloatingActionButton btn_addNearByPlaces;
            public RecyclerView recyclerView_nearByPlaceList;

            public LocationViewHolder(@NonNull View itemView) {
                super(itemView);

                txt_locationPlaceName = itemView.findViewById(R.id.textViewPlaceName);
                btn_addNearByPlaces = itemView.findViewById(R.id.addNearByPlace);
                recyclerView_nearByPlaceList = itemView.findViewById(R.id.nearByPlaceList);

            }
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
    }
}