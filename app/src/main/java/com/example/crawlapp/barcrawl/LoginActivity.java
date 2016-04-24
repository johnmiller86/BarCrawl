package com.example.crawlapp.barcrawl;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity{

    private CallbackManager callbackManager;
    private TextView info;
    private ImageView profileImgView;
    private LoginButton facebookLoginButton;
    private Button loginButton;
    private PrefUtil prefUtil;
    private IntentUtil intentUtil;
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
        intentUtil = new IntentUtil(this);

        info = (TextView) findViewById(R.id.info);
        //profileImgView = (ImageView) findViewById(R.id.profile_img);
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

        // Generating Facebook Keyhash..
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.crawlapp.barcrawl", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void Login(View view) {

        if(editText_username.getText().toString().equals("")&& editText_password.getText().toString().equals("")){
            Toast.makeText(LoginActivity.this, "Username and Password are correct", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, StartScreenActivity.class);
            startActivity(intent);
        }
        else{
            if (attemptsCount == 0) {
                facebookLoginButton.setEnabled(false);
                loginButton.setEnabled(false);
                Toast.makeText(LoginActivity.this, "You have been locked out due to too many incorrect attempts!", Toast.LENGTH_SHORT).show();
            }
            else {
                attemptsCount--;
                Toast.makeText(LoginActivity.this, "Username and Password are incorrect! You have " + attemptsCount + " attempts remaining.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        deleteAccessToken();
        Profile profile = Profile.getCurrentProfile();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private String message(Profile profile) {
        StringBuilder stringBuffer = new StringBuilder();
        if (profile != null) {
            stringBuffer.append("Welcome ").append(profile.getName());
        }
        return stringBuffer.toString();
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

