package com.example.crawlapp.barcrawl;

class Place {

    public static final String TAG = Place.class.getSimpleName();
    public static final String TABLE = "places";
    public static final String KEY_PLACE_ID = "placeId";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_PLACE_NAME = "place_name";
    public static final String KEY_PLACE_LAT = "place_lat";
    public static final String KEY_PLACE_LNG = "place_lng";

    private String placeId, placeName, placeLat, placeLng;


    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceLat() {
        return placeLat;
    }

    public void setPlaceLat(String placeLat) {
        this.placeLat = placeLat;
    }

    public String getPlaceLng() {
        return placeLng;
    }

    public void setPlaceLng(String placeLng) {
        this.placeLng = placeLng;
    }
}