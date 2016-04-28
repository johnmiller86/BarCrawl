package com.example.crawlapp.barcrawl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

class UserRepo {

    private final User user;

    public UserRepo(){
        user = new User();
    }


    public static String createTable(){
        return "CREATE TABLE " + User.TABLE  + "("
                + User.KEY_USER_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + User.KEY_USERNAME + " TEXT, "
                + User.KEY_PASSWORD + " TEXT)";
    }


    public int insert(User user) {
        int userId;
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(User.KEY_USERNAME, user.getUsername());
        values.put(User.KEY_PASSWORD, user.getPassword());

        // Inserting Row
        userId = (int) db.insert(User.TABLE, null, values);
        DatabaseManager.getInstance().closeDatabase();

        return userId;
    }

    public boolean userExists(String username){
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + User.TABLE + " WHERE " + User.KEY_USERNAME + "=?", new String[]{username});
        if (cursor != null && cursor.getCount() > 0){
            return true;
        }
        assert cursor != null;
        cursor.close();
        return false;
    }

    public boolean passwordCorrect(String username, String password){
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + User.TABLE + " WHERE " + User.KEY_USERNAME + "=? AND " + User.KEY_PASSWORD + "=?", new String[]{username, password});
        if (cursor != null && cursor.getCount() > 0){
            return true;
        }
        assert cursor != null;
        cursor.close();
        return false;
    }

    public int getUserId(String username){

        int userId = -1;
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery("SELECT " + User.KEY_USER_ID + " FROM " + User.TABLE + " WHERE " + User.KEY_USERNAME + "=?", new String[]{username});
        if (cursor.moveToFirst()){
            userId = cursor.getInt(cursor.getColumnIndex(User.KEY_USER_ID));
        }
        cursor.close();
        return userId;
    }



    public void delete() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(User.TABLE,null,null);
        DatabaseManager.getInstance().closeDatabase();
    }
}