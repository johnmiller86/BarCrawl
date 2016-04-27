package com.example.crawlapp.barcrawl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper{

    // Defining the database and table
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Bar_Crawl";
    private static final String TAG = DBHelper.class.getSimpleName().toString();

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //All necessary tables you like to create will create here
        db.execSQL(UserRepo.createTable());
        db.execSQL(CrawlRepo.createTable());
        db.execSQL(PlaceRepo.createTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, String.format("SQLiteDatabase.onUpgrade(%d -> %d)", oldVersion, newVersion));

        // Drop table if existed, all data will be gone!!!
        db.execSQL("DROP TABLE IF EXISTS " + User.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Crawl.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Place.TABLE);
        onCreate(db);
    }
}