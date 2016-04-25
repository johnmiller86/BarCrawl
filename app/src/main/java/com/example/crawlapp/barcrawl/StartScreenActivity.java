package com.example.crawlapp.barcrawl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class StartScreenActivity extends AppCompatActivity {

    private TextView welcomeTextView;
    private ImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        welcomeTextView = (TextView) findViewById(R.id.textView2);
        profilePic = (ImageView) findViewById(R.id.imageView);

        Intent intent = getIntent();
        // Greeting user
        welcomeTextView.setText("Welcome " + intent.getStringExtra("USERNAME"));

        // If Facebook show picture
        Glide.with(StartScreenActivity.this)
                .load(intent.getStringExtra("PROFILE_IMAGE_URL"))
                .into(profilePic);
    }

    public void CreateCrawl(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void LoadCrawl(View view) {

        // Get Data........ Put extras......

        Intent intent = new Intent(this, MapsActivity.class);
        // TODO put extras
        startActivity(intent);
    }

}
