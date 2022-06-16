package com.example.travelguidapplication.Interface;

import com.example.travelguidapplication.Model.Route;

import java.util.List;

public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route, String placeKey);
}
