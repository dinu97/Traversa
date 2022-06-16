package com.example.travelguidapplication.Interface;

import com.example.travelguidapplication.Model.Route;

import java.util.List;

public interface DirectionFinderRouteListener {
    void onDirectionFinderRouteStart();
    void onDirectionFinderRouteSuccess(List<Route> route);
}
