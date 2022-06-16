package com.example.travelguidapplication;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.travelguidapplication.Adapter.PlaceAutoSuggestAdapter;
import com.example.travelguidapplication.DirectionHelpers.FetchURL;
import com.example.travelguidapplication.DirectionHelpers.TaskLoadedCallback;
import com.example.travelguidapplication.Session.CurrentDestinationSession;
import com.example.travelguidapplication.Session.LocationSession;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MapActivity extends AppCompatActivity  implements OnMapReadyCallback,TaskLoadedCallback {

    private GoogleMap mGoogleMap;
    private AppPermissions appPermissions;
    private boolean isLocationPermissionOk;
    private Location currentLocation;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private FirebaseAuth firebaseAuth;
    private Marker currentMarker;
    private double searchLongitude,searchLatitude;
    private AutoCompleteTextView txt_destination,txt_current;
    private FloatingActionButton btn_addDestination;
    private LinearLayout destinationList;
    private ArrayList<LatLng> destinationPlaceList;
    private Button btn_findDirection,btn_createTravelPlan;
    private Polyline currentPolyline;
    private MarkerOptions current, destination,waypointsDestination;
    private static final int LOCATION_PERMISSION_CODE=2000;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        txt_destination = (AutoCompleteTextView) findViewById(R.id.txt_destination);
        txt_current = (AutoCompleteTextView) findViewById(R.id.txt_current);
        btn_addDestination = (FloatingActionButton) findViewById(R.id.btnAddDestination);
        destinationList =(LinearLayout) findViewById(R.id.destinationList);
        btn_findDirection =(MaterialButton) findViewById(R.id.btnFindDirection);
        btn_createTravelPlan=(MaterialButton) findViewById(R.id.buttonCreateTravelPlans);

        firebaseAuth = FirebaseAuth.getInstance();
        destinationPlaceList=new ArrayList<>();

        CurrentDestinationSession currentDestinationSession = new CurrentDestinationSession(getApplicationContext());
        HashMap<String, String> currentDestination = currentDestinationSession.readCurrentDestination();
        searchLatitude = Double.parseDouble(currentDestination.get(CurrentDestinationSession.KEY_CURRENTLATITUDE));
        searchLongitude = Double.parseDouble(currentDestination.get(CurrentDestinationSession.KEY_CURRENTLONGITUDE));

        LatLng searchLatLng=new LatLng(searchLatitude,searchLongitude);

        Address address=getAddressFromLatLng(searchLatLng);

        txt_destination.setText(address.getAddressLine(0));

        btn_addDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDestination();
            }
        });

        btn_findDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FindDirection();
            }
        });
        btn_createTravelPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                destinationPlaceList.clear();
                LatLng currentLatLng=getLatLngFromAddress(txt_current.getText().toString());

                LatLng destinationLatLng=new LatLng(searchLatitude, searchLongitude);
                destinationPlaceList.add(currentLatLng);
                destinationPlaceList.add(destinationLatLng);

                if (destinationList!=null) {
                    for (int i = 0; i < destinationList.getChildCount(); i++) {

                        View destinationViewHolder = destinationList.getChildAt(i);

                        AutoCompleteTextView editTextName = (AutoCompleteTextView) destinationViewHolder.findViewById(R.id.txt_addDestination);

                        LatLng latLng=getLatLngFromAddress(editTextName.getText().toString());

                        Address address=getAddressFromLatLng(latLng);

                        LatLng placeLatLng = new LatLng(address.getLatitude(), address.getLongitude());

                        destinationPlaceList.add(placeLatLng);

                    }
                }

                Intent intent1 = new Intent(MapActivity.this,TravelPlanActivity.class);
                LocationSession.writeLocation(MapActivity.this, destinationPlaceList);
                startActivity(intent1);

            }
        });


        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.destinationMap);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        if (ContextCompat.checkSelfPermission(MapActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionOk = true;
            setUpGoogleMap();
        }
        else {
            RequestLocationPermission();
        }
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Waypoints
        String waypoints = "";
        for(int i=0;i<destinationPlaceList.size();i++){
            LatLng point  = (LatLng) destinationPlaceList.get(i);
            if(i==0)
            waypoints = "waypoints=";
            waypoints += point.latitude + "," + point.longitude + "|";

            LatLng wayPointLatLng=new LatLng(point.latitude, point.longitude);
            Address wayPointAddress=getAddressFromLatLng(wayPointLatLng);
            waypointsDestination = new MarkerOptions().position(wayPointLatLng).title(wayPointAddress.getAddressLine(0));
            mGoogleMap.addMarker(waypointsDestination);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(wayPointLatLng));
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(wayPointLatLng, 14));
        }
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode+"&"+waypoints;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.API_KEY);
        return url;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                ((InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
    private void addDestination() {

        final View destinationViewHolder = getLayoutInflater().inflate(R.layout.row_add_destination,null,false);

        FloatingActionButton btnClose = (FloatingActionButton) destinationViewHolder.findViewById(R.id.closeDestination);
        AutoCompleteTextView autoCompleteTextView=(AutoCompleteTextView) destinationViewHolder.findViewById(R.id.txt_addDestination);
        autoCompleteTextView.setAdapter(new PlaceAutoSuggestAdapter(MapActivity.this,android.R.layout.simple_list_item_1));

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d("Address : ",autoCompleteTextView.getText().toString());
                LatLng latLng=getLatLngFromAddress(autoCompleteTextView.getText().toString());
                if(latLng!=null) {
                    Log.d("Lat Lng : ", " " + latLng.latitude + " " + latLng.longitude);
                    Address address=getAddressFromLatLng(latLng);

                    if(address!=null) {

                        mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(address.getAddressLine(0)));
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));

                    }
                    else {
                        Log.d("Adddress","Address Not Found");
                    }
                }
                else {
                    Log.d("Lat Lng","Lat Lng Not Found");
                }

            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeView(destinationViewHolder);
            }
        });

        destinationList.addView(destinationViewHolder);

    }

    private void removeView(View view){

        destinationList.removeView(view);
    }

    private void FindDirection() {

        LatLng currentLatLng=getLatLngFromAddress(txt_current.getText().toString());
        Address currentAddress=getAddressFromLatLng(currentLatLng);

        LatLng destinationLatLng=new LatLng(searchLatitude, searchLongitude);
        Address destinationAddress=getAddressFromLatLng(destinationLatLng);

        destinationPlaceList.clear();
        if (destinationList!=null) {
            for (int i = 0; i < destinationList.getChildCount(); i++) {

                View destinationViewHolder = destinationList.getChildAt(i);

                AutoCompleteTextView editTextName = (AutoCompleteTextView) destinationViewHolder.findViewById(R.id.txt_addDestination);

                LatLng latLng=getLatLngFromAddress(editTextName.getText().toString());

                Address address=getAddressFromLatLng(latLng);

                LatLng placeLatLng = new LatLng(address.getLatitude(), address.getLongitude());

                destinationPlaceList.add(placeLatLng);

            }
        }

        current = new MarkerOptions().position(new LatLng(currentAddress.getLatitude(), currentAddress.getLongitude())).title("Current Location");
        destination = new MarkerOptions().position(new LatLng(searchLatitude, searchLongitude)).title(destinationAddress.getAddressLine(0));

        mGoogleMap.addMarker(destination);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(searchLatitude, searchLongitude)));
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(searchLatitude, searchLongitude), 14));

        new FetchURL(MapActivity.this).execute(getUrl(current.getPosition(), destination.getPosition(), "driving"), "driving");

    }
    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mGoogleMap.addPolyline((PolylineOptions) values[0]);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==LOCATION_PERMISSION_CODE)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MapActivity.this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MapActivity.this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setUpGoogleMap() {

        if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionOk = false;
            return;
        }
        mGoogleMap.setMyLocationEnabled(false);
        mGoogleMap.getUiSettings().setTiltGesturesEnabled(false);

        setUpLocationUpdate();
    }

    private void setUpLocationUpdate() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    for (Location location : locationResult.getLocations()) {
                        Log.d("TAG", "onLocationResult" + location.getLongitude() + "" + location.getLatitude());
                    }
                }
                super.onLocationResult(locationResult);

            }
        };

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapActivity.this);

        startLocationUpdates();
    }

    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionOk = false;
            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MapActivity.this, "Location updated started", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }
    private void RequestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MapActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            new AlertDialog.Builder(MapActivity.this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MapActivity.this,
                                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(MapActivity.this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        }
    }
    private void getCurrentLocation() {

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapActivity.this);
        if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionOk = false;
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                currentLocation = location;

                LatLng latLng =new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title("Current Location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        .snippet(firebaseAuth.getCurrentUser().getDisplayName());

                if (currentMarker != null) {
                    currentMarker.remove();

                }
                txt_current.setText(getAddressFromLatLng(latLng).getAddressLine(0));

                mGoogleMap.addMarker(markerOptions);
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));


            }
        });
    }

    private void stopLocationUpdate() {
        Log.d("TAG", "stopLocationUpdate: Location Update stop");
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);

    }

    public void onPause() {
        super.onPause();
        if (fusedLocationProviderClient != null) {
            stopLocationUpdate();
        }
    }

    public void onResume() {
        super.onResume();

        if (fusedLocationProviderClient != null) {
            startLocationUpdates();

            if (currentMarker != null) {
                currentMarker.remove();
            }
        }

    }
    private Address getAddressFromLatLng(LatLng latLng){
        Geocoder geocoder=new Geocoder(MapActivity.this);
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

        Geocoder geocoder=new Geocoder(MapActivity.this);
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
    @Override
    public void onStart() {
        super.onStart();

        getCurrentLocation();
    }
}
