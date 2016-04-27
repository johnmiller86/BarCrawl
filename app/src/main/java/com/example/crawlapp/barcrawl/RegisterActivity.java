package com.example.crawlapp.barcrawl;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    // UI Components
    private EditText usernameEditText, passwordEditText, confirmPasswordEditText;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        // Linking UI Components
        usernameEditText = (EditText) findViewById(R.id.editTextUsername);
        passwordEditText = (EditText) findViewById(R.id.editTextPassword);
        confirmPasswordEditText = (EditText) findViewById(R.id.editTextConfirmPassword);
    }



    /**
     * Registers a new account.
     * @param view the search button.
     */
    public void registerAccount(View view){

        // Validating input
         if (usernameEditText.getText().toString() == null || usernameEditText.getText().toString().equals("")){
             Toast.makeText(RegisterActivity.this, "You must choose a username!!", Toast.LENGTH_SHORT).show();
         }
        else if (passwordEditText.getText().toString() == null || passwordEditText.getText().toString().equals("")){
            Toast.makeText(RegisterActivity.this, "You must choose a password!!", Toast.LENGTH_SHORT).show();
        }
        else if (usernameEditText.getText().toString() == null || usernameEditText.getText().toString().equals("")){
            Toast.makeText(RegisterActivity.this, "You must confirm your password!!", Toast.LENGTH_SHORT).show();
        }
        else if (!passwordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString())){
            Toast.makeText(RegisterActivity.this, "Password and confirmation do not match!!", Toast.LENGTH_SHORT).show();
        }
        else{

             // User object
             User user = new User();

             // User Database functions object
             UserRepo userRepo = new UserRepo();

             // Configuring user
             user.setUsername(usernameEditText.getText().toString());
             user.setPassword(passwordEditText.getText().toString());

             // Inserting
             userRepo.insert(user);

             // Finished registering, exit
             finish();
         }
    }
}
