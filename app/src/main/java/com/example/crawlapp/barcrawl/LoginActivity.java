package com.example.crawlapp.barcrawl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class LoginActivity extends AppCompatActivity{

    // UI/Facebook Components
    private CallbackManager callbackManager;
    private LoginButton facebookLoginButton;
    private Button loginButton;
    private PrefUtil prefUtil;
    private EditText editText_username;
    private EditText editText_password;
    int attemptsCount = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initializing Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);

        // SharedPreferences to keep user logged in
        prefUtil = new PrefUtil(this);

        // Linking UI Components
        facebookLoginButton = (LoginButton) findViewById(R.id.facebookButton);
        editText_username = (EditText) findViewById(R.id.editTextPassword);
        editText_password = (EditText) findViewById(R.id.editTextConfirmPassword);
        loginButton = (Button) findViewById(R.id.button_login);

        // Listener for FacebookLoginButton
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            // User was successfully logged in
            @Override
            public void onSuccess(LoginResult loginResult) {

                // Get their profile
                Profile profile = Profile.getCurrentProfile();

                // Get their userId and accessToken
                String userId = loginResult.getAccessToken().getUserId();
                String accessToken = loginResult.getAccessToken().getToken();

                // Save accessToken to SharedPreferences
                prefUtil.saveAccessToken(accessToken);

                // The link to their profile picture
                String profileImgUrl = "https://graph.facebook.com/" + userId + "/picture?type=large";

                // Creating Intent for the StartScreenActivity (next activity)
                Intent intent = new Intent(LoginActivity.this, StartScreenActivity.class);

                // Passing username and profile picture link to StartScreenActivity
                intent.putExtra("USERNAME", profile.getName());
                intent.putExtra("PROFILE_IMAGE_URL", profileImgUrl);

                // Launching StartScreenActivity
                startActivity(intent);
            }

            // The user cancelled the login, do nothing
            @Override
            public void onCancel() {}

            // There was an error logging in, print out the errors
            @Override
            public void onError(FacebookException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Login without Facebook button listener.
     * @param view the login button.
     */
    public void Login(View view) {

        // Always allowing access for testing purposes
        boolean authenticated = true;

        // TODO store users and passwords to authenticate users

        // When authenticated
        if(authenticated){

            // Create Intent for StartScreenActivity
            Intent intent = new Intent(this, StartScreenActivity.class);

            // Passing username to StartScreenActivity
            intent.putExtra("USERNAME", editText_username.getText().toString());

            // Launching StartScreenActivity
            startActivity(intent);
        }

        // Failed to authenticate
        else{

            // Decrement remaining attempts and prompt user
            attemptsCount--;
            Toast.makeText(LoginActivity.this, "Username and Password are incorrect! You have " + attemptsCount + " attempts remaining.", Toast.LENGTH_SHORT).show();
        }

        // The user has exceeded the attempt limit
        if (attemptsCount == 0) {

            // Disable the buttons and inform the user they have been locked out
            facebookLoginButton.setEnabled(false);
            loginButton.setEnabled(false);
            Toast.makeText(LoginActivity.this, "You have been locked out due to too many incorrect attempts!", Toast.LENGTH_SHORT).show();
        }
    }

    // Delete AccessToken on resume, it may be a different user
    @Override
    public void onResume() {
        super.onResume();
        deleteAccessToken();
    }


    // Handles results from the FacebookLogin Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Deletes the AccessToken upon sign out.
     */
    private void deleteAccessToken() {

        // AccessTokenTracker monitors the AccessToken
        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

                // User logged out
                if (currentAccessToken == null){

                    // Clear token
                    prefUtil.clearToken();
                }
            }
        };
    }
}

