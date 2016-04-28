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

    // Database
    private static DBHelper dbHelper;
    public static User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initializing Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);

        // SharedPreferences to keep user logged in
        prefUtil = new PrefUtil(this);

        // Database Manager
        dbHelper = new DBHelper(this.getApplicationContext());
        DatabaseManager.initializeInstance(dbHelper);

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

                // Configuring
                // TODO Make if exists function
//                user = new User();
//                UserRepo userRepo = new UserRepo();
//                user.setUsername(userId);
//                user.setPassword("");
//
//                // Inserting
//                userRepo.insert(user);

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

        user = new User();
        UserRepo userRepo = new UserRepo();

        user.setUsername(editText_username.getText().toString());
        user.setPassword(editText_password.getText().toString());

        // Verifying user exists
        if (!userRepo.userExists(user.getUsername())){

            // User not found
            Toast.makeText(this, "User not found!!", Toast.LENGTH_SHORT).show();
        }

        // Verifying password is correct
        else if (!userRepo.passwordCorrect(user.getUsername(), user.getPassword())){

            // Incorrect password
            Toast.makeText(LoginActivity.this, "Password is incorrect!!", Toast.LENGTH_SHORT).show();
        }

        // Logging in
        else{

            // Create Intent for StartScreenActivity
            Intent intent = new Intent(this, StartScreenActivity.class);

            // Passing username to StartScreenActivity
            intent.putExtra("USERNAME", user.getUsername());

            // Launching StartScreenActivity
            startActivity(intent);
        }
    }

    public void Register(View view){

        // Create Intent for RegisterActivity
        Intent intent = new Intent(this, RegisterActivity.class);

        // Launching RegisterActivity
        startActivity(intent);
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

