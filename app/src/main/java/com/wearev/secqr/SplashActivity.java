package com.wearev.secqr;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(()-> playSound(R.raw.homemessage), 2500);

        new Handler().postDelayed(() -> {
            SharedPreferences pref = getSharedPreferences("login",MODE_PRIVATE);
            boolean check = pref.getBoolean("category",false);

            Intent iNext;
            if (check){
                iNext = new Intent(SplashActivity.this, MainActivity.class);
            }
            else {
                iNext = new Intent(SplashActivity.this, AuthActivity.class);
            }
            startActivity(iNext);
            finish();
        },3600);
    }
    private void playSound(int id) {
        MediaPlayer mediaPlayer = MediaPlayer.create(this,id);
        mediaPlayer.start();
    }
}