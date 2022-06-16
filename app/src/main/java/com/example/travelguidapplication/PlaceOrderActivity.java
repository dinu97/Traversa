package com.example.travelguidapplication;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelguidapplication.Adapter.PlaceOrderAdapter;
import com.example.travelguidapplication.Interface.DirectionFinderListener;
import com.example.travelguidapplication.Model.PlaceOrder;
import com.example.travelguidapplication.Model.Route;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.SphericalUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.Duration;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PlaceOrderActivity extends AppCompatActivity implements DirectionFinderListener {

    private RecyclerView recyclerView_PlaceOrder;
    private String userId,startDate,destinationPlaceName,currentPlaceName,placeKey;
    private int position;
    private ArrayList<PlaceOrder> placeOrderList;
    private ArrayList<PlaceOrder> placeOrderNewList;
    private PlaceOrderAdapter placeOrderAdapter;
    private Button btn_savePlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser refUser = firebaseAuth.getCurrentUser();
        userId = refUser.getUid();

        Intent intent = getIntent();
        startDate = intent.getStringExtra("startDate");

        recyclerView_PlaceOrder = (RecyclerView) findViewById(R.id.recyclerViewPlaceOrder);
        btn_savePlan = (Button) findViewById(R.id.btnSavePlan);

        ShowPlaces(startDate);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView_PlaceOrder.addItemDecoration(dividerItemDecoration);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView_PlaceOrder);

        btn_savePlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeOrderNewList=placeOrderAdapter.getPlaceList();

                for (int k = 0; k <placeOrderNewList.size(); k++ ) {
                    PositionUpdate(placeOrderNewList.get(k).getPlaceKey(), k);
                }

                int size=placeOrderNewList.size()-1;
                for (int i = 0; i <size; i++ ) {

                    int j =i;j++;

                    currentPlaceName=placeOrderNewList.get(i).getPlaceName();
                    destinationPlaceName=placeOrderNewList.get(j).getPlaceName();
                    LatLng currentLatLng=new LatLng(placeOrderNewList.get(i).getPlaceLatitude(),placeOrderNewList.get(i).getPlaceLongitude());
                    LatLng destinationLatLng=new LatLng(placeOrderNewList.get(j).getPlaceLatitude(),placeOrderNewList.get(j).getPlaceLongitude());

                    Location location1 = new Location("");
                    location1.setLatitude(placeOrderNewList.get(i).getPlaceLatitude());
                    location1.setLongitude(placeOrderNewList.get(i).getPlaceLongitude());

                    Location location2 = new Location("");
                    location2.setLatitude(placeOrderNewList.get(j).getPlaceLatitude());
                    location2.setLongitude(placeOrderNewList.get(j).getPlaceLongitude());

                    float distanceInMeters = location1.distanceTo(location2);
                    int speedIs200MetersPerMinute = 200;
                    float estimatedDriveTimeInMinutes = distanceInMeters/speedIs200MetersPerMinute;

                    int hours = (int) (estimatedDriveTimeInMinutes / 60);
                    int minutes = (int) (estimatedDriveTimeInMinutes % 60);

                    double distance=SphericalUtil.computeDistanceBetween(currentLatLng,destinationLatLng);

                    if (distance>1000)
                    {
                        double kmDistance=distance/1000;
                        TimeDistanceUpdate(placeOrderNewList.get(j).getPlaceKey(),String.format("%.1f Km", kmDistance), String.format("%d h:%d min", hours, minutes));

                    }else
                    {
                        TimeDistanceUpdate(placeOrderNewList.get(j).getPlaceKey(), String.format("%.1f m", distance), String.format("%d h:%d min", hours, minutes));
                    }


                }

                Intent intent = new Intent(PlaceOrderActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });

    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {


            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            Collections.swap(placeOrderList, fromPosition, toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };
    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext.setQueryRateLimit(3)
                .setApiKey(getString(R.string.API_KEY))
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }
    public String getDurationForRoute(String origin, String destination)
    {

        DateTime now = new DateTime();
        DirectionsResult result = null;
        try {
            result = DirectionsApi.newRequest(getGeoContext())
                    .mode(TravelMode.DRIVING).origin(origin)
                    .destination(destination).departureTime(now)
                    .await();
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        DirectionsRoute route = result.routes[0];
        DirectionsLeg leg = route.legs[0];
        Duration duration = leg.duration;
        return duration.humanReadable;
    }

    private void PositionUpdate(String placeKey, int i) {

        HashMap<String, Object> positionUpdateHashMap = new HashMap<>();
        positionUpdateHashMap.put("placeId",String.valueOf(i));

        DatabaseReference databaseReferencePosition = FirebaseDatabase.getInstance().getReference();

        databaseReferencePosition.child("Users").child(userId).child("TravelPlans").child(placeKey).updateChildren(positionUpdateHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });


    }


    private void TimeDistanceUpdate(String placeKey, String distance,String time) {

        HashMap<String, Object> distanceTimeDataHashMap = new HashMap<>();
        distanceTimeDataHashMap.put("distance",distance);
        distanceTimeDataHashMap.put("time",time);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("Users").child(userId).child("TravelPlans").child(placeKey).updateChildren(distanceTimeDataHashMap);


    }

    private void ShowPlaces(String startDate) {

        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerView_PlaceOrder.setLayoutManager(manager);
        recyclerView_PlaceOrder.setHasFixedSize(true);

        placeOrderList=new ArrayList<>();
        DatabaseReference placeRef= FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("TravelPlans");

        placeRef.orderByChild("startDate").equalTo(startDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//set of reference markers for data at a particular point in time.
                placeOrderList.clear();
                for (DataSnapshot ds:snapshot.getChildren())
                {

                    PlaceOrder placeOrder=ds.getValue(PlaceOrder.class);
                    placeOrderList.add(placeOrder);

                }


                placeOrderAdapter=new PlaceOrderAdapter(placeOrderList,getApplicationContext());
                recyclerView_PlaceOrder.setAdapter(placeOrderAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onDirectionFinderStart() {

    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes, String placeKey) {

        for (Route route : routes) {

            HashMap<String, Object> distanceTimeDataHashMap = new HashMap<>();
            distanceTimeDataHashMap.put("distance",route.distance.text);
            distanceTimeDataHashMap.put("time",route.duration.text);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

            databaseReference.child("Users").child(userId).child("TravelPlans").child(placeKey).updateChildren(distanceTimeDataHashMap);
        }
    }

    private Address getAddressFromLatLng(LatLng latLng){
        Geocoder geocoder=new Geocoder(PlaceOrderActivity.this);
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

    private LatLng getLatLngFromAddress(String address){

        Geocoder geocoder=new Geocoder(PlaceOrderActivity.this);
        List<Address> addressList;

        try {
            addressList = geocoder.getFromLocationName(address, 1);
            if(addressList!=null){
                Address singleaddress=addressList.get(0);
                LatLng latLng=new LatLng(singleaddress.getLatitude(),singleaddress.getLongitude());
                return latLng;
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
}