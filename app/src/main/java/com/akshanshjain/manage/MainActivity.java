package com.akshanshjain.manage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //This is supposed to be a lock activity for user privacy
        startActivity(new Intent(getApplicationContext(), LandingActivity.class));
    }
}
