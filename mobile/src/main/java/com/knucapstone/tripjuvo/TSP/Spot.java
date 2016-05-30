package com.knucapstone.tripjuvo.TSP;

import android.location.Location;

import java.io.Serializable;

/**
 * Created by JYM on 2016-05-15.
 */
public class Spot extends Location implements Serializable{
//    private int id;
//    private Location location;
    private String picture;
//    private String name;
//    private double latitude;
//    private double longitude;
//
//
    public Spot(String provider)
    {
        super(provider);
    }

    public Spot(String provider, double latitude, double longitude, String picture)
    {
        super(provider);
        this.setLatitude(latitude);
        this.setLongitude(longitude);
        this.picture = picture;
    }

    public Spot(Location location, String picture)
    {
        super(location);
        this.picture = picture;
    }

//    protected Spot(Parcel in) {
//        super(in);
//        picture = in.readString();
//    }

//    @Override
//    public void writeToParcel(Parcel dest, int flags)
//    {
//        super.writeToParcel(dest, flags);
//        dest.writeString(picture);
//    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    public static final Parcelable.Creator<Spot> CREATOR = new Creator<Spot>() {
//        @Override
//        public Spot createFromParcel(Parcel in)
//        {
//            String provider = in.readString();
//            Spot spot = new Spot(provider);
//            spot.picture = in.readString();
//            return spot;
//        }
//
//        @Override
//        public Spot[] newArray(int size) {
//            return new Spot[size];
//        }
//    };

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }

}