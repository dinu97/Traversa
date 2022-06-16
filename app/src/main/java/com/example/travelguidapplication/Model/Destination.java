package com.example.travelguidapplication.Model;

import java.io.Serializable;

public class Destination implements Serializable {
    String destination;

    public Destination() {
    }

    public Destination(String destination) {
        this.destination = destination;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
