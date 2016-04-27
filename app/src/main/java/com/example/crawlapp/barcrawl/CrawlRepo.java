package com.example.crawlapp.barcrawl;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class CrawlRepo{

    private Crawl crawl;

    public CrawlRepo(){
        crawl = new Crawl();
    }


    public static String createTable(){
        return "CREATE TABLE " + Crawl.TABLE  + "("
                + Crawl.KEY_CRAWL_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + User.KEY_USER_ID + " INTEGER, "
                + Crawl.KEY_CRAWL_NAME + " TEXT, "
                + Crawl.KEY_CRAWL_DATE + " TEXT, "
                + "FOREIGN KEY(" + User.KEY_USER_ID + ") REFERENCES " + User.TABLE + "(" + User.KEY_USER_ID + "))";
    }


    public int insert(Crawl crawl, User user) {
        int crawlId;
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(User.KEY_USER_ID, user.getUserId());
        values.put(Crawl.KEY_CRAWL_NAME, crawl.getCrawlName());
        values.put(Crawl.KEY_CRAWL_DATE, crawl.getCrawlDate());

        // Inserting Row
        crawlId = (int) db.insert(Crawl.TABLE, null, values);
        DatabaseManager.getInstance().closeDatabase();

        return crawlId;
    }

    public void delete() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(Crawl.TABLE,null,null);
        DatabaseManager.getInstance().closeDatabase();
    }
}