package com.example.crawlapp.barcrawl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

class CrawlRepo{

    public CrawlRepo(){
        Crawl crawl = new Crawl();
    }


    public static String createTable(){
        return "CREATE TABLE " + Crawl.TABLE  + "("
                + Crawl.KEY_CRAWL_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Crawl.KEY_USER_ID + " INTEGER, "
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

    public ArrayList<String> getCrawlList(int userId){

        ArrayList<String> crawlList = new ArrayList<>();
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery("SELECT " + Crawl.KEY_CRAWL_NAME + " FROM " + Crawl.TABLE + " WHERE " + Crawl.KEY_USER_ID + "=?", new String[]{String.valueOf(userId)});
        while (cursor.moveToNext()){
            crawlList.add(cursor.getString(cursor.getColumnIndex(Crawl.KEY_CRAWL_NAME)));
        }
        cursor.close();
        return crawlList;
    }
}