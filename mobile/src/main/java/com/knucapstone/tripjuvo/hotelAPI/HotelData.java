package com.knucapstone.tripjuvo.hotelAPI;

/**
 * Created by leedonghee on 16. 5. 16..
 */
public class HotelData {
    String hotel_name;
    String type;
    String hotel_id;
    String address;
    String district;
    String number_of_rooms;
    String popularity_desc;
    String latitude;
    String longitute;
    String imageUrl;

    public HotelData(String hotel_name, String type, String hotel_id, String address, String district, String number_of_rooms, String popularity_desc, String latitude, String longitute, String imageUrl) {
        this.hotel_name = hotel_name;
        this.type = type;
        this.hotel_id = hotel_id;
        this.address = address;
        this.district = district;
        this.number_of_rooms = number_of_rooms;
        this.popularity_desc = popularity_desc;
        this.latitude = latitude;
        this.longitute = longitute;
        this.imageUrl = imageUrl;
    }
    HotelData() {
    }
}
