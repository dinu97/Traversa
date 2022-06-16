package com.example.travelguidapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.travelguidapplication.DirectionHelpers.FetchURL;
import com.example.travelguidapplication.DirectionHelpers.TaskLoadedCallback;
import com.example.travelguidapplication.Model.Plan;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class PlanPlaceMapActivity extends AppCompatActivity  implements OnMapReadyCallback,TaskLoadedCallback {

    private GoogleMap mGoogleMap;
    private boolean isLocationPermissionOk;
    private ArrayList<Plan> placeList;
    private Polyline currentPolyline;
    private String userId,startDate;
    private MarkerOptions current, destination,waypointsDestination;
    private static final int LOCATION_PERMISSION_CODE=2000;
    private AppPermissions appPermissions;
    private Location currentLocation;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker currentMarker;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_place_map);

        placeList=new ArrayList<>();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser refUser = firebaseAuth.getCurrentUser();
        userId = refUser.getUid();

        startDate = getIntent().getStringExtra("startDate");

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.planPlaceMap);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        if (ContextCompat.checkSelfPermission(PlanPlaceMapActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionOk = true;
            setUpGoogleMap();
        }
        else {
            RequestLocationPermission();
        }
    }

    private String getUrl(ArrayList<Plan> placeList, String directionMode) {

        LatLng currentLatLng=new LatLng(placeList.get(0).getPlaceLatitude(), placeList.get(0).getPlaceLongitude());
        LatLng destinationLatLng=new LatLng(placeList.get(1).getPlaceLatitude(), placeList.get(1).getPlaceLongitude());
        current = new MarkerOptions().position(currentLatLng).title(placeList.get(0).getPlaceName());
        destination = new MarkerOptions().position(destinationLatLng).title(placeList.get(1).getPlaceName());

        mGoogleMap.addMarker(current);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14));


        mGoogleMap.addMarker(destination);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(destinationLatLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destinationLatLng, 14));
        // Origin of route
        String str_origin = "origin=" + placeList.get(0).getPlaceLatitude() + "," + placeList.get(0).getPlaceLongitude();
        // Destination of route
        String str_dest = "destination=" + placeList.get(1).getPlaceLatitude() + "," + placeList.get(1).getPlaceLongitude();
        // Waypoints
        String waypoints = "";
        for(int i=2;i<placeList.size();i++){
            if(i==2)
            waypoints = "waypoints=";
            waypoints += placeList.get(i).getPlaceLatitude() + "," + placeList.get(i).getPlaceLongitude() + "|";

            LatLng wayPointLatLng=new LatLng(placeList.get(i).getPlaceLatitude(), placeList.get(i).getPlaceLongitude());
            waypointsDestination = new MarkerOptions().position(wayPointLatLng).title(placeList.get(i).getPlaceName());
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

    private void FindDirection(String userId, String startDate) {

            DatabaseReference placeRef= FirebaseDatabase.getInstance().getReference().child("Users").child(this.userId).child("TravelPlans");

            placeRef.orderByChild("startDate").equalTo(this.startDate).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    placeList.clear();
                    for (DataSnapshot ds:snapshot.getChildren())
                    {

                        Plan plan=ds.getValue(Plan.class);
                        placeList.add(plan);

                    }

                    placeList.sort((o1, o2)
                            -> o1.getPlaceId().compareTo(
                            o2.getPlaceId()));

                    new FetchURL(PlanPlaceMapActivity.this).execute(getUrl(placeList, "driving"), "driving");

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

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
                Toast.makeText(PlanPlaceMapActivity.this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PlanPlaceMapActivity.this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void RequestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(PlanPlaceMapActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            new AlertDialog.Builder(PlanPlaceMapActivity.this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(PlanPlaceMapActivity.this,
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
            ActivityCompat.requestPermissions(PlanPlaceMapActivity.this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        }
    }

    private void setUpGoogleMap() {

        if (ActivityCompat.checkSelfPermission(PlanPlaceMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(PlanPlaceMapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(PlanPlaceMapActivity.this);

        startLocationUpdates();
    }

    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(PlanPlaceMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(PlanPlaceMapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionOk = false;
            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(PlanPlaceMapActivity.this, "Location updated started", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    private void getCurrentLocation() {

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(PlanPlaceMapActivity.this);
        if (ActivityCompat.checkSelfPermission(PlanPlaceMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(PlanPlaceMapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionOk = false;
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                currentLocation = location;

                LatLng latLng =new LatLng(location.getLatitude(), location.getLongitude());

                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                FindDirection(userId,startDate);


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

    @Override
    public void onStart() {
        super.onStart();

        getCurrentLocation();
    }



}
