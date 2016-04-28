package com.example.crawlapp.barcrawl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

class PlaceRepo{

    private final Place place;

    public PlaceRepo(){
        place = new Place();
    }


    public static String createTable(){
        return "CREATE TABLE " + Place.TABLE  + "("
                + Place.KEY_PLACE_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Place.KEY_USER_ID + " INTEGER,"
                + Place.KEY_CRAWL_NAME + " TEXT, "
                + Place.KEY_PLACE_NAME + " TEXT, "
                + Place.KEY_PLACE_LAT + " TEXT, "
                + Place.KEY_PLACE_LNG + " TEXT, "
                + "FOREIGN KEY(" + User.KEY_USER_ID + ") REFERENCES " + User.TABLE + "(" + User.KEY_USER_ID + "))";
    }


    public int insert(Place place, User user, String crawlName) {
        int placeId;
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(Place.KEY_USER_ID, user.getUserId());
        values.put(Place.KEY_CRAWL_NAME, crawlName);
        values.put(Place.KEY_PLACE_NAME, place.getPlaceName());
        values.put(Place.KEY_PLACE_LAT, place.getPlaceLat());
        values.put(Place.KEY_PLACE_LNG, place.getPlaceLng());

        // Inserting Row
        placeId = (int) db.insert(Place.TABLE, null, values);
        DatabaseManager.getInstance().closeDatabase();
        return placeId;
    }

    public void delete() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(Place.TABLE,null,null);
        DatabaseManager.getInstance().closeDatabase();
    }

    public ArrayList<com.example.crawlapp.barcrawl.Place> getPlaceList(String crawlName){

        ArrayList<com.example.crawlapp.barcrawl.Place> placeList = new ArrayList<>();
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Place.TABLE + " WHERE " + Place.KEY_USER_ID + "=? AND " + Place.KEY_CRAWL_NAME + "=?", new String[]{String.valueOf(LoginActivity.user.getUserId()), crawlName});

        while (cursor.moveToNext()){
            com.example.crawlapp.barcrawl.Place place = new com.example.crawlapp.barcrawl.Place();
            place.setPlaceName(cursor.getString(cursor.getColumnIndex(Place.KEY_PLACE_NAME)));
            place.setPlaceLat(cursor.getString(cursor.getColumnIndex(Place.KEY_PLACE_LAT)));
            place.setPlaceLng(cursor.getString(cursor.getColumnIndex(Place.KEY_PLACE_LNG)));
            placeList.add(place);
        }
        cursor.close();
        return placeList;
    }
}