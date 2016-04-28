package com.example.crawlapp.barcrawl;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class NewCrawlActivity extends AppCompatActivity {

    // UI Components
    private EditText crawlNameEditText;
    private DatePicker datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_crawl);

        // Linking UI Components
        crawlNameEditText = (EditText) findViewById(R.id.editTextCrawlName);
        datePicker = (DatePicker) findViewById(R.id.datePicker);
    }

    public void continueOn(View view){

        // Validating input
        if (crawlNameEditText.getText().toString() == null || crawlNameEditText.getText().toString().equals("")){
            Toast.makeText(NewCrawlActivity.this, "You must choose a crawl name!!", Toast.LENGTH_SHORT).show();
        }
//        else if (!passwordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString())){
//            Toast.makeText(RegisterActivity.this, "Password and confirmation do not match!!", Toast.LENGTH_SHORT).show();
//        }
//        else{
//
//            Crawl crawl = new Crawl();
//            CrawlRepo crawlRepo = new CrawlRepo();
//
//            // Configuring crawl
//            crawl.setUsername(usernameEditText.getText().toString());
//            crawl.setPassword(passwordEditText.getText().toString());
//
//            // Inserting
//            crawlRepo.insert(crawl, LoginActivity.user);
//
//            // Finished registering, exit
//            finish();
//        }
        UserRepo userRepo = new UserRepo();
        int userId = userRepo.getUserId(LoginActivity.user.getUsername());
        Toast.makeText(this, userId + "", Toast.LENGTH_SHORT).show();
    }
}
