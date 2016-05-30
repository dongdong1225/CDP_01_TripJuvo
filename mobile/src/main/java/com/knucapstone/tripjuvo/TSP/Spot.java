package com.knucapstone.tripjuvo.TSP;

import android.location.Location;

/**
 * Created by JYM on 2016-05-15.
 */
public class Spot {
    private int id;
    private Location location;
//    private String name;
//    private double latitude;
//    private double longitude;


    public Spot() {

    }

    public Spot(int id, String name, double latitude, double longitude) {
        super();
        this.id = id;
        location = new Location(name);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
//        this.name = name;
//        this.latitude = latitude;
//        this.longitude = longitude;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}