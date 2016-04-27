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
                + Crawl.KEY_CRAWL_ID  + "   PRIMARY KEY,"
                + Crawl.KEY_CRAWL_NAME + " TEXT,"
                + Crawl.KEY_CRAWL_DATE + " TEXT)";
    }


    public int insert(Crawl crawl) {
        int crawlId;
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(Crawl.KEY_CRAWL_ID, crawl.getCrawlId());
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