package com.example.crawlapp.barcrawl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{

    // Defining the database and table
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Bar_Crawl";
    private static final String DATABASE_TABLE_USERS = "users";
    private static final String DATABASE_TABLE_CRAWLS = "crawls";

    // Defining the column names for the users table
    private static final String USER_ID = "userId";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    // Defining the column names for the users table
    private static final String CRAWL_NAME = "crawl_name";
    private static final String CRAWL_DATE = "crawl_date";


    private int taskCount;

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){

        String userTable =
                "CREATE TABLE " + DATABASE_TABLE_USERS + "("
                        + USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + USERNAME + " TEXT, "
                        + PASSWORD + " TEXT" + ")";
        db.execSQL(userTable);

        String crawlTable =
                "CREATE TABLE " + DATABASE_TABLE_CRAWLS + "("
                        + USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + CRAWL_NAME + " TEXT, "
                        + CRAWL_DATE + " DATE" + ")";
        db.execSQL(crawlTable);

        String placesTable =
                "CREATE TABLE " + DATABASE_TABLE_CRAWLS + "("
                        + USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + CRAWL_NAME + " TEXT, "
                        + CRAWL_DATE + " DATE" + ")";
        db.execSQL(crawlTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion){

        // Drop old tables if exists
        database.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_USERS);
        database.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_CRAWLS);


        // Recreate tables
        onCreate(database);
    }
}