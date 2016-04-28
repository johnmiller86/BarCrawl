package com.example.crawlapp.barcrawl;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
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
        datePicker = (DatePicker) findViewById(R.id.datePicker);
    }

    public void showDatePicker(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }



    public void continueOn(View view){

        Toast.makeText(this, "Year " + y + " Month " + m + " Day " + d,Toast.LENGTH_SHORT).show();
//        // Validating input
//        if (crawlNameEditText.getText().toString() == null || crawlNameEditText.getText().toString().equals("")){
//            Toast.makeText(NewCrawlActivity.this, "You must choose a crawl name!!", Toast.LENGTH_SHORT).show();
//        }
//        else if (!passwordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString())){
//            Toast.makeText(RegisterActivity.this, "Password and confirmation do not match!!", Toast.LENGTH_SHORT).show();
//        }
//        else{
//
//            Crawl crawl = new Crawl();
//            CrawlRepo crawlRepo = new CrawlRepo();
//
//            // Configuring crawl
//            crawl.setCrawlName(crawlNameEditText.getText().toString());
//            crawl.setCrawlDate(datePicker.getd);
//
//            // Inserting
//            crawlRepo.insert(crawl, LoginActivity.user);
//
//            // Finished registering, exit
//            finish();
//        }
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

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
