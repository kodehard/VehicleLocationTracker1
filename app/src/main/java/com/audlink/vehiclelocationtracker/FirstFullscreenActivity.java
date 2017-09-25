package com.audlink.vehiclelocationtracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.audlink.vehiclelocationtracker.SavesessionData.SessionData;
public class FirstFullscreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_fullscreen);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                if (SessionData.getSessionDataInstance().getLoginResult() != null ) {
                    Intent mainIntent = new Intent(FirstFullscreenActivity.this,MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                } else {
                    Intent mainIntent = new Intent(FirstFullscreenActivity.this,DriverLoginActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
            }
        }, 2500);
    }
}
