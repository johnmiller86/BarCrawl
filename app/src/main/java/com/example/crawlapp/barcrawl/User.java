package com.example.crawlapp.barcrawl;

public class User {

    public static final String TAG = User.class.getSimpleName();
    public static final String TABLE = "users";
    public static final String KEY_USER_ID = "id";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";

    private String id, username, password;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserame() {
        return username;
    }

    public void setUsernameame(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}