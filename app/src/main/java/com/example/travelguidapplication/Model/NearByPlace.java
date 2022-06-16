package com.example.travelguidapplication.Model;

public class NearByPlace {

    private String nearByPlaceName,nearByPlaceImage,nearByPlaceId;
    private String nearByPlaceLatitude,nearByPlaceLongitude;
    private String nearByPlaceRating;
    private boolean isChecked;

    public NearByPlace(String nearByPlaceId,String nearByPlaceName, String nearByPlaceRating, String nearByPlaceLatitude,String nearByPlaceLongitude,String nearByPlaceImage) {
        this.nearByPlaceId = nearByPlaceId;
        this.nearByPlaceName = nearByPlaceName;
        this.nearByPlaceRating=nearByPlaceRating;
        this.nearByPlaceLatitude=nearByPlaceLatitude;
        this.nearByPlaceLongitude=nearByPlaceLongitude;
        this.nearByPlaceImage=nearByPlaceImage;


    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getNearByPlaceId() {
        return nearByPlaceId;
    }

    public void setNearByPlaceId(String nearByPlaceId) {
        this.nearByPlaceId = nearByPlaceId;
    }

    public String getNearByPlaceName() {
        return nearByPlaceName;
    }

    public void setNearByPlaceName(String nearByPlaceName) {
        this.nearByPlaceName = nearByPlaceName;
    }

    public String getNearByPlaceRating() {
        return nearByPlaceRating;
    }

    public void setNearByPlaceRating(String nearByPlaceRating) {
        this.nearByPlaceRating = nearByPlaceRating;
    }

    public String getNearByPlaceImage() {
        return nearByPlaceImage;
    }

    public void setNearByPlaceImage(String nearByPlaceImage) {
        this.nearByPlaceImage = nearByPlaceImage;
    }

    public String getNearByPlaceLatitude() {
        return nearByPlaceLatitude;
    }

    public void setNearByPlaceLatitude(String nearByPlaceLatitude) {
        this.nearByPlaceLatitude = nearByPlaceLatitude;
    }

    public String getNearByPlaceLongitude() {
        return nearByPlaceLongitude;
    }

    public void setNearByPlaceLongitude(String nearByPlaceLongitude) {
        this.nearByPlaceLongitude = nearByPlaceLongitude;
    }
}
