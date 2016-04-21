package com.example.crawlapp.barcrawl;

import android.app.Activity;
import android.content.Intent;

public class IntentUtil {

    private Activity activity;

    // constructor
    public IntentUtil(Activity activity) {
        this.activity = activity;
    }

    public void showAccessToken() {
        Intent i = new Intent(activity, AccessTokenActivity.class);
        activity.startActivity(i);
    }
}