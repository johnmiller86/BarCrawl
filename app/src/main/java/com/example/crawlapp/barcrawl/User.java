package com.example.crawlapp.barcrawl;

public class User {

    public static final String TAG = User.class.getSimpleName();
    public static final String TABLE = "users";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";

    private int userId;
    private String username, password;


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}