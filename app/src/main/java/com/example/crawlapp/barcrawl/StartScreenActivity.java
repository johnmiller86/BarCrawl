package com.example.crawlapp.barcrawl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class StartScreenActivity extends AppCompatActivity {

    private TextView welcomeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
    }

    public void CreateCrawl(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void LoadCrawl(View view) {

        // Get Data........ Put extra......

        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

}
