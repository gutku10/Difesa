package com.tika.app2.Login;

import com.google.firebase.database.PropertyName;

public class Location {

    private String longitude;
    private String latitude;


    @PropertyName("Longitude")
    public String getLongitude() {
        return longitude;
    }

    @PropertyName("Longitude")

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @PropertyName("Latitude")
    public String getLatitude() {
        return latitude;
    }


    @PropertyName("Latitude")
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}
