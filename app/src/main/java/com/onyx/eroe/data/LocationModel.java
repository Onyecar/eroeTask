package com.onyx.eroe.data;

import android.location.Location;

/**
 * Created by onyekaanene on 23/03/2018.
 */

public class LocationModel {
    private int id;
    private String location;
    private String locationName;
    private double latitude;
    private double longitude;

    public LocationModel(int id, String location, String locationName, double latitude, double longitude){
        this.id = id;
        this.location=location;
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;

    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

}
