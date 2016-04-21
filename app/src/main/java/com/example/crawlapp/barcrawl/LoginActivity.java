package com.example.crawlapp.barcrawl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jdm5908_bw.ist402.barcrawl.StartScreenActivity;

public class LoginActivity extends AppCompatActivity{

    private EditText editText_username;
    private EditText editText_password;
    private Button loginButton;
    int attemptsCount = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editText_username= (EditText)findViewById(R.id.editText_username);
        editText_password= (EditText)findViewById(R.id.editText_password);
        loginButton = (Button)findViewById(R.id.button_login);
    }

    public void Login(View view) {

        if(editText_username.getText().toString().equals("")&& editText_password.getText().toString().equals("")){
            Toast.makeText(LoginActivity.this, "Username and Password are correct", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, StartScreenActivity.class);
            startActivity(intent);
        }
        else{
            if (attemptsCount == 0) {
                loginButton.setEnabled(false);
                Toast.makeText(LoginActivity.this, "You have been locked out due to too many incorrect attempts!", Toast.LENGTH_SHORT).show();
            }
            else {
                attemptsCount--;
                Toast.makeText(LoginActivity.this, "Username and Password are incorrect! You have " + attemptsCount + " attempts remaining.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

