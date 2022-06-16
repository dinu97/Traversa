package com.example.travelguidapplication.Fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.travelguidapplication.Adapter.PlaceAutoSuggestAdapter;
import com.example.travelguidapplication.AppPermissions;
import com.example.travelguidapplication.GetNearByPlaces;
import com.example.travelguidapplication.MapActivity;
import com.example.travelguidapplication.R;
import com.example.travelguidapplication.Session.CurrentDestinationSession;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    private AppPermissions appPermissions;
    private boolean isLocationPermissionOk, isTrafficEnable;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation;
    private FirebaseAuth firebaseAuth;
    private Marker currentMarker;
    private int ProximityRadius = 10000;
    private Chip nearHospital, nearRestaurant, nearATM, nearGasStation, nearSuperMarket, nearHotels, nearPharmacies, nearCarWash, nearSalons;
    private Button btn_direction;

    private FloatingActionButton btn_currentLocation,btn_mapType,btn_enableTraffic;
    private static final int LOCATION_PERMISSION_CODE=2000;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_home, container, false);
        nearRestaurant = (Chip) view.findViewById(R.id.nearResturant);
        nearHospital = (Chip) view.findViewById(R.id.nearHospitals);
        nearATM = (Chip) view.findViewById(R.id.nearATM);
        nearGasStation = (Chip) view.findViewById(R.id.nearGasStation);
        nearSuperMarket = (Chip) view.findViewById(R.id.nearSuperMarket);
        nearHotels = (Chip) view.findViewById(R.id.nearHotel);
        nearPharmacies = (Chip) view.findViewById(R.id.nearPharmacies);
        nearCarWash = (Chip) view.findViewById(R.id.nearCarWash);
        nearSalons = (Chip) view.findViewById(R.id.nearSalons);

        btn_direction = (Button) view.findViewById(R.id.btnDirection);
        btn_currentLocation = (FloatingActionButton) view.findViewById(R.id.currentLocation);
        btn_mapType = (FloatingActionButton) view.findViewById(R.id.btnMapType);
        btn_enableTraffic = (FloatingActionButton) view.findViewById(R.id.enableTraffic);

        isTrafficEnable=false;
        firebaseAuth = FirebaseAuth.getInstance();
        final AutoCompleteTextView autoCompleteTextView=view.findViewById(R.id.autocomplete);
        autoCompleteTextView.setAdapter(new PlaceAutoSuggestAdapter(getContext(),android.R.layout.simple_list_item_1));

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d("Address : ",autoCompleteTextView.getText().toString());
                LatLng latLng=getLatLngFromAddress(autoCompleteTextView.getText().toString());
                if(latLng!=null) {
                    Log.d("Lat Lng : ", " " + latLng.latitude + " " + latLng.longitude);
                    Address address=getAddressFromLatLng(latLng);

                    if(address!=null) {
                        mGoogleMap.clear();
                        mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(address.getAddressLine(0)));
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                        hideKeyboard();
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

        btn_mapType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View viewPopup = inflater.inflate(R.layout.bottom_sheet_layout_map_type,null);
                TextView btn_default=(TextView)viewPopup.findViewById(R.id.btnDefault);
                TextView btn_satellite=(TextView)viewPopup.findViewById(R.id.btnSatellite);
                TextView btn_terrain=(TextView)viewPopup.findViewById(R.id.btnTerrain);

                final Dialog popupMenu = new Dialog(getContext(), R.style.AppBottomSheetDialogTheme);
                popupMenu.setContentView(viewPopup);
                popupMenu.setCancelable(true);
                popupMenu.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                popupMenu.getWindow().setGravity(Gravity.CENTER);
                popupMenu.show();

                btn_default.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        popupMenu.cancel();
                    }
                });

                btn_satellite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        popupMenu.cancel();
                    }
                });

                btn_terrain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        popupMenu.cancel();
                    }
                });



            }
        });

        btn_enableTraffic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTrafficEnable) {
                    if (mGoogleMap != null) {
                        mGoogleMap.setTrafficEnabled(false);
                        isTrafficEnable = false;
                    }
                } else {
                    if (mGoogleMap != null) {
                        mGoogleMap.setTrafficEnabled(true);
                        isTrafficEnable = true;
                    }
                }
            }
        });

        btn_direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if(autoCompleteTextView.getText().toString().isEmpty())
                    {
                        int blue = Color.parseColor("#2f3ced");
                        new StyleableToast.Builder(getContext())
                                .text("Search field empty")
                                .backgroundColor(blue)
                                .solidBackground()
                                .textColor(Color.WHITE)
                                .gravity(Gravity.BOTTOM)
                                .cornerRadius(50)
                                .textSize(12)
                                .show();
                    }
                    else
                    {
                        LatLng latLng=getLatLngFromAddress(autoCompleteTextView.getText().toString());

                        Intent intent=new Intent(getContext(), MapActivity.class);

                        CurrentDestinationSession currentDestinationSession =new CurrentDestinationSession(getContext());
                        currentDestinationSession.writeCurrentDestination(String.valueOf(latLng.latitude),String.valueOf(latLng.longitude));

                        startActivity(intent);

                    }
                }
                else {
                    RequestLocationPermission();
                }

            }
        });

        nearHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String hospital = "hospital";

                NearBySearch(hospital);
            }
        });

        nearRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String restaurant = "restaurant";
                NearBySearch(restaurant);

            }
        });

        nearATM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String atm = "atm";
                NearBySearch(atm);

            }
        });

        nearGasStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String gas_station = "gas_station";
                NearBySearch(gas_station);

            }
        });

        nearSuperMarket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String supermarket = "supermarket";
                NearBySearch(supermarket);

            }
        });

        nearHotels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String hotel = "hotel";
                NearBySearch(hotel);

            }
        });

        nearPharmacies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String pharmacy = "pharmacy";
                NearBySearch(pharmacy);

            }
        });

        nearCarWash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String car_wash = "car_wash";
                NearBySearch(car_wash);

            }
        });

        nearSalons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String beauty_salon = "beauty_salon";
                NearBySearch(beauty_salon);

            }
        });


        btn_currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                }
                else {
                    RequestLocationPermission();
                }

            }
        });

        return view;
    }


    private Address getAddressFromLatLng(LatLng latLng){
        Geocoder geocoder=new Geocoder(getContext());
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

        Geocoder geocoder=new Geocoder(getContext());
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.homeMap);

        mapFragment.getMapAsync(this);

    }

    private void NearBySearch(String nearPlace) {

        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mGoogleMap.clear();

            FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                isLocationPermissionOk = false;
                return;
            }
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    Object transferData[] = new Object[2];
                    GetNearByPlaces getNearByPlaces = new GetNearByPlaces();

                    String url = getUrl(location.getLatitude(), location.getLongitude(), nearPlace);
                    transferData[0] = mGoogleMap;
                    transferData[1] = url;

                    getNearByPlaces.execute(transferData);
                    Toast.makeText(getContext(), "Searching place", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getContext(), "Showing Nearby place", Toast.LENGTH_SHORT).show();

                }
            });
        }
        else {
            RequestLocationPermission();
        }


    }

    private String getUrl(double latitude, double longitude, String nearByPlace) {
        StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleURL.append("location=" + latitude + "," + longitude);
        googleURL.append("&radius=" + ProximityRadius);
        googleURL.append("&type=" + nearByPlace);
        googleURL.append("&sensor=true");
        googleURL.append("API_KEY");

        return googleURL.toString();

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;

        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionOk = true;
            setUpGoogleMap();
        }
        else {
            RequestLocationPermission();
        }

    }

    private void RequestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            new AlertDialog.Builder(getContext())
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(getActivity(),
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
            ActivityCompat.requestPermissions(getActivity(),
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==LOCATION_PERMISSION_CODE)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void hideKeyboard() {

        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void setUpGoogleMap() {

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        startLocationUpdates();
    }

    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionOk = false;
            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireContext(), "Location updated started", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
        getCurrentLocation();
    }

    private void getCurrentLocation() {

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    @Override
    public void onStart() {
        super.onStart();
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

                mGoogleMap.addMarker(markerOptions);
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));

            }
        });
    }


}