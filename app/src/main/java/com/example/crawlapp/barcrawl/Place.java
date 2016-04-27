package com.example.crawlapp.barcrawl;

public class Place {

    public static final String TAG = Place.class.getSimpleName();
    public static final String TABLE = "places";
    public static final String KEY_PLACE_ID = "placeId";
    public static final String KEY_PLACE_NAME = "place_name";
    public static final String KEY_PLACE_LAT = "place_lat";
    public static final String KEY_PLACE_LNG = "place_lng";

    private String crawlId, crawlName, crawlDate;


    public String getCrawlId() {
        return crawlId;
    }

    public void setCrawlId(String crawlId) {
        this.crawlId = crawlId;
    }

    public String getCrawlName() {
        return crawlName;
    }

    public void setCrawlName(String crawlName) {
        this.crawlName = crawlName;
    }

    public String getCrawlDate() {
        return crawlDate;
    }

    public void setCrawlDate(String crawlDatea) {
        this.crawlDate = crawlDate;
    }
}