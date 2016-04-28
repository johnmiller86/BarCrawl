package com.example.crawlapp.barcrawl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class StartScreenActivity extends AppCompatActivity {

    // UI Components
    private TextView welcomeTextView;
    private ImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        // Linking UI Components
        welcomeTextView = (TextView) findViewById(R.id.textView2);
        profilePic = (ImageView) findViewById(R.id.imageView);

        // Intent to receive login information from LoginActivity
        Intent intent = getIntent();

        // Greeting user
        String greeting = "Welcome " + intent.getStringExtra("USERNAME");
        welcomeTextView.setText(greeting);

        // If Facebook show picture in the ImageView
        Glide.with(StartScreenActivity.this).load(intent.getStringExtra("PROFILE_IMAGE_URL")).into(profilePic);
    }

    /**
     * Create a new crawl button listener.
     * @param view the create crawl button.
     */
    public void CreateCrawl(View view) {

        // MapsActivity Intent
        Intent intent = new Intent(this, NewCrawlActivity.class);

        // Launching MapsActivity
        startActivity(intent);
    }

    /**
     * Loads a previously created crawl.
     * @param view the load crawl button.
     */
    public void LoadCrawl(View view) {

        // MapsActivity Intent
        Intent intent = new Intent(this, MapsActivity.class);
        // TODO Load some data, for Intent extras
        // Launching MapsActivity
        startActivity(intent);
    }

}
