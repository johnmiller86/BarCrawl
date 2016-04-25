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

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);
        prefUtil = new PrefUtil(this);
        facebookLoginButton = (LoginButton) findViewById(R.id.facebookButton);
        editText_username = (EditText) findViewById(R.id.editText_username);
        editText_password = (EditText) findViewById(R.id.editText_password);
        loginButton = (Button) findViewById(R.id.button_login);
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                Profile profile = Profile.getCurrentProfile();

                String userId = loginResult.getAccessToken().getUserId();
                String accessToken = loginResult.getAccessToken().getToken();

                // save accessToken to SharedPreference
                prefUtil.saveAccessToken(accessToken);

                String profileImgUrl = "https://graph.facebook.com/" + userId + "/picture?type=large";

                Intent intent = new Intent(LoginActivity.this, StartScreenActivity.class);
                intent.putExtra("USERNAME", profile.getName());
                intent.putExtra("PROFILE_IMAGE_URL", profileImgUrl);
                startActivity(intent);
//
            }

            @Override
            public void onCancel() {}

            @Override
            public void onError(FacebookException e) {
                e.printStackTrace();
            }
        });
    }

    public void Login(View view) {

        boolean authenticated = true;
        if(authenticated){
            Intent intent = new Intent(this, StartScreenActivity.class);
            intent.putExtra("USERNAME", editText_username.getText().toString());
            startActivity(intent);
        }
        else{
            attemptsCount--;
            Toast.makeText(LoginActivity.this, "Username and Password are incorrect! You have " + attemptsCount + " attempts remaining.", Toast.LENGTH_SHORT).show();
        }
        if (attemptsCount == 0) {
            facebookLoginButton.setEnabled(false);
            loginButton.setEnabled(false);
            Toast.makeText(LoginActivity.this, "You have been locked out due to too many incorrect attempts!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        deleteAccessToken();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }



    private void deleteAccessToken() {
        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {

                if (currentAccessToken == null){
                    //User logged out
                    prefUtil.clearToken();
                   // clearUserArea();
                }
            }
        };
    }
}

