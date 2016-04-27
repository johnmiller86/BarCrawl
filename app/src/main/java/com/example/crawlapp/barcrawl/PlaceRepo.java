package com.example.crawlapp.barcrawl;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class PlaceRepo{

    private Place place;

    public PlaceRepo(){
        place = new Place();
    }


    public static String createTable(){
        return "CREATE TABLE " + Place.TABLE  + "("
                + Place.KEY_PLACE_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + User.KEY_USER_ID + " INTEGER,"
                + Place.KEY_PLACE_NAME + " TEXT, "
                + Place.KEY_PLACE_LAT + " TEXT, "
                + Place.KEY_PLACE_LNG + " TEXT, "
                + "FOREIGN KEY(" + User.KEY_USER_ID + ") REFERENCES " + User.TABLE + "(" + User.KEY_USER_ID + "))";
    }


    public int insert(Place place, User user) {
        int placeId;
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(User.KEY_USER_ID, user.getUserId());
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
}