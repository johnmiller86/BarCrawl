package com.example.crawlapp.barcrawl;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class NewCrawlActivity extends AppCompatActivity {

    // UI Components
    private EditText crawlNameEditText;
    private DatePicker datePicker;
    private static int y, m, d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_crawl);

        // Linking UI Components
        crawlNameEditText = (EditText) findViewById(R.id.editTextCrawlName);
    }

    public void showDatePicker(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }



    public void continueOn(View view){

        // Validating input

        if (crawlNameEditText.getText().toString().equals("")){
            Toast.makeText(NewCrawlActivity.this, "You must choose a crawl name!!", Toast.LENGTH_SHORT).show();
        }
        else if (d == 0 || m == 0 || y == 0){
            Toast.makeText(this, "You must choose a crawl date!!", Toast.LENGTH_SHORT).show();
        }
        else{
            Crawl crawl = new Crawl();
            CrawlRepo crawlRepo = new CrawlRepo();
            UserRepo userRepo = new UserRepo();

            // Configuring crawl
            crawl.setCrawlName(crawlNameEditText.getText().toString());
            crawl.setCrawlDate(m + "/" + d + "/" + y);

            int userId = userRepo.getUserId(LoginActivity.user.getUsername());

            // Ensuring userId was retrieved successfully
            if (userId == -1){
                Toast.makeText(this, "An internal error occurred :(", Toast.LENGTH_SHORT).show();
            }
            else{
                // Setting userId
                LoginActivity.user.setUserId(userId);

                // Inserting
                crawlRepo.insert(crawl, LoginActivity.user);

                // MapsActivity Intent
                Intent intent = new Intent(this, MapsActivity.class);

                // Launching MapsActivity
                startActivity(intent);
            }
        }
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            y = year;
            m = month;
            d = day;
        }
    }
}
