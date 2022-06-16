package com.example.travelguidapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.travelguidapplication.Session.PlaceSession;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

public class ShowMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private int  placeId;
    private float placeLatitude,placeLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.showMap);
        mapFragment.getMapAsync(this);

        PlaceSession placeSession = new PlaceSession(getApplicationContext());
        HashMap<String, String> place = placeSession.readPlaceDetails();

        placeId = Integer.parseInt(place.get(PlaceSession.KEY_PLACEID));
        placeLatitude= Float.parseFloat(place.get(PlaceSession.KEY_PLACELATITUDE));
        placeLongitude= Float.parseFloat(place.get(PlaceSession.KEY_PLACELONGITUDE));

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sydney = new LatLng(placeLatitude, placeLongitude);
        mMap.addMarker(new MarkerOptions().position(sydney).title("location"));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom((sydney), 18), 5000, null);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        mMap.setMyLocationEnabled(true);


    }


}